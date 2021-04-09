/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.metadata.cddb.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Files;
import it.tidalwave.bluemarine2.rest.RestResponse;
import it.tidalwave.bluemarine2.metadata.cddb.CddbAlbum;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import it.tidalwave.bluemarine2.commons.test.TestSetTriple;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.test.FileComparisonUtilsWithPathNormalizer.*;
import static it.tidalwave.bluemarine2.rest.CachingRestClientSupport.CacheMode.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultCddbMetadataProviderTest extends TestSupport
  {
    private DefaultCddbMetadataProvider underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeTest
    public void setup()
      {
        underTest = new DefaultCddbMetadataProvider();
        underTest.setCacheMode(ONLY_USE_CACHE);
//        underTest.initialize(); // FIXME
      }

    /*******************************************************************************************************************
     *
     * Test sets iTunes-fg-20160504-1 and iTunes-fg-20161210-1 manually validated on 2017-01-07 18:00.
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "trackResourcesProvider")
    public void must_correctly_download_CDDB_resources (final @Nonnull TestSetTriple triple)
      throws Exception
      {
        // given
        final Path relativePath = triple.getRelativePath();
        final String testSetName = triple.getTestSetName();
        final Path actualResult = TEST_RESULTS.resolve("cddb").resolve(testSetName).resolve(relativePath);
        final Path expectedResult = EXPECTED_RESULTS.resolve("cddb").resolve(testSetName).resolve(relativePath);
        underTest.setCachePath(CDDB_CACHE.resolve(testSetName));

        final Metadata metadata = mockMetadataFrom(triple.getFilePath());
        final Optional<Cddb> cddb = metadata.get(CDDB);
        // when
        final RestResponse<CddbAlbum> response = underTest.findCddbAlbum(metadata);
        // then
        final String string = response.map(CddbAlbum::toDumpString).orElse(cddb.isPresent() ? "NOT FOUND" : "NO ITUNES COMMENT");

        log.info(">>>> writing to {}", actualResult);
        Files.createDirectories(actualResult.getParent());
        Files.write(actualResult, singletonList(string), UTF_8);
        assertSameContents(expectedResult, actualResult);
      }
  }