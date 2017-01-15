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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.commons.test.TestSetTriple;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.text.Normalizer.Form.NFC;
import static java.text.Normalizer.normalize;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents2;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.CDDB;

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
    public void must_properly_read_metadata (final @Nonnull TestSetTriple triple)
      throws IOException
      {
        // when
        final Metadata metadata = AudioMetadataFactory.loadFrom(triple.getFilePath());
        // then
        final String relativePath = triple.getRelativePath().toString();
        final Path dumpRelativePath = Paths.get(triple.getTestSetName(), relativePath + "-dump.txt");
        final Path actualFile = PATH_TEST_RESULTS.resolve(dumpRelativePath);
        final Path expectedFile = PATH_EXPECTED_TEST_RESULTS.resolve("metadata").resolve(dumpRelativePath);
        final List<String> metadataDump = metadata.getEntries()
                                                  .stream()
                                                  // FIXME: this should be removed, and the expected results updated
                                                  .filter(entry -> !entry.getKey().equals(CDDB))
                                                  .sorted(comparing(e -> e.getKey()))
                                                  .map(e -> String.format("%s.%s = %s", normalize(relativePath, NFC),
                                                                                        e.getKey(),
                                                                                        e.getValue()))
                                                  .collect(toList());
        Files.createDirectories(actualFile.getParent());
        Files.write(actualFile, metadataDump);
        assertSameContents2(expectedFile, actualFile);
      }

    /*******************************************************************************************************************
     *
     * The number of provided test cases varies in function of the physical test sets available on the disk. Some
     * test sets can't be distributed, as they contain commercial audio files.
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] pathProvider()
      {
        return streamOfTestSetTriples(TestSetLocator.allTestSets()).collect(toTestNGDataProvider());
      }
  }
