/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.rest.impl.server;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;
import java.util.Enumeration;
import java.util.EnumSet;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultResourceServer implements ResourceServer
  {
    private String ipAddress = "";

    private int port;

    private Server server;

    @Inject
    private ApplicationContext applicationContext;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
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
    /* VisibleForTesting FIXME */ public void onPowerOnNotification (final @ListensTo @Nonnull PowerOnNotification notification)
      throws Exception
      {
        log.info("onPowerOnNotification({})", notification);
        ipAddress = getNonLoopbackIPv4Address().getHostAddress();
        server = new Server(InetSocketAddress.createUnresolved(ipAddress, Integer.getInteger("port", 0)));

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setBaseResource(new ResourceCollection(findWebResources()));
        log.info("RESOURCE BASE: {}", servletContext.getResourceBase());
        servletContext.setContextPath("/");
        servletContext.setWelcomeFiles(new String[] { "index.xhtml" });
        final DelegateWebApplicationContext wac = new DelegateWebApplicationContext(applicationContext, servletContext.getServletContext());
        servletContext.addServlet(new ServletHolder("spring", new DispatcherServlet(wac)), "/rest/*");
        servletContext.addServlet(new ServletHolder("default", new DefaultServlet()), "/*");
        servletContext.addFilter(new FilterHolder(new LoggingFilter()), "/*", EnumSet.allOf(DispatcherType.class));
        server.setHandler(servletContext);

        server.start();
        port = server.getConnectors()[0].getLocalPort(); // jetty 8
//        port = ((ServerConnector)server.getConnectors()[0]).getLocalPort(); // jetty 9
        log.info(">>>> resource server jetty started at {}:{} ", ipAddress, port);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting FIXME */ public void onPowerOffNotification (final @ListensTo @Nonnull PowerOffNotification notification)
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
     ******************************************************************************************************************/
    @Nonnull
    private Resource[] findWebResources()
      throws IOException
      {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        return Stream.of(resolver.getResources("classpath*:/webapp"))
                     .map(_f(x -> Resource.newResource(x.getURI())))
                     .toArray(Resource[]::new);
      }
  }
