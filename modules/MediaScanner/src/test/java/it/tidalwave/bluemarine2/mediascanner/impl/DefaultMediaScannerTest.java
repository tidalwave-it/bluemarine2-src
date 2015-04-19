/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.mediascanner.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.time.Instant;
import java.io.File;
import java.nio.file.Paths;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.util.Key;
import it.tidalwave.util.spi.MockInstantProvider;
import it.tidalwave.util.test.FileComparisonUtils;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.mediascanner.ScanCompleted;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.persistence.Persistence;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMediaScannerTest
  {
    private DefaultMediaScanner underTest;
    
    private ClassPathXmlApplicationContext context;
    
    private MessageBus messageBus;
    
    private CountDownLatch scanCompleted;
    
    // Listeners must be fields or they will garbage-collected
    private final MessageBus.Listener<ScanCompleted> onScanCompleted = (message) -> scanCompleted.countDown();
    
    private MediaFileSystem fileSystem;
    
    private Persistence persistence;
    
    @BeforeMethod
    private void prepareTest() 
      throws InterruptedException 
      {
        final String s1 = "classpath:/META-INF/CommonsAutoBeans.xml";
        final String s2 = "classpath:/META-INF/PersistenceAutoBeans.xml";
        final String s3 = "classpath:/META-INF/DefaultMediaScannerTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s1, s2, s3);
        fileSystem = context.getBean(MediaFileSystem.class);
        persistence = context.getBean(Persistence.class);
        
        context.getBean(MockInstantProvider.class).setInstant(Instant.ofEpochSecond(1428232317L));
        messageBus = context.getBean(MessageBus.class);
        underTest = context.getBean(DefaultMediaScanner.class);
        
        scanCompleted = new CountDownLatch(1);
        messageBus.subscribe(ScanCompleted.class, onScanCompleted);

        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH, Paths.get("/Users/fritz/Personal/Music/iTunes/iTunes Music"));
        properties.put(it.tidalwave.bluemarine2.downloader.PropertyNames.CACHE_FOLDER_PATH, Paths.get("target/test-classes/download-cache"));
        messageBus.publish(new PowerOnNotification(properties));
        
        // Wait for the MediaFileSystem to initialize. Indeed, MediaFileSystem should be probably mocked
        Thread.sleep(1000);
      }
    
    @Test
    public void testScan() 
      throws Exception
      {
        underTest.process(fileSystem.getRoot());
        scanCompleted.await();

        final File actualFile = new File("target/test-results/model.n3");
        final File expectedFile = new File("src/test/resources/expected-results/model.n3");
        persistence.dump(actualFile.toPath());

        // FIXME: OOM
        FileComparisonUtils.assertSameContents(expectedFile, actualFile);
      }
  }
