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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.DataProvider;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static java.nio.file.Files.*;
import static it.tidalwave.util.test.FileComparisonUtils.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;
import org.testng.annotations.BeforeMethod;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class RepositoryCatalogTest
  {
    private static final Path PATH_TEST_SETS = Paths.get("target/test-classes/test-sets");

    private ApplicationContext context;

    private MessageBus messageBus;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        context = new ClassPathXmlApplicationContext("META-INF/CommonsAutoBeans.xml",
                                                     "META-INF/RepositoryCatalogTestBeans.xml");
        // FIXME: this is unneeded; just to avoid a runtime error from the file system - exclude it from the test setup
        messageBus = context.getBean(MessageBus.class);
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH, Paths.get("/base/path"));
        messageBus.publish(new PowerOnNotification(properties));
      }

    /*******************************************************************************************************************
     *
     * Queries the catalog for the whole data in various ways and dumps the results to check the consistency.
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "testSetNamesProvider", groups = "no-ci") // On Linux fails because of BMT-46
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
        dumpQueries(underTest, actualResult);
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void dumpQueries (final @Nonnull MediaCatalog catalog, final @Nonnull Path dumpPath)
      throws IOException
      {
        log.info("dumpQueries({})", dumpPath);
        createDirectories(PATH_TEST_RESULTS);
        final PrintWriter pw = new PrintWriter(dumpPath.toFile(), "UTF-8");

        final List<? extends MusicArtist> artists = catalog.findArtists().results();
        final List<? extends Record> records = catalog.findRecords().results();

        pw.println("ALL TRACKS:\n");
        final Map<String, RepositoryTrack> allTracks = catalog.findTracks().results().stream()
                        .map(t -> (RepositoryTrack)t)
                        .collect(Collectors.toMap(RepositoryTrack::toString, Function.identity()));
        final Comparator<RepositoryTrack> c = (o1, o2) -> o1.getRdfsLabel().compareTo(o2.getRdfsLabel());
        allTracks.values().stream().sorted(c).forEach(track -> pw.printf("  %s\n", track));

        pw.println("\n\n\nALL RECORDS:\n");
        records.forEach(artist -> pw.printf("%s\n", artist));

        pw.println("\n\n\nALL ARTISTS:\n");
        artists.forEach(artist -> pw.printf("%s\n", artist));

        // FIXME: not correctly sorted in many cases

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
        allTracks.values().stream().sorted(c).forEach(track -> pw.printf("  %s\n", track));

        pw.close();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Repository loadInMemoryCatalog (final @Nonnull Path path)
      throws RDFParseException, IOException, RepositoryException
      {
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
          };
      }
  }
