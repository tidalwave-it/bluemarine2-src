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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import it.tidalwave.bluemarine2.model.MediaFolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;
import static org.mockito.Mockito.*;
import static it.tidalwave.bluemarine2.util.PrettyPrint.xmlPrettyPrinted;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class PhotoItemDIDLAdapterTest
  {
    private ApplicationContext context;

    @BeforeMethod
    public void setup()
      {
        // required for DCI stuff
        context = new ClassPathXmlApplicationContext("classpath*:META-INF/DciBeans.xml");
      }

    @Test
    public void must_properly_generate_DIDL_content()
      throws Exception
      {
        // given
        final MediaFolder mediaFolder = mock(MediaFolder.class);
        when(mediaFolder.getPath()).thenReturn(Paths.get("/folder"));
        final PhotoItem photoItem = new PhotoItem(mediaFolder, "20150524-0034");
        final PhotoItemDIDLAdapter underTest = new PhotoItemDIDLAdapter(photoItem);
        // when
        final DIDLContent content = new DIDLContent();
        content.addObject(underTest.toObject());
        // then
        final DIDLParser parser = new DIDLParser();
        final String xml = xmlPrettyPrinted(parser.generate(content));

        final Path actualResult = Paths.get("target", "test-results", "didl.xml");
        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", "didl.xml");
        Files.createDirectories(actualResult.getParent());
        Files.write(actualResult, xml.getBytes(StandardCharsets.UTF_8));
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }
  }