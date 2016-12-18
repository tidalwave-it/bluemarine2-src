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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static java.util.stream.Collectors.toList;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedPath;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class AudioMetadataFactoryTest
  {
    @Test(dataProvider = "pathProvider", groups = "no-ci") // because of BMT-46
    public void must_properly_read_metadata (final @Nonnull String testSetName,
                                             final @Nonnull Path path,
                                             final @Nonnull Path relativePath)
      throws IOException
      {
        final Path p = Paths.get(testSetName, relativePath.toString() + "-dump.txt");
        final Path actualFile = PATH_TEST_RESULTS.resolve(p);
        final Path expectedFile = PATH_EXPECTED_TEST_RESULTS.resolve("metadata").resolve(p);
        Files.createDirectories(actualFile.getParent());
        final Metadata metadata = AudioMetadataFactory.loadFrom(path);
        final List<String> collect = metadata.getEntries().stream()
                .sorted(Comparator.comparing(e -> e.getKey()))
                .map(e -> String.format("%s.%s = %s", relativePath, e.getKey(), e.getValue()))
                .collect(toList());
        Files.write(actualFile, collect);
        assertSameContents(normalizedPath(expectedFile.toAbsolutePath()).toFile(),
                           normalizedPath(actualFile.toAbsolutePath()).toFile());
//        System.err.println(am.audioFile);
//        final Tag tag = metadata.audioFile.getTag();
//        final List<TagField> fields = toList(tag.getFields());
//        System.err.println("FIELDS: " + fields);
//        tag.getFields(FieldKey.)
      }

    @DataProvider
    private static Object[][] pathProvider()
      throws IOException
      {
        final String testSetName = "iTunes-fg-20160504-1";
        final Path testSetPath = TestSetLocator.getMusicTestSetsPath().resolve(testSetName);
        return Files.walk(testSetPath, FOLLOW_LINKS)
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> !path.getFileName().toString().startsWith(".")) // isHidden() throws exception
                    .map(path -> new Object[] { testSetName, path, testSetPath.relativize(path) })
                    .collect(toList())
                    .toArray(new Object[0][0]);
      }
  }
