/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import it.tidalwave.bluemarine2.metadata.cddb.impl.DefaultCddbMetadataProvider;
import it.tidalwave.bluemarine2.metadata.cddb.impl.TestSupport;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.TestSetTriple;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import static it.tidalwave.bluemarine2.rest.CachingRestClientSupport.CacheMode.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.streamOfTestSetTriples;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.toTestNGDataProvider;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMusicBrainzMetadataProviderTest extends TestSupport
  {
    private DefaultCddbMetadataProvider cddbMetadataProvider;

    private DefaultMusicBrainzMetadataProvider underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeTest
    public void setup()
      {
        cddbMetadataProvider = new DefaultCddbMetadataProvider();
        underTest = new DefaultMusicBrainzMetadataProvider();

        cddbMetadataProvider.setCacheMode(ONLY_USE_CACHE);
        underTest.setCacheMode(ONLY_USE_CACHE);
        underTest.setThrottleLimit(1500);
//        underTest.initialize(); // FIXME
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "trackResourcesProvider2")
    public void must_correctly_download_MusicBrainz_resources (final @Nonnull TestSetTriple triple)
      throws Exception
      {
        // given
        final Path relativePath = triple.getRelativePath();
        final String testSetName = triple.getTestSetName();
        final Path actualResult = TEST_RESULTS.resolve(testSetName).resolve(relativePath);
        final Path expectedResult = EXPECTED_RESULTS.resolve("musicbrainz").resolve(testSetName).resolve(relativePath);
        cddbMetadataProvider.setCachePath(CDDB_CACHE.resolve(testSetName));
        underTest.setCachePath(MUSICBRAINZ_CACHE.resolve(testSetName));

        final String albumTitle = readAlbumTitleFrom(triple.getFilePath()).get();

//        final Optional<ITunesComment> iTunesComment = readiTunesCommentFrom(triple.getFilePath());
//        final CddbResponse<CddbAlbum> album = cddbMetadataProvider.findCddbAlbum(iTunesComment.get());
//
//        if (album.isPresent())
//          {
//            albumTitle = album.get().getProperty("DTITLE").get();
//          }

        // when
        if (!albumTitle.trim().isEmpty())
          {
            underTest.findReleaseGroup(albumTitle);
          }
//            response = underTest.findCddbAlbum(iTunesComment.get());
        // then
//            string = response.map(CddbAlbum::toDumpString).orElse("NOT FOUND");

//        log.info(">>>> writing to {}", actualResult);
//        Files.createDirectories(actualResult.getParent());
//        Files.write(actualResult, singletonList(string), UTF_8);
//        assertSameContents(expectedResult, actualResult);

      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] trackResourcesProvider2()
      {
        return streamOfTestSetTriples(TestSetLocator.allTestSets(), name -> METADATA.resolve(name))
//                .limit(1000)
                                                    .collect(toTestNGDataProvider());
      }
  }
