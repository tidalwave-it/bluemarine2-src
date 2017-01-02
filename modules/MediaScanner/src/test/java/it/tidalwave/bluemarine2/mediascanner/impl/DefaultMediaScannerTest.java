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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;
import java.time.Instant;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.slf4j.bridge.SLF4JBridgeHandler;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import it.tidalwave.bluemarine2.persistence.PersistencePropertyNames;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedPath;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.Paths;
import static org.testng.AssertJUnit.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMediaScannerTest extends SpringTestSupport
  {
    private static final Instant MOCK_TIMESTAMP = Instant.ofEpochSecond(1428232317L);

    private Path musicTestSets;

    private DefaultMediaScanner underTest;

    private MessageBus messageBus;

    private CountDownLatch scanCompleted;

    // Listeners must be fields or they will garbage-collected
    private final MessageBus.Listener<ScanCompleted> onScanCompleted = message -> scanCompleted.countDown();

    private MediaFileSystem fileSystem;

    private Persistence persistence;

    static
      {
        // Some libraries use JUL
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
      }

    public DefaultMediaScannerTest()
      {
        super("META-INF/DciAutoBeans.xml",
              "META-INF/CommonsAutoBeans.xml",
              "META-INF/PersistenceAutoBeans.xml",
              "META-INF/DefaultMediaScannerTestBeans.xml");
      }

    @BeforeClass
    public void checkTestSets()
      {
        System.getProperties().forEach((name, value) -> log.info(">>>> {}: {}", name, value));
        musicTestSets = TestSetLocator.getMusicTestSetsPath();
      }

    @BeforeMethod
    public void prepareTest()
      throws InterruptedException
      {
        fileSystem = context.getBean(MediaFileSystem.class);
        persistence = context.getBean(Persistence.class);

        context.getBean(MockInstantProvider.class).setInstant(MOCK_TIMESTAMP);
        messageBus = context.getBean(MessageBus.class);
        underTest = context.getBean(DefaultMediaScanner.class);

        scanCompleted = new CountDownLatch(1);
        messageBus.subscribe(ScanCompleted.class, onScanCompleted);
      }

    @Test(dataProvider = "testSetNames") // see BMT-46
    public void testFileSystemConsistency (final @Nonnull String testSetName)
      throws Exception
      {
        final Path testSetPath = musicTestSets.resolve(testSetName);

        if (!Files.isDirectory(testSetPath))
          {
            log.warn("MISSING TEST SET: {} - {}", testSetName, testSetPath);
            return;
          }

        try (final Stream<Path> dirStream = Files.walk(testSetPath.resolve("Music"), FOLLOW_LINKS))
          {
            dirStream.forEach(path ->
              {
                try
                  {
                    assertTrue("Inconsistent file: " + path, Files.exists(normalizedPath(path)));
//                    assertTrue("Inconsistent file: " + path, normalizedPath(path).toFile().exists());
                  }
                catch (IOException e)
                  {
                    throw new RuntimeException(e);
                  }
              });
          }

//        for (final File file : p.resolve("Music").toFile().listFiles())
//          {
//            assertTrue("Inconsistent file: " + file, file.exists());
//          }
      }

    @Test(dataProvider = "testSetNames", dependsOnMethods = "testFileSystemConsistency")
    public void testScan (final @Nonnull String testSetName)
      throws Exception
      {
        // given
        final Path testSetPath = musicTestSets.resolve(testSetName);

        if (!Files.isDirectory(testSetPath))
          {
            log.warn("MISSING TEST SET: {} - {}", testSetName, testSetPath);
            return;
          }

        // FIXME: we should find a way to force HttpClient to pretend the network doesn't work
//        log.warn("******* YOU SHOULD RUN THIS TEST WITH THE NETWORK DISCONNECTED");
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(ModelPropertyNames.ROOT_PATH, testSetPath);
        // Large import with native RDF storage fails at end. Temporarily operating in memory. See BMT-114.
//        properties.put(PersistencePropertyNames.STORAGE_FOLDER, Paths.get("target/test-results/storage-" + testSetName));
//        properties.put(it.tidalwave.bluemarine2.downloader.PropertyNames.CACHE_FOLDER_PATH, Paths.get("target/test-classes/download-cache-" + dataSetName));
        messageBus.publish(new PowerOnNotification(properties));

        // Wait for the MediaFileSystem to initialize. Indeed, MediaFileSystem should be probably mocked
        Thread.sleep(1000);
        // when
        underTest.process(fileSystem.getRoot());
        scanCompleted.await();
        // then
        final String modelName = "model-" + testSetName + ".n3";
        final File actualFile = new File("target/test-results/" + modelName);
        final File expectedFile = new File("src/test/resources/expected-results/" + modelName);
        persistence.exportToFile(actualFile.toPath());

        // FIXME: likely OOM in case of mismatch
        assertSameContents(expectedFile, actualFile);
      }

    @DataProvider
    private static Object[][] testSetNames()
      {
        return new Object[][]
          {
            { "iTunes-fg-20160504-1" },
            { "iTunes-fg-20161210-1" }
          };
      }
  }
