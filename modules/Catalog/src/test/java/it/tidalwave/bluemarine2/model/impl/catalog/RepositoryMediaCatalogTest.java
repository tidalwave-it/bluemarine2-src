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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.testng.annotations.DataProvider;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import static java.util.stream.Collectors.*;
import static java.nio.file.Files.*;
import static it.tidalwave.bluemarine2.util.Miscellaneous.*;
import static it.tidalwave.util.test.FileComparisonUtils.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;

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

    private static final Comparator<Object> BY_RDFS_LABEL =
            (e1, e2) -> ((RepositoryEntitySupport)e1).getRdfsLabel().compareTo(
                        ((RepositoryEntitySupport)e2).getRdfsLabel());

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
    public void must_properly_query_the_whole_catalog_in_various_ways (final @Nonnull String testSetName)
      throws Exception
      {
        // given
        final Repository repository = loadInMemoryCatalog(PATH_TEST_SETS.resolve(testSetName + ".n3"));
        // when
        final MediaCatalog underTest = new RepositoryMediaCatalog(repository);
        // then
        final Path expectedResult = PATH_EXPECTED_TEST_RESULTS.resolve(testSetName + "-dump.txt");
        final Path actualResult = PATH_TEST_RESULTS.resolve(testSetName + "-dump.txt");
        queryAndDump(underTest, actualResult);
        assertSameContents(normalizedPath(expectedResult).toFile(), normalizedPath(actualResult).toFile());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void queryAndDump (final @Nonnull MediaCatalog catalog, final @Nonnull Path dumpPath)
      throws IOException
      {
        log.info("queryAndDump(.., {})", dumpPath);
        createDirectories(PATH_TEST_RESULTS);
        final PrintWriter pw = new PrintWriter(dumpPath.toFile(), "UTF-8");

        final List<? extends MusicArtist> artists = catalog.findArtists().stream().sorted(BY_RDFS_LABEL).collect(toList());
        final List<? extends Record> records = catalog.findRecords().stream().sorted(BY_RDFS_LABEL).collect(toList());

        pw.println("ALL TRACKS:\n");
        final Map<String, RepositoryTrack> allTracks = catalog.findTracks().results().stream()
                        .map(t -> (RepositoryTrack)t)
                        .collect(toMap(RepositoryTrack::toString, Function.identity()));
        allTracks.values().stream().sorted(BY_RDFS_LABEL).forEach(track -> pw.printf("  %s\n", track));

        pw.println("\n\n\nALL RECORDS:\n");
        records.forEach(artist -> pw.printf("%s\n", artist));

        pw.println("\n\n\nALL ARTISTS:\n");
        artists.forEach(artist -> pw.printf("%s\n", artist));

        artists.forEach(artist ->
          {
            pw.printf("\nTRACKS OF %s:\n", artist);
            artist.findTracks().stream().forEach(track ->
              {
                pw.printf("  %s\n", track);
                allTracks.remove(track.toString());
              });
          });

        records.forEach(record ->
          {
            pw.printf("\nTRACKS IN %s:\n", record);
            record.findTracks().stream().forEach(track ->
              {
                pw.printf("  %s\n", track);
//                allTracks.remove(track.toString()); FIXME: check orphans of Record too
              });
          });

        artists.forEach(artist ->
          {
            pw.printf("\nRECORDS OF %s:\n", artist);
            artist.findRecords().stream().forEach(record ->
              {
                pw.printf("  %s\n", record);
              });
          });

        pw.println("\n\nORPHANED TRACKS:\n");
        allTracks.values().stream().sorted(BY_RDFS_LABEL).forEach(track -> pw.printf("  %s\n", track));

        pw.close();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Repository loadInMemoryCatalog (final @Nonnull Path path)
      throws RDFParseException, IOException, RepositoryException
      {
        log.info("loadInMemoryCatalog({})", path);
        final Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();

        final RepositoryConnection connection = repository.getConnection();
        connection.add(path.toFile(), null, RDFFormat.N3);
        connection.commit();
        connection.close();

        return repository;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] testSetNamesProvider()
      {
        return new Object[][]
          {
              { "tiny-model"                    },
              { "small-model"                   },
              { "model-iTunes-fg-20160504-1"    },
              { "model-iTunes-fg-20161210-1"    },
          };
      }
  }
