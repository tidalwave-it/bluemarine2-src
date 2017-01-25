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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import javax.annotation.CheckForNull;
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
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.testng.annotations.DataProvider;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.MusicPerformer;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.role.Entity;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import static java.util.stream.Collectors.*;
import static java.nio.file.Files.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.Identifiable.Identifiable;
import static it.tidalwave.bluemarine2.util.Miscellaneous.*;
import static it.tidalwave.util.test.FileComparisonUtils.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;
import static it.tidalwave.bluemarine2.model.vocabulary.BM.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class RepositoryMediaCatalogTest extends SpringTestSupport
  {
    private static final Path PATH_TEST_SETS = Paths.get("target/test-classes/test-sets");

    private static final Comparator<Entity> BY_DISPLAY_NAME =
            (e1, e2) -> e1.as(Displayable).getDisplayName().compareTo(e2.as(Displayable).getDisplayName());

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
    public void must_properly_query_the_whole_catalog_in_various_ways (final @Nonnull String testSetName,
                                                                       final @CheckForNull String otherTestSetName,
                                                                       final @Nonnull Id source,
                                                                       final @Nonnull Id fallbackSource)
      throws Exception
      {
        // given
        final Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();

        if (otherTestSetName != null)
          {
            loadInMemoryCatalog(repository, PATH_TEST_SETS.resolve(otherTestSetName + ".n3"));
          }

        loadInMemoryCatalog(repository, PATH_TEST_SETS.resolve(testSetName + ".n3"));
        // when
        final RepositoryMediaCatalog underTest = new RepositoryMediaCatalog(repository);
        underTest.setSource(source);
        underTest.setFallback(fallbackSource);
        // then
        final Path expectedResult = PATH_EXPECTED_TEST_RESULTS.resolve(testSetName + "-dump.txt");
        final Path actualResult = PATH_TEST_RESULTS.resolve(testSetName + "-dump.txt");
        queryAndDump(underTest, actualResult);
        assertSameContents(normalizedPath(expectedResult).toFile(), normalizedPath(actualResult).toFile());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void queryAndDump (final @Nonnull RepositoryMediaCatalog catalog, final @Nonnull Path dumpPath)
      throws IOException
      {
        log.info("queryAndDump(.., {})", dumpPath);
        createDirectories(PATH_TEST_RESULTS);
        final PrintWriter pw = new PrintWriter(dumpPath.toFile(), "UTF-8");

        final MusicArtistFinder allArtistsFinder = catalog.findArtists();
        final RecordFinder allRecordsFinder = catalog.findRecords();
        final TrackFinder allTracksFinder = catalog.findTracks();

        final List<MusicArtist> artists = allArtistsFinder.stream().sorted(BY_DISPLAY_NAME).collect(toList());
        final List<Record> records = allRecordsFinder.stream().sorted(BY_DISPLAY_NAME).collect(toList());

        pw.printf("ALL TRACKS (%d):%n%n", allTracksFinder.count());
        final Map<String, Track> tracksOrphanOfArtist = allTracksFinder.stream()
                                                            .collect(toMap(Track::toString, Function.identity(), (u,v) -> v));
        final Map<String, Track> tracksOrphanOfRecord = new HashMap<>(tracksOrphanOfArtist);
        tracksOrphanOfArtist.values().stream().sorted(BY_DISPLAY_NAME).forEach(track -> pw.printf("  %s%n", track));

        pw.printf("%n%n%nALL RECORDS (%d):%n%n", allRecordsFinder.count());
        records.forEach(record -> pw.printf("%s - %s%n", displayNameOf(record), record.getSource().orElse(new Id("unknown"))));

        pw.printf("%n%n%nALL ARTISTS (%d):%n%n", allArtistsFinder.count());
        artists.forEach(artist -> pw.println(displayNameOf(artist)));

        artists.forEach(artist ->
          {
            final TrackFinder artistTracksFinder = artist.findTracks();
            pw.printf("%nTRACKS OF %s (%d):%n", displayNameOf(artist), artistTracksFinder.count());
            artistTracksFinder.stream().forEach(track ->
              {
                pw.printf("  %s%n", track);
                tracksOrphanOfArtist.remove(track.toString());
              });
          });

        records.forEach(record ->
          {
            pw.printf("%nRECORD %s:%n", displayNameOf(record));
            pw.printf("  SOURCE:  %s%n", record.getSource().orElse(new Id("unknown")));
            record.getAsin().ifPresent(asin -> pw.printf("  ASIN:    %s%n", asin));
            record.getGtin().ifPresent(gtin -> pw.printf("  BARCODE: %s%n", gtin));

            final TrackFinder recordTrackFinder = record.findTracks();
            pw.printf("  TRACKS (%d / %s):%n", recordTrackFinder.count(), ((RepositoryRecord)record).getTrackCount());

            recordTrackFinder.stream().forEach(track ->
              {
                pw.printf("    %s%n", track);
                tracksOrphanOfRecord.remove(track.toString());
                track.getPerformance().ifPresent(performance -> pw.printf("%s%n",
                        performance.findPerformers().stream()
                                                    .sorted(BY_DISPLAY_NAME)
                                                    .map(this::displayNameOf)
                                                    .collect(joining("\n      : ", "      : ", ""))));

              });
          });

        artists.forEach(artist ->
          {
            final RecordFinder recordFinder = artist.findRecords();
            pw.printf("%nRECORDS OF %s (%d):%n", displayNameOf(artist), recordFinder.count());
            recordFinder.stream().forEach(record -> pw.printf("  %s%n", displayNameOf(record)));
          });

        artists.forEach(artist ->
          {
            final PerformanceFinder performanceFinder = artist.findPerformances();
            pw.printf("%nPERFORMANCES OF %s (%d):%n", displayNameOf(artist), performanceFinder.count());
            performanceFinder.stream().forEach(performance -> pw.printf("  %s%n", performance.toDumpString()));
          });

        pw.printf("%n%nTRACKS ORPHAN OF ARTIST (%d):%n%n", tracksOrphanOfArtist.size());
        tracksOrphanOfArtist.values().stream().sorted(BY_DISPLAY_NAME).forEach(track -> pw.printf("  %s%n", track));

        pw.printf("%n%nTRACKS ORPHAN OF RECORD (%d):%n%n", tracksOrphanOfRecord.size());
        tracksOrphanOfRecord.values().stream().sorted(BY_DISPLAY_NAME).forEach(track -> pw.printf("  %s%n", track));

        pw.close();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private String displayNameOf (final @Nonnull MusicPerformer musicPerformer)
      {
        final MusicArtist artist = musicPerformer.getMusicArtist();
        final Optional<Entity> role = musicPerformer.getRole();
        final String performer = role.get().as(Displayable).getDisplayName().replaceAll("^performer_", "").replace('_', ' ');
        return String.format("%-20s %s", performer, displayNameOf(artist));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private String displayNameOf (final @Nonnull Entity entity)
      {
        return String.format("%s (%s)", entity.as(Displayable).getDisplayName(), entity.as(Identifiable).getId());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static void loadInMemoryCatalog (final @Nonnull Repository repository, final @Nonnull Path path)
      throws RDFParseException, IOException, RepositoryException
      {
        log.info("loadInMemoryCatalog(..., {})", path);

        try (final RepositoryConnection connection = repository.getConnection())
          {
            connection.add(path.toFile(), null, RDFFormat.N3);
            connection.commit();
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] testSetNamesProvider()
      {
        return new Object[][]
          {
            { "tiny-model"                      , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "small-model"                     , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "model-iTunes-fg-20160504-2"      , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "model-iTunes-fg-20161210-1"      , null,                         ID_SOURCE_EMBEDDED,    ID_SOURCE_EMBEDDED },
            { "musicbrainz-iTunes-fg-20160504-2", "model-iTunes-fg-20160504-2", ID_SOURCE_MUSICBRAINZ, ID_SOURCE_EMBEDDED },
            { "musicbrainz-iTunes-fg-20161210-1", "model-iTunes-fg-20161210-1", ID_SOURCE_MUSICBRAINZ, ID_SOURCE_EMBEDDED },
          };
      }
  }
