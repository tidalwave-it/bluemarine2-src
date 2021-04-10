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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.MusicPerformer;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.audio.Track;
import it.tidalwave.bluemarine2.model.finder.audio.AudioFileFinder;
import it.tidalwave.bluemarine2.model.finder.audio.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.audio.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryFinderSupport;
import it.tidalwave.bluemarine2.model.spi.Entity;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import static java.util.stream.Collectors.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.*;
import static it.tidalwave.util.test.FileComparisonUtilsWithPathNormalizer.*;
import static it.tidalwave.role.Identifiable._Identifiable_;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.bluemarine2.model.vocabulary.BMMO.*;
import static org.testng.Assert.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;
import static it.tidalwave.bluemarine2.commons.test.TestUtilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class RepositoryMediaCatalogTest extends SpringTestSupport
  {
    private static final Path PATH_TEST_SETS = Paths.get("target/test-classes/test-sets");

    private static final Comparator<Entity> BY_DISPLAY_NAME =
            (e1, e2) -> e1.as(_Displayable_).getDisplayName().compareTo(e2.as(_Displayable_).getDisplayName());

    private static int latestQueryCount;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public RepositoryMediaCatalogTest()
      {
        super("META-INF/DciAutoBeans.xml",
              "META-INF/RepositoryCatalogTestBeans.xml");
      }

    /*******************************************************************************************************************
     *
     * Queries the catalog for the whole data in various ways and dumps the results to check the consistency.
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "testSetNamesProvider")
    public void must_properly_query_the_whole_catalog_in_various_ways (@Nonnull final String testSetName,
                                                                       @Nullable final String otherTestSetName,
                                                                       @Nonnull final Id source,
                                                                       @Nonnull final Id fallbackSource)
      throws Exception
      {
        // given
        final Repository repository = context.getBean(Repository.class);

        if (otherTestSetName != null)
          {
            loadRepository(repository, PATH_TEST_SETS.resolve(otherTestSetName + ".n3"));
          }

        loadRepository(repository, PATH_TEST_SETS.resolve(testSetName + ".n3"));
        final RepositoryMediaCatalog underTest = context.getBean(RepositoryMediaCatalog.class);
        underTest.setSource(source);
        underTest.setFallback(fallbackSource);
        final Path expectedResult = PATH_EXPECTED_TEST_RESULTS.resolve(testSetName + "-dump.txt");
        final Path actualResult = PATH_TEST_RESULTS.resolve(testSetName + "-dump.txt");
        // when
        queryAndDump(underTest, actualResult);
        // then
        assertSameContents(expectedResult, actualResult);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void queryAndDump (@Nonnull final RepositoryMediaCatalog catalog, @Nonnull final Path dumpPath)
      throws IOException
      {
        log.info("queryAndDump(.., {})", dumpPath);
        createDirectories(PATH_TEST_RESULTS);
        RepositoryFinderSupport.resetQueryCount();
        latestQueryCount = 0;
        final PrintWriter pw = new PrintWriter(dumpPath.toFile(), UTF_8);

        final MusicArtistFinder allArtistsFinder = catalog.findArtists();
        final RecordFinder allRecordsFinder = catalog.findRecords();
        final TrackFinder allTracksFinder = catalog.findTracks();

        log.info("QUERYING ALL ARTISTS...");
        final List<MusicArtist> artists = allArtistsFinder.stream().sorted(BY_DISPLAY_NAME).collect(toList());
        final int artistsQueryCount = getLatestQueryCount();

        getLatestQueryCount();
        log.info("QUERYING ALL TRACKS...");
        pw.printf("ALL TRACKS (%d):%n%n", allTracksFinder.count());
        final int trackCountQueryCount = getLatestQueryCount();
        final Map<String, Track> tracksOrphanOfArtist = allTracksFinder.stream()
                                                            .collect(toMap(Track::toDumpString, Function.identity(), (u,v) -> v));
        final Map<String, Track> tracksOrphanOfRecord = new HashMap<>(tracksOrphanOfArtist);
        tracksOrphanOfArtist.values().stream().sorted(BY_DISPLAY_NAME).forEach(track -> pw.printf("  %s%n", track.toDumpString()));
        pw.printf("  COUNT OF ALL TRACKS RETRIEVED BY %d QUERIES%n", trackCountQueryCount);
        pw.printf("  ALL TRACKS RETRIEVED BY %d QUERIES%n", getLatestQueryCount());

        pw.printf("%n%n%nALL RECORDS (%d):%n%n", allRecordsFinder.count());
        final int recordCountQueryCount = getLatestQueryCount();
        log.info("QUERYING ALL RECORDS...");
        final List<Record> records = allRecordsFinder.stream().sorted(BY_DISPLAY_NAME).collect(toList());
        records.forEach(record -> pw.printf("  %s - %d tracks - %s%n",
                displayNameOf(record),
                record.findTracks().count(),
                record.getSource().orElse(Id.of("unknown"))));
        pw.printf("  COUNT OF ALL RECORDS RETRIEVED BY %d QUERIES%n", recordCountQueryCount);
        pw.printf("  ALL RECORDS RETRIEVED BY %d QUERIES%n", getLatestQueryCount());

        pw.printf("%n%n%nALL ARTISTS (%d):%n%n", allArtistsFinder.count());
        final int artistCountQueryCount = getLatestQueryCount();
        artists.forEach(artist -> pw.printf("  %s - %s%n", displayNameOf(artist), artist.getSource().orElse(Id.of("unknown"))));
        pw.printf("  COUNT OF ALL ARTISTS RETRIEVED BY %d QUERIES%n", artistCountQueryCount);
        pw.printf("  ALL ARTISTS RETRIEVED BY %d QUERIES%n", artistsQueryCount);

        artists.forEach(artist ->
          {
            log.info("QUERYING TRACKS OF {}...", displayNameOf(artist));
            final TrackFinder artistTracksFinder = artist.findTracks();
            pw.printf("%nTRACKS OF %s (%d):%n", displayNameOf(artist), artistTracksFinder.count());
            artistTracksFinder.stream().forEach(track ->
              {
                pw.printf("  %s%n", track.toDumpString());
                tracksOrphanOfArtist.remove(track.toDumpString());
                assertEquals(track.getSource(), artist.getSource());
              });

            pw.printf("  RETRIEVED BY %d QUERIES%n", getLatestQueryCount());
          });

        artists.forEach(artist ->
          {
            log.info("QUERYING RECORDS OF {}...", displayNameOf(artist));
            final RecordFinder recordFinder = artist.findRecords();
            pw.printf("%nRECORDS OF %s (%d):%n", displayNameOf(artist), recordFinder.count());
            recordFinder.stream().forEach(record -> pw.printf("  %s%n", displayNameOf(record)));
            recordFinder.stream().forEach(record -> assertEquals(record.getSource(), artist.getSource()));
            pw.printf("  RETRIEVED BY %d QUERIES%n", getLatestQueryCount());
          });

        artists.forEach(artist ->
          {
            log.info("QUERYING PERFORMANCES OF {}...", displayNameOf(artist));
            final PerformanceFinder performanceFinder = artist.findPerformances();
            pw.printf("%nPERFORMANCES OF %s (%d):%n", displayNameOf(artist), performanceFinder.count());
            performanceFinder.stream().forEach(performance -> pw.printf("  %s%n", performance.toDumpString()));
            performanceFinder.stream().forEach(performance -> assertEquals(performance.getSource(), artist.getSource()));
            pw.printf("  RETRIEVED BY %d QUERIES%n", getLatestQueryCount());
          });

        records.forEach(record ->
          {
            pw.printf("%nRECORD %s:%n", displayNameOf(record));
            pw.printf("  SOURCE:  %s%n", record.getSource().orElse(Id.of("unknown")));
            record.getAsin().ifPresent(asin -> pw.printf("  ASIN:    %s%n", asin));
            record.getGtin().ifPresent(gtin -> pw.printf("  BARCODE: %s%n", gtin));

            log.info("QUERYING TRACKS OF {}...", displayNameOf(record));
            final TrackFinder recordTrackFinder = record.findTracks();
            pw.printf("  TRACKS (%d / %s):%n", recordTrackFinder.count(), record.getTrackCount());
//            record.getTrackCount().ifPresent(trackCount -> assertEquals(trackCount.intValue(), recordTrackFinder.count())); FIXME

            recordTrackFinder.stream().forEach(track ->
              {
                pw.printf("    %s%n", track.toDumpString());
                tracksOrphanOfRecord.remove(track.toDumpString());
                track.getPerformance().ifPresent(performance -> pw.printf("%s%n",
                        performance.findPerformers().stream()
                                                    .sorted(BY_DISPLAY_NAME)
                                                    .map(this::displayNameOf)
                                                    .collect(joining("\n      : ", "      : ", ""))));
                assertEquals(track.getSource(), record.getSource());
              });

            pw.printf("  RETRIEVED BY %d QUERIES%n", getLatestQueryCount());
          });

        log.info("QUERYING ALL AUDIO TRACKS...");
        final AudioFileFinder allAudioFileFinder = catalog.findAudioFiles();
        pw.printf("%n%n%nALL AUDIO FILES (%d):%n%n", allAudioFileFinder.count());
        allAudioFileFinder.results().forEach(audioFile -> pw.printf("  %s%n", audioFile.toDumpString()));
        // FIXME: allAudioFileFinder.forEach(audioFile -> pw.printf("  %s%n", audioFile.toDumpString()));

        pw.printf("%n%nTRACKS ORPHAN OF ARTIST (%d):%n%n", tracksOrphanOfArtist.size());
        tracksOrphanOfArtist.values().stream().sorted(BY_DISPLAY_NAME).forEach(track -> pw.printf("  %s%n", track.toDumpString()));

        pw.printf("%n%nTRACKS ORPHAN OF RECORD (%d):%n%n", tracksOrphanOfRecord.size());
        tracksOrphanOfRecord.values().stream().sorted(BY_DISPLAY_NAME).forEach(track -> pw.printf("  %s%n", track.toDumpString()));

        pw.printf("TOTAL NUMBER OF QUERIES: %d%n", RepositoryFinderSupport.getQueryCount());
        pw.close();
      }

    /*******************************************************************************************************************
     *
     * Assumes strictly sequential testing.
     *
     ******************************************************************************************************************/
    @Nonnegative
    private int getLatestQueryCount()
      {
        final int previousCount = latestQueryCount;
        latestQueryCount = RepositoryFinderSupport.getQueryCount();
        return latestQueryCount - previousCount;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private String displayNameOf (@Nonnull final MusicPerformer musicPerformer)
      {
        final MusicArtist artist = musicPerformer.getMusicArtist();
        final Optional<Entity> role = musicPerformer.getRole();
        final String performer = role.get().as(_Displayable_).getDisplayName().replaceAll("^performer_", "").replace('_', ' ');
        return String.format("%-20s %s", performer, displayNameOf(artist));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private String displayNameOf (@Nonnull final Entity entity)
      {
        return String.format("%s (%s)", entity.as(_Displayable_).getDisplayName(), entity.as(_Identifiable_).getId());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] testSetNamesProvider()
      {
        return new Object[][]
          {
            // FIXME: prepare a new tiny-model
//            { "tiny-model"                      , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "small-model"                     , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "small-model-2"                   , null,                         ID_SOURCE_MUSICBRAINZ, ID_SOURCE_MUSICBRAINZ },
            { "model-iTunes-fg-20160504-2"      , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "model-iTunes-fg-20161210-1"      , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "model-iTunes-aac-fg-20170131-1"      , null,                     ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "model-amazon-autorip-fg-20170131-1"  , null,                     ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "musicbrainz-iTunes-fg-20160504-2", "model-iTunes-fg-20160504-2", ID_SOURCE_MUSICBRAINZ, ID_SOURCE_EMBEDDED },
            { "musicbrainz-iTunes-fg-20161210-1", "model-iTunes-fg-20161210-1", ID_SOURCE_MUSICBRAINZ, ID_SOURCE_EMBEDDED },
          };
      }
  }
