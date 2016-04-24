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
import java.util.Collection;
import java.util.stream.Stream;
import it.tidalwave.bluemarine2.model.Entity;
import org.testng.annotations.Test;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PhotoCollectionProviderSupportTest extends PhotoCollectionProviderTestSupport
  {
    private static final String URL_MOCK_RESOURCE = "http://localhost:8080";
//    private static final String URL_MOCK_RESOURCE = "file:src/test/resources/images.xml";

    @Test
    public void must_properly_parse_PhotoItems()
      throws Exception
      {
        // given
        final PhotoCollectionProviderSupport underTest = new PhotoCollectionProviderSupport(URL_MOCK_RESOURCE);
        // when
        final Collection<Entity> photoItems = underTest.findPhotos(mediaFolder, "file:src/test/resources/images.xml");
        // then
        final Path actualResult = Paths.get("target", "test-results", "photoItems.txt");
        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", "photoItems.txt");
        Files.createDirectories(actualResult.getParent());
        final Stream<String> stream = photoItems.stream()
                                                .map(e -> (PhotoItem)e)
                                                .map(pi -> String.format("%s: %s", pi.getId(), pi.getTitle()));
        Files.write(actualResult, (Iterable<String>)stream::iterator, StandardCharsets.UTF_8);
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }
  }
