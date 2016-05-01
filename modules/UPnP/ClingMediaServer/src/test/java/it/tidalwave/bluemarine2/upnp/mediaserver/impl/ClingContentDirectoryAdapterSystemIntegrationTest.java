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
import java.util.stream.Stream;
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
import it.tidalwave.bluemarine2.persistence.PropertyNames;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static it.tidalwave.bluemarine2.util.PrettyPrint.xmlPrettyPrinted;
import static it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class ClingContentDirectoryAdapterSystemIntegrationTest extends ClingTestSupport
  {
    private static final Path EXPECTED_PATH = Paths.get("src/test/resources/expected-results");

    private static final Path ACTUAL_PATH = Paths.get("target/test-results");

    private UpnpClient upnpClient;

    private DefaultResourceServer resourceServer;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter @ToString
    static class Params
      {
        private final String objectId;
        private final String browseFlag;
        private final int firstResult;
        private final int maxResult;
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
        final Path configPath = getConfiguratonPath();
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
    @Nonnull
    private Path getConfiguratonPath()
      {
        String s = System.getProperty("user.home", "/");
        final String osName = System.getProperty("os.name").toLowerCase();

        switch (osName)
          {
            case "linux":
                s += "/.blueMarine2";
                break;

            case "mac os x":
                s += "/Library/Application Support/blueMarine2";
                break;

            case "windows":
                s += "/.blueMarine2";
                break;
          }

        return Paths.get(s);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "sequences", timeOut = 120000)
    public void test_sequence (final @Nonnull String sequenceName, final @Nonnull Collection<Params> sequence)
      throws Throwable
      {
        final AtomicInteger n = new AtomicInteger(0);
        final AtomicReference<Throwable> error = new AtomicReference<>();

        for (final Params params : sequence)
          {
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
                        final String fileName = String.format("%s/%s-%02d.txt", sequenceName, sequenceName, n.getAndIncrement());
                        final Path expectedFile = EXPECTED_PATH.resolve(fileName);
                        final Path actualFile = ACTUAL_PATH.resolve(fileName);
                        Files.createDirectories(actualFile.getParent());
                        final String header = String.format("%s(%s)", actionInvocation.getAction().getName(), actionInvocation.getInputMap());
                        final DIDLParser parser = new DIDLParser();
                        final String hostAndPort = String.format("%s:%d", resourceServer.getIpAddress(), resourceServer.getPort());
                        final String result = xmlPrettyPrinted(parser.generate(didl)).replaceAll(hostAndPort, "<server>");
                        Files.write(actualFile, (header + "\n" + result).getBytes(StandardCharsets.UTF_8));

                        try
                          {
                            assertSameContents(expectedFile.toFile(), actualFile.toFile());
                          }
                        catch (Throwable e)
                          {
                            error.set(e);
                          }
                      }
                    catch (Exception e)
                      {
                        log.error("", e);
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
      {
        return new Object[][]
          {
            { "sequence1", toParams(sequence1()) }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Collection<Params> toParams (final @Nonnull Object[][] objects)
      {
        return Stream.of(objects).map(p -> new Params((String)p[0], (String)p[1], (Integer)p[2], (Integer)p[2]))
                                 .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Object[][] sequence1()
      {
        return new Object[][]
          {
            { "0", "BrowseMetadata", 0, 0 },
            { "0", "BrowseDirectChildren", 0, 16 },
            { "0", "BrowseDirectChildren", 4, 12 },
            { "0", "BrowseDirectChildren", 4, 12 },
            { "0", "BrowseDirectChildren", 0, 1 },
            { "0", "BrowseDirectChildren", 0, 16 },
            { "0", "BrowseDirectChildren", 0, 16 },
            { "/music", "BrowseMetadata", 0, 0 },
            { "/music", "BrowseDirectChildren", 0, 16 },
            { "/music", "BrowseDirectChildren", 0, 1 },
            { "/music", "BrowseDirectChildren", 0, 16 },
            { "/music", "BrowseDirectChildren", 5, 11 },
            { "/music", "BrowseDirectChildren", 5, 11 },
            { "/music", "BrowseDirectChildren", 0, 16 },
            { "/music", "BrowseDirectChildren", 5, 11 },
            { "/music", "BrowseDirectChildren", 5, 11 },
            { "/music/RepositoryBrowserByArtistThenRecord", "BrowseMetadata", 0, 0 },
            { "/music/RepositoryBrowserByArtistThenRecord", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord", "BrowseDirectChildren", 0, 1 },
            { "/music/RepositoryBrowserByArtistThenRecord", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseMetadata", 0, 0 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 0, 1 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 1, 15 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 1, 15 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 1, 15 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e", "BrowseDirectChildren", 1, 15 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e/urn:bluemarine:record:c5a4f3868bfac0380a98543d4e073a62a5c2de04", "BrowseMetadata", 0, 0 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e/urn:bluemarine:record:c5a4f3868bfac0380a98543d4e073a62a5c2de04", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e/urn:bluemarine:record:c5a4f3868bfac0380a98543d4e073a62a5c2de04", "BrowseDirectChildren", 0, 1 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e/urn:bluemarine:record:c5a4f3868bfac0380a98543d4e073a62a5c2de04", "BrowseDirectChildren", 0, 16 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e/urn:bluemarine:record:c5a4f3868bfac0380a98543d4e073a62a5c2de04", "BrowseDirectChildren", 8, 8 },
            { "/music/RepositoryBrowserByArtistThenRecord/urn:bluemarine:artist:c58ba442a69066b53f80306ffa3bc65938c0017e/urn:bluemarine:record:c5a4f3868bfac0380a98543d4e073a62a5c2de04", "BrowseDirectChildren", 8, 8 },
          };
      }
  }
