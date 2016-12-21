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
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.text.Normalizer.Form.NFC;
import static java.text.Normalizer.normalize;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents2;
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
    /*******************************************************************************************************************
     *
     * Scans a test set and dumps the read metadata to text files with the same name of the sample plus the '-dump.txt'
     * suffix. They are checked against a collection of files with expected values, that have been manually validated.
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "pathProvider")
    public void must_properly_read_metadata (final @Nonnull String testSetName,
                                             final @Nonnull Path path,
                                             final @Nonnull Path relativePath)
      throws IOException
      {
        final Path dumpRelativePath = Paths.get(testSetName, relativePath.toString() + "-dump.txt");
        final Path actualFile = PATH_TEST_RESULTS.resolve(dumpRelativePath);
        final Path expectedFile = PATH_EXPECTED_TEST_RESULTS.resolve("metadata").resolve(dumpRelativePath);
        Files.createDirectories(actualFile.getParent());
        final Metadata metadata = AudioMetadataFactory.loadFrom(path);
        final List<String> metadataDump = metadata.getEntries().stream()
                .sorted(comparing(e -> e.getKey()))
                .map(e -> String.format("%s.%s = %s",
                                        normalize(relativePath.toString(), NFC),
                                        e.getKey(), e.getValue()))
                .collect(toList());
        Files.write(actualFile, metadataDump);
        assertSameContents2(expectedFile, actualFile);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @AllArgsConstructor @ToString
    static class Triple
      {
        private final String testSetName;
        private final Path testSetPath;
        private final Path filePath;

        public static Triple ofName (final @Nonnull String testSetName)
          {
            return new Triple(testSetName, TestSetLocator.getMusicTestSetsPath().resolve(testSetName), null);
          }

        @Nonnull
        private Stream<Triple> walk()
          {
            try
              {
                if (Files.exists(testSetPath))
                  {
                    return Files.walk(testSetPath, FOLLOW_LINKS)
                                .map(path -> new Triple(testSetName, testSetPath, path));
                  }
                else
                  {
                    log.warn("MISSING TEST SET: {} - {}", testSetName, testSetPath);
                    return Stream.empty();
                  }
              }
            catch (IOException e)
              {
                throw new RuntimeException(e);
              }
          }
      }

    /*******************************************************************************************************************
     *
     * The number of provided test cases varies in function of the physical test sets available on the disk. Some
     * test sets can't be distributed, as they contain commercial audio files.
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] pathProvider()
      throws IOException
      {
        final Stream<String> testSetNames = Stream.of("iTunes-fg-20160504-1", "iTunes-fg-20161210-1");
        return testSetNames.map(testSetName -> Triple.ofName(testSetName))
                           .flatMap(Triple::walk)
                           .filter(t -> Files.isRegularFile(t.filePath))
                           .filter(t -> !t.filePath.getFileName().toString().startsWith(".")) // isHidden() throws exception
                           .map(t -> new Object[] { t.testSetName, t.filePath, t.testSetPath.relativize(t.filePath) })
                           .collect(toList())
                           .toArray(new Object[0][0]);
      }
  }
