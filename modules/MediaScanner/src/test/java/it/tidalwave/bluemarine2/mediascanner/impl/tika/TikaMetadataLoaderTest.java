/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.mediascanner.impl.tika;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.tika.metadata.Metadata;
import org.testng.annotations.Test;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class TikaMetadataLoaderTest extends TestSupport
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "testFileData")
    public void testMetadataLoader (@Nonnull final TikaMetadataLoader.Config config, @Nonnull final Path path)
            throws Exception
      {
        // given
        final TikaMetadataLoader underTest = new TikaMetadataLoader();
        // when
        final Metadata metadata = underTest.loadMetadata(path, config).getMeta();
        // then
        final String fileName = path.getFileName().toString() + "-dump.txt";
        final Path actualResult = ACTUAL_RESULT_FOLDER.resolve(fileName);
        final Path expectedResult = EXPECTED_RESULT_FOLDER.resolve(fileName);
        Files.createDirectories(ACTUAL_RESULT_FOLDER);
        dumpMetadata(metadata, actualResult, ITEMS_TO_IGNORE);
        assertSameContents(expectedResult, actualResult);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void testMetadataLoaderAlt()
            throws Exception
      {
        // given
        final TikaMetadataLoader.Config config = TikaMetadataLoader.Config.DEFAULT
                .withMetadataExtractor(TikaMetadataLoader.EXP_XMP_METADATA_EXTRACTOR);
        final Path path = TEST_RESOURCES.resolve("images/20210813-0091.xmp");
        final TikaMetadataLoader underTest = new TikaMetadataLoader();
        // when
        final Metadata metadata = underTest.loadMetadata(path, config).getMeta();
        // then
        final String fileName = path.getFileName().toString() + "-alt-dump.txt";
        final Path actualResult = ACTUAL_RESULT_FOLDER.resolve(fileName);
        final Path expectedResult = EXPECTED_RESULT_FOLDER.resolve(fileName);
        Files.createDirectories(ACTUAL_RESULT_FOLDER);
        dumpMetadata(metadata, actualResult, ITEMS_TO_IGNORE);
        assertSameContents(expectedResult, actualResult);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void testMetadataLoaderJempBox()
            throws Exception
      {
        // given
        final TikaMetadataLoader.Config config = TikaMetadataLoader.Config.DEFAULT
                .withContentReader(TikaMetadataLoader.EXP_XMP_PACKET_WRAPPER_CONTENT_READER)
                .withMetadataExtractor(TikaMetadataLoader.EXP_XMP_METADATA_EXTRACTOR_JEMPBOX);
        final Path path = TEST_RESOURCES.resolve("images/20210813-0091.xmp");
        final TikaMetadataLoader underTest = new TikaMetadataLoader();
        // when
        final Metadata metadata = underTest.loadMetadata(path, config).getMeta();
        // then
        final String fileName = path.getFileName().toString() + "-jempbox-dump.txt";
        final Path actualResult = ACTUAL_RESULT_FOLDER.resolve(fileName);
        final Path expectedResult = EXPECTED_RESULT_FOLDER.resolve(fileName);
        Files.createDirectories(ACTUAL_RESULT_FOLDER);
        dumpMetadata(metadata, actualResult, ITEMS_TO_IGNORE);
        assertSameContents(expectedResult, actualResult);
      }
  }
