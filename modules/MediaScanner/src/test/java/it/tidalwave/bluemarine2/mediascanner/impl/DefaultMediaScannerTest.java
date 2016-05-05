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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.time.Instant;
import java.io.File;
import java.nio.file.Path;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.util.Key;
import it.tidalwave.util.spi.MockInstantProvider;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.mediascanner.ScanCompleted;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.persistence.Persistence;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMediaScannerTest
  {
    private static final Instant MOCK_TIMESTAMP = Instant.ofEpochSecond(1428232317L);

    private Path musicTestSets;

    private DefaultMediaScanner underTest;

    private ClassPathXmlApplicationContext context;

    private MessageBus messageBus;

    private CountDownLatch scanCompleted;

    // Listeners must be fields or they will garbage-collected
    private final MessageBus.Listener<ScanCompleted> onScanCompleted = message -> scanCompleted.countDown();

    private MediaFileSystem fileSystem;

    private Persistence persistence;

    @BeforeClass
    public void checkTestSets()
      {
        musicTestSets = TestSetLocator.getMusicTestSetsPath();
      }

    @BeforeMethod
    private void prepareTest()
      throws InterruptedException
      {
        context = new ClassPathXmlApplicationContext("META-INF/CommonsAutoBeans.xml",
                                                     "META-INF/PersistenceAutoBeans.xml",
                                                     "META-INF/DefaultMediaScannerTestBeans.xml");
        fileSystem = context.getBean(MediaFileSystem.class);
        persistence = context.getBean(Persistence.class);

        context.getBean(MockInstantProvider.class).setInstant(MOCK_TIMESTAMP);
        messageBus = context.getBean(MessageBus.class);
        underTest = context.getBean(DefaultMediaScanner.class);

        scanCompleted = new CountDownLatch(1);
        messageBus.subscribe(ScanCompleted.class, onScanCompleted);

      }

    @Test(dataProvider = "dataSetNames", groups = "no-ci") // until we manage to run it without downloading stuff, it's not reproducible
    public void testScan (final @Nonnull String dataSetName)
      throws Exception
      {
        final Path p = musicTestSets.resolve(dataSetName);
        // FIXME: we should find a way to force HttpClient to pretend the network doesn't work
//        log.warn("******* YOU SHOULD RUN THIS TEST WITH THE NETWORK DISCONNECTED");
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH, p);
//        properties.put(it.tidalwave.bluemarine2.downloader.PropertyNames.CACHE_FOLDER_PATH, Paths.get("target/test-classes/download-cache-" + dataSetName));
        messageBus.publish(new PowerOnNotification(properties));

        // Wait for the MediaFileSystem to initialize. Indeed, MediaFileSystem should be probably mocked
        Thread.sleep(1000);

        underTest.process(fileSystem.getRoot());
        scanCompleted.await();

        final String modelName = "model-" + dataSetName + ".n3";
        final File actualFile = new File("target/test-results/" + modelName);
        final File expectedFile = new File("src/test/resources/expected-results/" + modelName);
        persistence.dump(actualFile.toPath());

        // FIXME: likely OOM in case of mismatch
        assertSameContents(expectedFile, actualFile);
      }

    @DataProvider
    private static Object[][] dataSetNames()
      {
        return new Object[][]
          {
              { "iTunes-fg-20160504-1" }
          };
      }
  }
