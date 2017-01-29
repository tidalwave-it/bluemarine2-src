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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.InetSocketAddress;
import java.net.URI;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class MusicResourcesControllerTest extends SpringTestSupport
  {
    private Server server;

    private String baseUrl;

    public MusicResourcesControllerTest()
      {
        super("classpath:META-INF/DciAutoBeans.xml",
              "classpath:META-INF/CommonsAutoBeans.xml",
              "classpath:META-INF/ModelAutoBeans.xml",
              "classpath:META-INF/PersistenceAutoBeans.xml",
              "classpath:META-INF/CatalogAutoBeans.xml");
      }

    @BeforeClass
    public void setup()
      throws Exception
      {
        final ServletHolder diServletHolder = new ServletHolder(new DispatcherServlet());
        diServletHolder.setName("spring");
        final String ipAddress = "127.0.0.1";
        server = new Server(InetSocketAddress.createUnresolved(ipAddress, Integer.getInteger("port", 0)));
        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(diServletHolder, "/rest/*");
        servletContext.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        server.setHandler(servletContext);
        server.start();
        final int port = server.getConnectors()[0].getLocalPort(); // jetty 8
        baseUrl = String.format("http://%s:%d", ipAddress, port);
      }

    @AfterClass
    public void shutdown()
      throws Exception
      {
        server.stop();
        server.destroy();
      }

    @Test(dataProvider = "resources")
    public void must_serve_REST_resource (final @Nonnull String url, final @Nonnull String expected)
      throws IOException
      {
        // given
        final RestTemplate restTemplate = new RestTemplate();
        // when
        final String resource = restTemplate.getForObject(URI.create(baseUrl + url), String.class);
        // then
        Files.createDirectories(PATH_TEST_RESULTS);
        final Path actualPath = PATH_TEST_RESULTS.resolve(expected);
        final Path expectedPath = PATH_EXPECTED_TEST_RESULTS.resolve(expected);
        Files.write(actualPath, resource.getBytes(UTF_8));
        assertSameContents(expectedPath, actualPath);
      }

    @Test
    public void test_service_publishing()
      throws InterruptedException
      {
        Thread.sleep(Integer.getInteger("delay", 0));
      }

    @DataProvider
    private static Object[][] resources()
      {
        return new Object[][]
          {
            { "/rest/record",                                                          "records.json"  },
            { "/rest/track",                                                           "tracks.json"   },
            { "/rest/record/urn:bluemarine:record:eLWktOMBbcOWysVn6AW6kksBS7Q=",       "record-eLWktOMBbcOWysVn6AW6kksBS7Q=.json"          },
            { "/rest/record/urn:bluemarine:record:eLWktOMBbcOWysVn6AW6kksBS7Q=/track", "record-eLWktOMBbcOWysVn6AW6kksBS7Q=-tracks.json"   }
          };
      }
  }
