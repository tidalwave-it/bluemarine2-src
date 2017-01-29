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
package it.tidalwave.bluemarine2.gracenote.api.impl;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.http.ResponseEntity;
import it.tidalwave.bluemarine2.gracenote.api.Album;
import it.tidalwave.bluemarine2.gracenote.api.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.TestSetTriple;
import static java.util.stream.Collectors.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static it.tidalwave.util.test.FileComparisonUtils8.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.*;
import static it.tidalwave.bluemarine2.gracenote.api.impl.DefaultGracenoteApi.CacheMode.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultGracenoteApiTest
  {
    private static final String PURE_ELLA_GN_ID = "161343049-DE60B292E7510AB532A959E2F8140814";

    private static final String PURE_ELLA_OFFSETS = "150 21860 38705 47155 68112 81095 99740 114517 131995 145947 "
                                                  + "163532 176950 188370 218577 241080 272992 287877 307292";

    private static final String CALLAS_LA_DIVINA_2_OFFSETS = "183 20100 39930 68110 81145 113055 132908 151000 172608 "
                                                           + "187760 211130 235045 256465 280275 306418 323443";

    private static final String RESPONSE_TXT = "response.txt";

    private static final Path GRACENOTE_CACHE = PATH_EXPECTED_TEST_RESULTS.resolve("gracenote/iTunes-fg-20160504-2/");

    private final static List<String> IGNORED_HEADERS =
            Arrays.asList("Date", "Server", "X-Powered-By", "Connection", "Keep-Alive", "Vary");

    private DefaultGracenoteApi underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        underTest = new DefaultGracenoteApi();
        underTest.initialize();
        underTest.setUserId(System.getProperty("gracenote.user", ""));
        underTest.setClientId(System.getProperty("gracenote.client", ""));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_retrieve_an_album_by_offsets()
      throws Exception
      {
        // given
        underTest.setCacheMode(ONLY_USE_CACHE);
        underTest.setCachePath(GRACENOTE_CACHE);
        // when
        final Response<Album> response = underTest.findAlbumByToc(PURE_ELLA_OFFSETS);
        // then
        assertThat(response.isPresent(), is(true));
        final Album album = response.get();
        assertThat(album.getGnId(), is(PURE_ELLA_GN_ID));
        assertThat(album.getArtist(), is("Ella Fitzgerald"));
        assertThat(album.getTitle(), is("Essential (Pure Ella) [Live]"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_retrieve_an_album_by_offsets_2() // FIXME: use @DataProvider
      throws Exception
      {
        // given
        underTest.setCacheMode(ONLY_USE_CACHE);
        underTest.setCachePath(GRACENOTE_CACHE);
        // when
        final Response<Album> response = underTest.findAlbumByToc(CALLAS_LA_DIVINA_2_OFFSETS);
        // then
        assertThat(response.isPresent(), is(true));
        final Album album = response.get();
        assertThat(album.getGnId(), is("38603393-EDE145149322F90ED2DD32958DB7AD49"));
        assertThat(album.getArtist(), is("Maria Callas"));
        assertThat(album.getTitle(), is("Callas La Divina 2"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_retrieve_an_album_by_GN_ID()
      throws Exception
      {
        // given
        underTest.setCacheMode(ONLY_USE_CACHE);
        underTest.setCachePath(GRACENOTE_CACHE);
       // when
        final Response<Album> response = underTest.findAlbumByToc(PURE_ELLA_OFFSETS);
        // then
        assertThat(response.isPresent(), is(true));
        final Album album = response.get();
        assertThat(album.getGnId(), is(PURE_ELLA_GN_ID));
        assertThat(album.getArtist(), is("Ella Fitzgerald"));
        assertThat(album.getTitle(), is("Essential (Pure Ella) [Live]"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_properly_report_on_offsets_not_recognized()
      throws Exception
      {
        // given
        underTest.setCacheMode(ONLY_USE_CACHE);
        underTest.setCachePath(GRACENOTE_CACHE);
        // when
        final Response<Album> response = underTest.findAlbumByToc("183 69955 94303 142758 206005 238393");
        // then
        assertThat(response.isPresent(), is(false));
      }

//    /*******************************************************************************************************************
//     *
//     ******************************************************************************************************************/
//    @Test
//    public void must_properly_report_on_GN_ID_not_found()
//      throws Exception
//      {
//        // given
//        underTest.setCacheMode(ALWAYS_USE_CACHE);
//        underTest.setCachePath(GRACENOTE_CACHE);
//       // when
//        final Response<Album> response = underTest.findAlbumByToc("invalid-gnid");
//        // then
//        assertThat(response.isPresent(), is(false));
//        final Album album = response.get();
//      }

    // FIXME: test cache miss, ALWAYS_USE_CACHE

    //////// TESTS BELOW USE THE NETWORK

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(groups = "no-ci")
    public void must_properly_query_album_TOC()
      throws Exception
      {
        // given
        underTest.setCacheMode(DONT_USE_CACHE);
        // when
        final ResponseEntity<String> response = underTest.queryAlbumToc(PURE_ELLA_OFFSETS);
        // then
        final Path actualResult = dump("queryAlbumTocResponse.txt", response);
        assertSameContents(gracenoteFilesPath("iTunes-fg-20160504-2").resolve("albumToc")
                                                                     .resolve(PURE_ELLA_OFFSETS.replace(' ', '/'))
                                                                     .resolve(RESPONSE_TXT),
                           actualResult);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(groups = "no-ci")
    public void must_properly_fetch_album_details()
      throws Exception
      {
        // given
        underTest.setCacheMode(DONT_USE_CACHE);
        // when
        final ResponseEntity<String> response = underTest.queryAlbumFetch(PURE_ELLA_GN_ID);
        // then
        final Path actualResult = dump("queryAlbumFetchResponse.txt", response);
        assertSameContents(gracenoteFilesPath("iTunes-fg-20160504-2").resolve("albumFetch")
                                                                     .resolve(PURE_ELLA_GN_ID)
                                                                     .resolve(RESPONSE_TXT),
                           actualResult);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "gracenoteResourcesProvider", groups = "no-ci")
    public void must_correctly_download_Gracenote_resources (final @Nonnull TestSetTriple triple)
      {
        // given
        underTest.setCacheMode(DONT_USE_CACHE);
        underTest.initialize(); // FIXME

        try
          {
            final Optional<String> iTunesComment = readiTunesCommentFrom(triple.getFilePath());
            log.info(">>>> {}", iTunesComment);

            final Path targetFolder = Paths.get("target/test-results/gracenote");

            if (iTunesComment.isPresent())
              {
                final String offsets = itunesCommentToAlbumToc(iTunesComment.get());
                final String p = "albumToc/" + offsets.replace(' ', '/') + "/" + RESPONSE_TXT;
                final String testSetName = triple.getTestSetName();
                final Path actualResult = targetFolder.resolve(testSetName).resolve(p);
                final Path expectedResult = gracenoteFilesPath(testSetName).resolve(p);

                if (!Files.exists(actualResult))
                  {
                    log.info(">>>> writing to {}", actualResult);
                    Files.createDirectories(actualResult.getParent());
                    // when
                    final ResponseEntity<String> response = underTest.queryAlbumToc(offsets);
                    // then
                    ResponseEntityIo.store(actualResult, response, IGNORED_HEADERS);
                    assertSameContents(expectedResult, actualResult);
                  }
              }
            else
              {
                  // TODO: write a file telling that there are no offsets, so you can assert it
              }

          }
        catch (IOException e)
          {
            log.error("", e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<String> readiTunesCommentFrom (final @Nonnull Path path)
      throws IOException
      {
        return Files.lines(path, UTF_8).filter(s -> s.contains("[iTunes.comment]"))
                                       .findFirst()
                                       .map(s -> s.replaceAll("^.*cddb1=", "").replaceAll(", .*$", ""));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] gracenoteResourcesProvider()
      {
//        final String testSetName = "iTunes-fg-20161210-1"; // FIXME: use both
        final String testSetName = "iTunes-fg-20160504-2";

        return streamOfTestSetTriples(Arrays.asList(testSetName),
                                      name -> Paths.get("target/metadata").resolve(name))
                            .collect(toTestNGDataProvider());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String itunesCommentToAlbumToc (final @Nonnull String comment)
      {
        return Stream.of(comment.split("\\+")).skip(3).collect(joining(" "));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Path dump (final @Nonnull String resourceName, final @Nonnull ResponseEntity<String> response)
      throws IOException
      {
        final Path actualResult = PATH_TEST_RESULTS.resolve(resourceName);
        ResponseEntityIo.store(actualResult, response, IGNORED_HEADERS);
        return actualResult;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Path gracenoteFilesPath (final @Nonnull String testSet)
      {
        return PATH_EXPECTED_TEST_RESULTS.resolve("gracenote").resolve(testSet);
      }
  }