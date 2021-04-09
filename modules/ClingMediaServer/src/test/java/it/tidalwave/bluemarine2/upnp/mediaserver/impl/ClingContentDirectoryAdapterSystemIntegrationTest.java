/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import it.tidalwave.util.Key;
import it.tidalwave.bluemarine2.model.impl.DefaultMediaFileSystem;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.test.FileComparisonUtilsWithPathNormalizer.*;
import static it.tidalwave.bluemarine2.util.Formatters.*;
import static it.tidalwave.bluemarine2.commons.test.TestUtilities.*;
import it.tidalwave.bluemarine2.message.PersistenceInitializedNotification;
import it.tidalwave.bluemarine2.message.PowerOffNotification;
import static it.tidalwave.bluemarine2.model.ModelPropertyNames.ROOT_PATH;
import it.tidalwave.bluemarine2.model.impl.DefaultCacheManager;
import it.tidalwave.bluemarine2.rest.impl.server.DefaultResourceServer;

/***********************************************************************************************************************
 *
 * This is a complex integration test that brings up the real MediaServer and exposes it.
 *
 * To just bring up the service and keep it running for some time, run
 *
 *      mvn -Prun-service
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class ClingContentDirectoryAdapterSystemIntegrationTest extends ClingTestSupport
  {
    private static final Path EXPECTED_PATH = Paths.get("src/test/resources/expected-results/sequences");

    private static final Path ACTUAL_PATH = Paths.get("target/test-results/sequences");

    private static final Path PATH_SEQUENCES = Paths.get("src/test/resources/sequences");

    private UpnpClient upnpClient;

    private DefaultResourceServer resourceServer;

    private DefaultCacheManager cacheManager;

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
        super("META-INF/DciAutoBeans.xml" ,
              "META-INF/CommonsAutoBeans.xml" ,
              "META-INF/CatalogAutoBeans.xml" ,
              "META-INF/MediaServerAutoBeans.xml",
              "META-INF/RestAutoBeans.xml",
              "META-INF/UPnPAutoBeans.xml",
              "META-INF/UPnPTestBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeClass
    public final void setup()
      throws Exception
      {
        upnpClient = new UpnpClient("ContentDirectory", device -> "underTest".equals(device.getDetails().getSerialNumber()));
        Executors.newSingleThreadExecutor().submit(upnpClient);
        final Map<Key<?>, Object> properties = new HashMap<>();
        final Path repositoryPath = Paths.get("target/test-classes/test-sets/model-iTunes-fg-20160504-2.n3");
        properties.put(ROOT_PATH, TestSetLocator.getMusicTestSetsPath().resolve("iTunes-fg-20160504-2"));
        final PowerOnNotification powerOnNotification = new PowerOnNotification(properties);
        final DefaultMediaFileSystem fileSystem = context.getBean(DefaultMediaFileSystem.class);
        resourceServer = context.getBean(DefaultResourceServer.class);
        fileSystem.onPowerOnNotification(powerOnNotification);
        resourceServer.onPowerOnNotification(powerOnNotification);
        final Repository repository = context.getBean(Repository.class);
        loadRepository(repository, repositoryPath);
        cacheManager = context.getBean(DefaultCacheManager.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @AfterClass
    public final void shutdown()
      throws Exception
      {
        upnpClient.shutdown();
        resourceServer.onPowerOffNotification(new PowerOffNotification());
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
    public void test_sequence (final @Nonnull String clientDeviceName,
                               final @Nonnull String testSetName,
                               final @Nonnull String sequenceName)
      throws Throwable
      {
        final AtomicInteger n = new AtomicInteger(0);
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final Path sequencePath = PATH_SEQUENCES.resolve(Paths.get(clientDeviceName, testSetName, sequenceName + ".txt"));

        for (final Params params : toParams(sequencePath))
          {
            log.info("==== {} ========================================================================================",
                     String.format("%s-%03d", sequenceName, n.get()));
            log.info(">>>> sending {} ...", params);
            RepositoryTrackFinder.resetQueryCount();
            cacheManager.onPersistenceUpdated(new PersistenceInitializedNotification());
            final CountDownLatch latch = new CountDownLatch(1);

            final Browse browse = new Browse(upnpClient.getService(),
                                             params.getObjectId(),
                                             BrowseFlag.valueOrNullOf(params.getBrowseFlag()),
                                             null,
                                             params.getFirstResult(),
                                             (long)params.getMaxResult())
              {
                @Override
                public void updateStatus (final @Nonnull Browse.Status status)
                  {
                    log.info("updateStatus({})", status);
                  }

                @Override
                public void received (final @Nonnull ActionInvocation actionInvocation, final @Nonnull DIDLContent didl)
                  {
                    try
                      {
                        log.info("received() - {}", actionInvocation);
                        final String fileName = String.format("%s/%s/%s/%s-%03d.txt",
                                clientDeviceName, testSetName, sequenceName, sequenceName, n.getAndIncrement());
                        final Path expectedFile = EXPECTED_PATH.resolve(fileName);
                        final Path actualFile = ACTUAL_PATH.resolve(fileName);
                        Files.createDirectories(actualFile.getParent());
                        final String header = String.format("%s(%s)", actionInvocation.getAction().getName(), actionInvocation.getInputMap());
                        final DIDLParser parser = new DIDLParser();
                        final String baseUrl = resourceServer.absoluteUrl("");
                        final String result = xmlPrettyPrinted(parser.generate(didl)).replaceAll(baseUrl, "http://<server>/");
                        final String queries = String.format("QUERY COUNT: %d", RepositoryTrackFinder.getQueryCount());
                        Files.write(actualFile, (header + "\n" + result + "\n" + queries).getBytes(UTF_8));
                        assertSameContents(expectedFile, actualFile);
                      }
                    catch (Throwable e)
                      {
                        log.error("", e);
                        error.set(e);
                      }

                    latch.countDown();
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
            RepositoryTrackFinder.setDumpThreadOnQuery(false);

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
           // device name    test set name           sequence name
            { "LG-37LS5600", "iTunes-fg-20160504-2", "sequence1" },
            { "LG-37LS5600", "iTunes-fg-20160504-2", "sequence2" },
            { "LG-37LS5600", "iTunes-fg-20160504-2", "sequence3" },
            { "LG-37LS5600", "iTunes-fg-20160504-2", "sequence4" },
            { "LG-37LS5600", "iTunes-fg-20160504-2", "sequence5" },
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
