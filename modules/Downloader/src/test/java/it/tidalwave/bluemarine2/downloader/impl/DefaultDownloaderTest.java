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
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.util.test.FileComparisonUtils;
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
      {
        final String s = "classpath:/META-INF/DefaultDownloaderTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s);
        underTest = context.getBean(DefaultDownloader.class);
        messageBus = context.getBean(MessageBus.class); // FIXME: use a mock messagebus
        downloadCompleted = new CountDownLatch(1);
        response = null;
        messageBus.subscribe(DownloadComplete.class, onDownloadCompleted);
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
        
        if (statusCode > 0)
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
              {
                "http://dbtune.org/musicbrainz/data/track/a51c646d-d676-4690-8131-62373e8b77db",
                200,
                "a51c646d-d676-4690-8131-62373e8b77db-200",
                Option.NO_OPTION
              },
              {
                "http://dbtune.org/musicbrainz/resource/track/a51c646d-d676-4690-8131-62373e8b77db",
                303,
                "a51c646d-d676-4690-8131-62373e8b77db-303",
                Option.NO_OPTION
              },
              {
                "http://dbtune.org/musicbrainz/resource/track/a51c646d-d676-4690-8131-62373e8b77db",
                200,
                "a51c646d-d676-4690-8131-62373e8b77db-200",
                Option.FOLLOW_REDIRECT
              },
              // unknown host
              {
                "http://does.not.exist/a-resource",
                -1,
                "",
                Option.NO_OPTION
              }
          };
      }
  }
