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
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Enumeration;
import java.util.EnumSet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.message.PowerOffNotification;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;

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

    @Inject
    private ApplicationContext applicationContext;

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

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setBaseResource(new ResourceCollection(findWebResources()));
        log.info("RESOURCE BASE: {}", servletContext.getResourceBase());
        servletContext.setContextPath("/");
        servletContext.setWelcomeFiles(new String[] { "index.xhtml" });
        final DelegateWebApplicationContext wac = new DelegateWebApplicationContext(applicationContext, servletContext.getServletContext());
        // FIXME: make this another REST stuff, serving audiofile/urn:....
        servletContext.addServlet(new ServletHolder("music", new RangeServlet(rootPath)), "/Music/*");
        servletContext.addServlet(new ServletHolder("spring", new DispatcherServlet(wac)), "/rest/*");
        servletContext.addServlet(new ServletHolder("default", new DefaultServlet()), "/*");
        servletContext.addFilter(new FilterHolder(new LoggingFilter()), "/*", EnumSet.allOf(DispatcherType.class));
        server.setHandler(servletContext);

        server.start();
        port = server.getConnectors()[0].getLocalPort(); // jetty 8
//        port = ((ServerConnector)server.getConnectors()[0]).getLocalPort(); // jetty 9
        log.info(">>>> resource server jetty started at {}:{} serving resources at {}", ipAddress, port, rootPath);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPowerOffNotification (final @ListensTo @Nonnull PowerOffNotification notification)
      throws Exception
      {
        log.info("onPowerOffNotification({})", notification);
        server.stop();
        server.destroy();
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
     * FIXME: this should go away when musing will be served by the REST music controller.
     *
     ******************************************************************************************************************/
    @Override
    public String urlForResource (final @Nonnull AudioFile resource)
      {
        final String s = StreamSupport.stream(resource.getPath().spliterator(), false)
                                      .map(p -> urlEncoded(p.toString()))
                                      .collect(Collectors.joining("/"));

        return "http://" + ipAddress + ":" + port + "/Music/" + s;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String absoluteUrl (final @Nonnull String type)
      {
        return String.format("http://%s:%d/%s", ipAddress, port, type);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Resource[] findWebResources()
      throws IOException
      {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        final List<Resource> resources = Stream.of(resolver.getResources("classpath*:/webapp"))
                                               .map(_f(x -> Resource.newResource(x.getURI())))
                                               .collect(toList());
        return resources.toArray(new Resource[0]);
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
