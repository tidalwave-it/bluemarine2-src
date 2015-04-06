/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
import java.util.concurrent.CountDownLatch;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import it.tidalwave.bluemarine2.downloader.DownloadRequest.Option;
import static it.tidalwave.bluemarine2.downloader.PropertyNames.CACHE_FOLDER_PATH;
import it.tidalwave.bluemarine2.ui.commons.PowerOnNotification;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.util.test.FileComparisonUtils;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultDownloaderTest 
  {
    private DefaultDownloader underTest;
    
    private ClassPathXmlApplicationContext context;
    
    private MessageBus messageBus;
    
    private CountDownLatch downloadCompleted;
    
    private DownloadComplete response;
    
    // Listeners must be fields or they will garbage-collected
    private final MessageBus.Listener<DownloadComplete> onDownloadCompleted = (response) ->
      {
        downloadCompleted.countDown();
        this.response = response;
      };
            
    @BeforeMethod
    public void prepare() 
      throws IOException, NotFoundException 
      {
        final Path cacheResourcesPath = Paths.get("target/test-classes/download-cache");
        final Path cachePath = Paths.get("target/download-cache");
//        Files.deleteIfExists(cachePath);
//        Files.createDirectories(cachePath);
        FileUtils.deleteDirectory(cachePath.toFile());
        FileUtils.copyDirectory(cacheResourcesPath.toFile(), cachePath.toFile());
//        Files.copy(cacheResourcesPath, cachePath, StandardCopyOption.REPLACE_EXISTING);

        final String s = "classpath:/META-INF/DefaultDownloaderTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s);
        underTest = context.getBean(DefaultDownloader.class);
        messageBus = context.getBean(MessageBus.class); // FIXME: use a mock messagebus
        downloadCompleted = new CountDownLatch(1);
        response = null;
        messageBus.subscribe(DownloadComplete.class, onDownloadCompleted);
        
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(CACHE_FOLDER_PATH, cachePath);
        underTest.onPowerOnNotification(new PowerOnNotification(properties));
      }

    @Test(dataProvider = "p")
    public void testCache (final @Nonnull String u,
                           final int statusCode,
                           final @Nonnull String fileName,
                           final @Nonnull Option option)
      throws Exception
      {
        final URL url = new URL(u);
        underTest.onDownloadRequest(new DownloadRequest(url, new Option[]{option} ));
        downloadCompleted.await();
        assertThat(response.getStatusCode(), is(statusCode));
        
        if (!"".equals(fileName))
          {
            final Path testResults = Paths.get("target/test-results");
            final Path expectedResults = Paths.get("src/test/resources/expected-results");
            final Path actualResult = testResults.resolve(fileName);
            final Path expectedResult = expectedResults.resolve(fileName);
            Files.createDirectories(testResults);
            Files.write(actualResult, response.getBytes());
            FileComparisonUtils.assertSameContents(expectedResult.toFile(), actualResult.toFile());
          }
        
        // FIXME: verify it didn't go to the nextwork
        // FIXME: verify that the cache has not been updated
      }
    
    @DataProvider(name = "p")
    private static Object[][] p()
      {
        return new Object[][]
          {
//            ////////////////// Not in cache
//              {
//                "http://dbtune.org/musicbrainz/data/track/00270741-e962-4a97-aa28-8ad08885a82a",
//                HttpStatus.SC_OK,
//                "00270741-e962-4a97-aa28-8ad08885a82a-200",
//                Option.NO_OPTION
//              },
            ////////////////// In cache
              {
                "http://dbtune.org/musicbrainz/data/track/a51c646d-d676-4690-8131-62373e8b77db",
                HttpStatus.SC_OK,
                "a51c646d-d676-4690-8131-62373e8b77db-200",
                Option.NO_OPTION
              },
            ////////////////// In cache with "See Other", don't follow redirect
              {
                "http://dbtune.org/musicbrainz/resource/track/a51c646d-d676-4690-8131-62373e8b77db",
                HttpStatus.SC_SEE_OTHER,
                "a51c646d-d676-4690-8131-62373e8b77db-303",
                Option.NO_OPTION
              },
            ////////////////// In cache with "See Other", do follow redirect
              {
                "http://dbtune.org/musicbrainz/resource/track/a51c646d-d676-4690-8131-62373e8b77db",
                HttpStatus.SC_OK,
                "a51c646d-d676-4690-8131-62373e8b77db-200",
                Option.FOLLOW_REDIRECT
              },
              
            ////////////////// Not found
              {
                "http://dbtune.org/does-not-exist",
                HttpStatus.SC_NOT_FOUND,
                "",
                Option.NO_OPTION
              },
              
              // TODO: 404 could be cached?
            ////////////////// Unknown host
              {
                "http://does.not.exist/a-resource",
                -1,
                "",
                Option.NO_OPTION
              }
          };
      }
  }
