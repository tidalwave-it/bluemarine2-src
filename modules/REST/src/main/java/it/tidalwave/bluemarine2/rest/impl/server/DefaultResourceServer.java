/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.rest.impl.server;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import org.eclipse.jetty.server.Server;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultResourceServer implements ResourceServer
  {
    @Getter
    private String ipAddress = "";

    @Getter
    private int port;

    private Server server;

    private Path rootPath;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPowerOnNotification (final @ListensTo @Nonnull PowerOnNotification notification)
      throws Exception
      {
        log.info("onPowerOnNotification({})", notification);
        rootPath = notification.getProperties().get(ModelPropertyNames.ROOT_PATH);
        ipAddress = getNonLoopbackIPv4Address().getHostAddress();
        server = new Server(InetSocketAddress.createUnresolved(ipAddress, Integer.getInteger("port", 0)));

        final ServletHolder servletHolder = new ServletHolder(new RangeServlet(rootPath));
        servletHolder.setName("music");
        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(servletHolder, "/Music/*");
        server.setHandler(servletContext);

        server.start();
        port = server.getConnectors()[0].getLocalPort(); // jetty 8
//        port = ((ServerConnector)server.getConnectors()[0]).getLocalPort(); // jetty 9
        log.info(">>>> resource server jetty started at {}:{} serving resources at {}", ipAddress, port, rootPath);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private InetAddress getNonLoopbackIPv4Address()
      throws SocketException
      {
        for (final Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements() ; )
          {
            final NetworkInterface itf =  en.nextElement();

            if (!itf.getName().startsWith("docker"))
              {
                for (final Enumeration<InetAddress> ee = itf.getInetAddresses(); ee.hasMoreElements() ;)
                  {
                    final InetAddress address = ee.nextElement();

                    if (!address.isLoopbackAddress() && (address instanceof Inet4Address))
                      {
                        return address;
                      }
                  }
              }
          }

        log.warn("Returning loopback address!");
        return InetAddress.getLoopbackAddress();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PreDestroy // FIXME: user PowerOffNotification
    private void shutDown()
      throws Exception
      {
        server.stop();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    public String urlForResource (final @Nonnull AudioFile resource)
      {
        final Path path = rootPath.relativize(resource.getPath());
        final String s = StreamSupport.stream(path.spliterator(), false)
                                      .map(p -> urlEncoded(p.toString()))
                                      .collect(Collectors.joining("/"));

        return "http://" + ipAddress + ":" + port + "/" + s;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String urlEncoded (final @Nonnull String string)
      {
        try
          {
            return URLEncoder.encode(string, "UTF-8");
          }
        catch (UnsupportedEncodingException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
