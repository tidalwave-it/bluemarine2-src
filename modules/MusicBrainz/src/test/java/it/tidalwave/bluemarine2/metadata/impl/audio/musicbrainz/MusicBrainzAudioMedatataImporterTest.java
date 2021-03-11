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
package it.tidalwave.bluemarine2.metadata.impl.audio.musicbrainz;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.metadata.cddb.impl.DefaultCddbMetadataProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.util.ModelBuilder;
import it.tidalwave.bluemarine2.commons.test.TestSetTriple;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.metadata.cddb.impl.TestSupport;
import it.tidalwave.bluemarine2.metadata.musicbrainz.impl.DefaultMusicBrainzMetadataProvider;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.rest.CachingRestClientSupport.CacheMode.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class MusicBrainzAudioMedatataImporterTest extends TestSupport
  {
    private DefaultCddbMetadataProvider cddbMetadataProvider;

    private DefaultMusicBrainzMetadataProvider musicBrainzMetadataProvider;

//    private MusicBrainzAudioMedatataImporter underTest;

    private final Map<String, MusicBrainzAudioMedatataImporter> underTest = new TreeMap<>();

    private final Map<String, TestSetStats> stats = new TreeMap<>();

    private final Map<String, ModelBuilder> modelBuilders = new TreeMap<>();

    private final Set<String> unmatched = new TreeSet<>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static class TestSetStats
      {
        private final AtomicInteger count = new AtomicInteger(0);

        private final AtomicInteger found = new AtomicInteger(0);

        private final Set<String> withoutCddb = new TreeSet<>();

        @Override @Nonnull
        public String toString()
          {
            return String.format("matched: %s/%s (%d%%) - without CDDB: %s",
                                 found, count, (found.intValue() * 100) / count.intValue(), withoutCddb.size());
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeTest
    public void setup()
      {
        cddbMetadataProvider = new DefaultCddbMetadataProvider();
        musicBrainzMetadataProvider = new DefaultMusicBrainzMetadataProvider();

        cddbMetadataProvider.setCacheMode(USE_CACHE);
        musicBrainzMetadataProvider.setCacheMode(USE_CACHE);
//        underTest.initialize(); // FIXME

//        underTest = new MusicBrainzAudioMedatataImporter(cddbMetadataProvider, musicBrainzMetadataProvider);

        stats.clear();
        unmatched.clear();
        modelBuilders.clear();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @AfterClass
    public void printStats()
      {
        log.info("STATS: {}", stats.entrySet().stream().map(Object::toString).collect(joining(", ")));
        unmatched.forEach(path -> log.info("STATS: unmatched with CDDB: {}", path));
        stats.values().stream().flatMap(s -> s.withoutCddb.stream()).collect(toSet())
                .stream().forEachOrdered(path -> log.info("STATS: without CDDB:        {}", path));
        modelBuilders.entrySet().forEach(_c(entry -> verifyGlobalModel(entry.getValue().toModel(), entry.getKey())));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void verifyGlobalModel (final @Nonnull Model model, final @Nonnull String testSetName)
      throws RDFHandlerException, IOException
      {
        final String name = "musicbrainz-" + testSetName + ".n3";
        final Path actualFile = TEST_RESULTS.resolve(name);
        final Path expectedFile = Paths.get("target/test-classes/expected-results").resolve(name);
        exportToFile(model, actualFile);

        assertSameContents(expectedFile, actualFile);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "trackResourcesProvider2")
    public void must_correctly_retrieve_MusicBrainz_data (final @Nonnull TestSetTriple triple)
      throws Exception
      {
        // given
        final String testSetName = triple.getTestSetName();
        cddbMetadataProvider.setCachePath(CDDB_CACHE.resolve(testSetName));
        musicBrainzMetadataProvider.setCachePath(MUSICBRAINZ_CACHE.resolve(testSetName));
        final Metadata metadata = mockMetadataFrom(triple.getFilePath());

        final Path relativePath = Paths.get(triple.getRelativePath().getParent().toString()
                + metadata.get(DISK_COUNT).map(dc -> (dc.intValue() == 1) ? "" : metadata.get(DISK_NUMBER).map(n -> " _#" + n).orElse("")).orElse("")
                + ".n3");
        final Path actualResult = TEST_RESULTS.resolve("musicbrainz").resolve(testSetName).resolve(relativePath);
        final Path expectedResult = EXPECTED_RESULTS.resolve("musicbrainz").resolve(testSetName).resolve(relativePath);

        stats.putIfAbsent(testSetName, new TestSetStats());
        final TestSetStats testSetStats = stats.get(testSetName);
        underTest.putIfAbsent(testSetName, new MusicBrainzAudioMedatataImporter(cddbMetadataProvider, musicBrainzMetadataProvider));
        // when
        final Optional<Model> optionalModel = underTest.get(testSetName).handleMetadata(metadata);
        // then
        if (optionalModel.isPresent())
          {
            final String recordName = triple.getRelativePath().getParent().getFileName().toString();
            final Model model = optionalModel.get();
            final boolean matched = !model.isEmpty();
            final boolean hasCddb = metadata.get(CDDB).isPresent();

            if (!hasCddb)
              {
                testSetStats.withoutCddb.add(recordName);
              }
            else
              {
                testSetStats.count.incrementAndGet();
              }

            if (matched)
              {
                testSetStats.found.incrementAndGet();
                modelBuilders.putIfAbsent(testSetName, new ModelBuilder());
                modelBuilders.get(testSetName).with(model);
              }

            if (!matched && hasCddb)
              {
                unmatched.add(recordName + " / " + metadata.get(CDDB).get().getToc());
              }

            exportToFile(model, actualResult);
            assertSameContents(expectedResult, actualResult);
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] trackResourcesProvider2()
      {
        return streamOfTestSetTriples(TestSetLocator.allTestSets(), name -> METADATA.resolve(name))
                // Files there apparendly don't have CDDB offsets
                .filter(triple -> !triple.getTestSetName().equals("iTunes-aac-fg-20170131-1"))
                .filter(triple -> !triple.getTestSetName().equals("amazon-autorip-fg-20170131-1"))
                .collect(toTestNGDataProvider());
      }
  }
