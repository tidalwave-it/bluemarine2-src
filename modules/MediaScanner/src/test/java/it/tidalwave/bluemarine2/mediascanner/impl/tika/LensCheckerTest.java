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
import java.io.IOException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.tika.metadata.Metadata;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class LensCheckerTest extends TestSupport
  {
    private LensChecker underTest;

    @BeforeClass
    public void init()
      {
        underTest = new LensChecker();
      }

    @AfterClass
    public void complete()
      {
        underTest.getStatistics().forEach((lens, count) -> log.info("LENS {} : {}", lens, count));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "testFileData")
    public void testSingleFile (@Nonnull final TikaMetadataLoader.Config config, @Nonnull final Path path)
            throws Exception
      {
        // given
        final TikaMetadataLoader metadataLoader = new TikaMetadataLoader();
        final Metadata metadata = metadataLoader.loadMetadata(path, config).getMeta();
        final MetadataWithPath xmpMetadata = new MetadataWithPath(path, metadata);
        final LensChecker underTest = new LensChecker();
        // when
        underTest.accept(xmpMetadata);
        // then
        // TODO - assertions
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "testFolderData", enabled = true)
    public void testFolderScan (@Nonnull final Path basePath)
            throws IOException
      {
        // given
        final Scanner.Params params = Scanner.params().withFilter(Scanner.Params.extensionFilter("xmp"));
        final TikaMetadataLoader metadataLoader = new TikaMetadataLoader();
        final Scanner scanner = new Scanner(metadataLoader);
        // when
        scanner.scan(basePath, params, underTest);
        // then
        // TODO - assertions
      }
  }
