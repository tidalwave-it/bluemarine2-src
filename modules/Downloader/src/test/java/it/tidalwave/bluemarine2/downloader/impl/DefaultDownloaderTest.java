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
package it.tidalwave.bluemarine2.downloader.impl;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.apache.http.HttpStatus;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadComplete.Origin;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import it.tidalwave.bluemarine2.downloader.DownloadRequest.Option;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import static java.nio.file.Files.*;
import static org.apache.commons.io.FileUtils.*;
import static it.tidalwave.bluemarine2.downloader.PropertyNames.CACHE_FOLDER_PATH;
import static it.tidalwave.util.test.FileComparisonUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultDownloaderTest extends SpringTestSupport
  {
    private static final Path CACHE_RESOURCES_PATH = Paths.get("target/test-classes/download-cache");

    private static final Path CACHE_PATH = Paths.get("target/download-cache");

    private static final Path TEST_RESULTS = Paths.get("target/test-results");

    private static final Path EXPECTED_TEST_RESULTS = Paths.get("src/test/resources/expected-results");

    private DefaultDownloader underTest;

    private SimpleHttpCacheStorage cacheStorage;

    private MessageBus messageBus;

    private CountDownLatch downloadCompleted;

    private DownloadComplete response;

    // Listeners must be fields or they will garbage-collected
    private final MessageBus.Listener<DownloadComplete> onDownloadCompleted = (response) ->
      {
        downloadCompleted.countDown();
        this.response = response;
      };

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DefaultDownloaderTest()
      {
        super("META-INF/CommonsAutoBeans.xml",
              "META-INF/DefaultDownloaderTestBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void prepare()
      throws IOException, NotFoundException
      {
        deleteDirectory(CACHE_PATH.toFile());
        copyDirectory(CACHE_RESOURCES_PATH.toFile(), CACHE_PATH.toFile());

        underTest = context.getBean(DefaultDownloader.class);
        cacheStorage = context.getBean(SimpleHttpCacheStorage.class);
        messageBus = context.getBean(MessageBus.class);
        downloadCompleted = new CountDownLatch(1);
        response = null;
        messageBus.subscribe(DownloadComplete.class, onDownloadCompleted);

        // FIXME: should also test with false
        cacheStorage.setNeverExpiring(true);

        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(CACHE_FOLDER_PATH, CACHE_PATH);
        messageBus.publish(new PowerOnNotification(properties));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "downloadDataProvider", groups = "no-ci", enabled = false) // FIXME dbtune.org has been returing HTTP status 503 for months
    public void testCache (final @Nonnull String urlAsString,
                           final @Nonnull Option option,
                           final int expectedStatusCode,
                           final @Nonnull String expectedContentFileName,
                           final @Nonnull Origin expectedOrigin)
      throws Exception
      {
        final URL url = new URL(urlAsString);
        underTest.onDownloadRequest(new DownloadRequest(url, new Option[]{option} ));
        downloadCompleted.await();
        assertThat(response.getUrl(), is(url));
        assertThat(response.getStatusCode(), is(expectedStatusCode));

        if (!"".equals(expectedContentFileName))
          {
            final Path actualResult = TEST_RESULTS.resolve(expectedContentFileName);
            final Path expectedResult = EXPECTED_TEST_RESULTS.resolve(expectedContentFileName);
            createDirectories(TEST_RESULTS);
            write(actualResult, response.getBytes());
            assertSameContents(expectedResult.toFile(), actualResult.toFile());
          }

        if (!Arrays.asList(-1, HttpStatus.SC_SEE_OTHER, HttpStatus.SC_NOT_FOUND).contains(response.getStatusCode()))
          {
            assertThat("Cache updated?", cacheStorage.isCachedResourcePresent(urlAsString), is(true));
          }

        // FIXME: verify it didn't go to the network
        // FIXME: verify that the cache has not been updated
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @DataProvider(name = "downloadDataProvider")
    private static Object[][] downloadDataProvider()
      {
        return new Object[][]
          {
            ////////////////// Not in cache
              {
                "http://dbtune.org/musicbrainz/data/track/00270741-e962-4a97-aa28-8ad08885a82a",
                Option.NO_OPTION,
                HttpStatus.SC_OK,
                "00270741-e962-4a97-aa28-8ad08885a82a-200",
                Origin.NETWORK
              },
            ////////////////// In cache
              {
                "http://dbtune.org/musicbrainz/data/track/a51c646d-d676-4690-8131-62373e8b77db",
                Option.NO_OPTION,
                HttpStatus.SC_OK,
                "a51c646d-d676-4690-8131-62373e8b77db-200",
                Origin.CACHE
              },
            ////////////////// In cache with "See Other", don't follow redirect
              {
                "http://dbtune.org/musicbrainz/resource/track/a51c646d-d676-4690-8131-62373e8b77db",
                Option.NO_OPTION,
                HttpStatus.SC_SEE_OTHER,
                "a51c646d-d676-4690-8131-62373e8b77db-303",
                Origin.CACHE
              },
            ////////////////// In cache with "See Other", do follow redirect
              {
                "http://dbtune.org/musicbrainz/resource/track/a51c646d-d676-4690-8131-62373e8b77db",
                Option.FOLLOW_REDIRECT,
                HttpStatus.SC_OK,
                "a51c646d-d676-4690-8131-62373e8b77db-200",
                Origin.CACHE
              },
            ////////////////// Not found
              {
                "http://dbtune.org/does-not-exist",
                Option.NO_OPTION,
                HttpStatus.SC_NOT_FOUND,
                "",
                Origin.NETWORK
              },
              // TODO: 404 could be cached?
            ////////////////// Unknown host
              {
                "http://does.not.exist/a-resource",
                Option.NO_OPTION,
                -1,
                "",
                Origin.NETWORK
              }
          };
      }
  }
