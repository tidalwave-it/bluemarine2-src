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
package it.tidalwave.bluemarine2.mediascanner;

import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.io.File;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.model.impl.DefaultMediaFileSystem;
import it.tidalwave.bluemarine2.persistence.DumpCompleted;
import it.tidalwave.bluemarine2.persistence.DumpRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import it.tidalwave.util.test.FileComparisonUtils;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    
    private CountDownLatch dumpCompleted;
    
    // Listeners must be fields or they will garbage-collected
    private final MessageBus.Listener<DumpCompleted> onDumpCompleted = (message) -> dumpCompleted.countDown();
    
    @BeforeMethod
    private void prepareTest() 
      {
        final String s = "classpath:/META-INF/DefaultMediaScannerTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s);
        
        context.getBean(MockInstantProvider.class).setTimestamp(Instant.ofEpochSecond(1428232317L));
        messageBus = context.getBean(MessageBus.class);
        underTest = context.getBean(DefaultMediaScanner.class);
        
        dumpCompleted = new CountDownLatch(1);
        messageBus.subscribe(DumpCompleted.class, onDumpCompleted);
      }

    @Test
    public void testScan() 
      throws IOException, InterruptedException
      {
        final DefaultMediaFileSystem mediaFileSystem = new DefaultMediaFileSystem();

        underTest.process(mediaFileSystem.getRoot());
        
        final File actualFile = new File("target/test-results/model.n3");
        final File expectedFile = new File("src/test/resources/expected-results/model.n3");
        
        messageBus.publish(new DumpRequest(actualFile.toPath()));
        dumpCompleted.await();

        // FIXME: OOM
        FileComparisonUtils.assertSameContents(expectedFile, actualFile);
      }
  }
