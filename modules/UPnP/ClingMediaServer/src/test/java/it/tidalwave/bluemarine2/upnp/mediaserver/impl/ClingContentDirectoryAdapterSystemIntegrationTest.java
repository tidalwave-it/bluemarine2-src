/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.persistence.PropertyNames;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.resourceserver.DefaultResourceServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static it.tidalwave.bluemarine2.util.PrettyPrint.xmlPrettyPrinted;
import static it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH;

/***********************************************************************************************************************
 *
 * This is a complex integration test that brings up the real MediaServer and exposes it.
 *
 * To just bring up the service and keep it running for some time, run
 *
 *      mvn -Prun-service
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class ClingContentDirectoryAdapterSystemIntegrationTest extends ClingTestSupport
  {
    private static final Path EXPECTED_PATH = Paths.get("src/test/resources/expected-results/sequences");

    private static final Path ACTUAL_PATH = Paths.get("target/test-results/sequences");

    private UpnpClient upnpClient;

    private DefaultResourceServer resourceServer;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Getter @ToString
    static class Params
      {
        private final String objectId;
        private final String browseFlag;
        private final int firstResult;
        private final int maxResult;

        public Params (final @Nonnull String string)
          {
            final String[] parts = string.split(" @@@ ");
            objectId = parts[1];
            browseFlag = parts[2];
            firstResult = Integer.parseInt(parts[3]);
            maxResult = Integer.parseInt(parts[4]);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ClingContentDirectoryAdapterSystemIntegrationTest()
      {
        super("META-INF/MediaServerAutoBeans.xml" ,
              "META-INF/UPnPAutoBeans.xml" ,
                                                    "META-INF/DciBeans.xml" ,
                                                     "classpath*:META-INF/CatalogAutoBeans.xml" ,
                                                     "classpath*:META-INF/CommonsAutoBeans.xml" ,
                                                     "classpath*:META-INF/ModelAutoBeans.xml" ,
                                                     "classpath*:META-INF/PersistenceAutoBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeClass
    public void setup2()
      throws Exception
      {
        upnpClient = new UpnpClient("ContentDirectory");
        Executors.newSingleThreadExecutor().submit(upnpClient);
        // FIXME: should use local resources
        final Map<Key<?>, Object> properties = new HashMap<>();
        final Path configPath = Paths.get("src/test/resources/config");
        final Path repositoryPath = configPath.resolve("repository.n3");
        properties.put(ROOT_PATH, configPath); // FIXME: why is this needed?
        properties.put(PropertyNames.REPOSITORY_PATH, repositoryPath);
        resourceServer = context.getBean(DefaultResourceServer.class);
        context.getBean(MessageBus.class).publish(new PowerOnNotification(properties));
        Thread.sleep(4000); // wait for power on
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @AfterClass
    public void shutdown()
      {
        upnpClient.shutdown();
        context.close();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void test_service_publishing()
      throws Exception
      {
        log.info("The service is up and running");
        delay();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "sequences", dependsOnMethods = "test_service_publishing", timeOut = 120000)
    public void test_sequence (final @Nonnull String clientDeviceName, final @Nonnull String sequenceName)
      throws Throwable
      {
        final AtomicInteger n = new AtomicInteger(0);
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final Path sequencePath = Paths.get("src/test/resources/sequences", clientDeviceName, sequenceName + ".txt");

        for (final Params params : toParams(sequencePath))
          {
            log.info(">>>> running {} ...", params);
            final CountDownLatch latch = new CountDownLatch(1);

            final Browse browse = new Browse(upnpClient.getService(),
                                             params.getObjectId(),
                                             BrowseFlag.valueOrNullOf(params.getBrowseFlag()),
                                             null,
                                             (long)params.getFirstResult(),
                                             (long)params.getMaxResult())
              {
                @Override
                public void received (final @Nonnull ActionInvocation actionInvocation, final @Nonnull DIDLContent didl)
                  {
                    try
                      {
                        log.info("received() - {}", actionInvocation);
                        final String fileName = String.format("%s/%s/%s-%03d.txt",
                                clientDeviceName, sequenceName, sequenceName, n.getAndIncrement());
                        final Path expectedFile = EXPECTED_PATH.resolve(fileName);
                        final Path actualFile = ACTUAL_PATH.resolve(fileName);
                        Files.createDirectories(actualFile.getParent());
                        final String header = String.format("%s(%s)", actionInvocation.getAction().getName(), actionInvocation.getInputMap());
                        final DIDLParser parser = new DIDLParser();
                        final String hostAndPort = String.format("http://%s:%d", resourceServer.getIpAddress(), resourceServer.getPort());
                        final String result = xmlPrettyPrinted(parser.generate(didl)).replaceAll(hostAndPort, "http://<server>");
                        Files.write(actualFile, (header + "\n" + result).getBytes(StandardCharsets.UTF_8));
                        assertSameContents(expectedFile.toFile(), actualFile.toFile());
                      }
                    catch (Throwable e)
                      {
                        log.error("", e);
                        error.set(e);
                      }

                    latch.countDown();
                  }

                @Override
                public void updateStatus (final @Nonnull Browse.Status status)
                  {
                    log.info("updateStatus({})", status);
                  }

                @Override
                public void failure (final @Nonnull ActionInvocation invocation,
                                     final @Nonnull UpnpResponse operation,
                                     final @Nonnull String defaultMsg)
                  {
                    log.error("failure({}, {}, {})", invocation, operation, defaultMsg);
                    error.set(new RuntimeException("browse failure " + defaultMsg));
                    latch.countDown();
                  }
              };

            upnpClient.execute(browse);
            latch.await();

            if (error.get() != null)
              {
                throw error.get();
              }
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] sequences()
      throws IOException
      {
        return new Object[][]
          {
            { "LGTV", "sequence1" },
            { "LGTV", "sequence2" },
            { "LGTV", "sequence3" },
            { "LGTV", "sequence4" },
            { "LGTV", "sequence5" },
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Collection<Params> toParams (final @Nonnull Path path)
      throws IOException
      {
        return Files.readAllLines(path, UTF_8).stream().filter(s -> !s.startsWith("#")).map(Params::new).collect(toList());
      }
  }
