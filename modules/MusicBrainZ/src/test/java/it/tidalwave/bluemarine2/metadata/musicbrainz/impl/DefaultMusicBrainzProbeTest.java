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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.musicbrainz.ns.mmd_2.DefTrackData;
import org.musicbrainz.ns.mmd_2.Recording;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.metadata.cddb.impl.DefaultCddbMetadataProvider;
import it.tidalwave.bluemarine2.metadata.musicbrainz.impl.DefaultMusicBrainzProbe.ReleaseAndMedium;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.TestSetTriple;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.metadata.cddb.impl.TestSupport;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents;
import static it.tidalwave.bluemarine2.rest.CachingRestClientSupport.CacheMode.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMusicBrainzProbeTest extends TestSupport
  {
    private DefaultCddbMetadataProvider cddbMetadataProvider;

    private DefaultMusicBrainzMetadataProvider musicBrainzMetadataProvider;

    private DefaultMusicBrainzProbe underTest;

    private final Map<String, AtomicInteger> countMap = new TreeMap<>();

    private final Map<String, AtomicInteger> foundMap = new TreeMap<>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeTest
    public void setup()
      {
        cddbMetadataProvider = new DefaultCddbMetadataProvider();
        musicBrainzMetadataProvider = new DefaultMusicBrainzMetadataProvider();

        cddbMetadataProvider.setCacheMode(ONLY_USE_CACHE);
        musicBrainzMetadataProvider.setCacheMode(ONLY_USE_CACHE);
        musicBrainzMetadataProvider.setThrottleLimit(1500);
//        underTest.initialize(); // FIXME

        underTest = new DefaultMusicBrainzProbe(cddbMetadataProvider, musicBrainzMetadataProvider);

        countMap.clear();
        foundMap.clear();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @AfterClass
    public void printStats()
      {
        log.info("STATS: COUNT: {}", countMap);
        log.info("STATS: FOUND: {}", foundMap);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "trackResourcesProvider2") // FIXME: run in parallel?
    public void must_correctly_retrieve_MusicBrainz_data (final @Nonnull TestSetTriple triple)
      throws Exception
      {
        // given
        final Path relativePath = triple.getRelativePath();
        final String testSetName = triple.getTestSetName();
        final Path actualResult = TEST_RESULTS.resolve("musicbrainz").resolve(testSetName).resolve(relativePath);
        final Path expectedResult = EXPECTED_RESULTS.resolve("musicbrainz").resolve(testSetName).resolve(relativePath);
        cddbMetadataProvider.setCachePath(CDDB_CACHE.resolve(testSetName));
        musicBrainzMetadataProvider.setCachePath(MUSICBRAINZ_CACHE.resolve(testSetName));

        final Metadata metadata = mockMetadataFrom(triple.getFilePath());

        countMap.putIfAbsent(testSetName, new AtomicInteger(0));
        countMap.get(testSetName).incrementAndGet();
        // when
        final List<ReleaseAndMedium> rams = underTest.probe(metadata);
        // then
        if (!rams.isEmpty())
          {
            foundMap.putIfAbsent(testSetName, new AtomicInteger(0));
            foundMap.get(testSetName).incrementAndGet();
          }

        dump(rams, actualResult);
        assertSameContents(expectedResult, actualResult);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void dump (final @Nonnull List<ReleaseAndMedium> rams, final  @Nonnull Path actualResult)
      throws IOException
      {
        final StringWriter sw = new StringWriter();

        try (final PrintWriter pw = new PrintWriter(sw))
          {
            for (final ReleaseAndMedium ram : rams)
              {
                log.info("FOUND RELEASE: {}", ram.getRelease().getId());
                log.info(">>>> TRACKS");
                pw.println("RELEASE: " + ram.getRelease().getId());
                pw.println("TRACKS");

                for (final DefTrackData track : ram.getMedium().getTrackList().getDefTrack())
                  {
                    final int position = track.getPosition().intValue();
                    final Recording recording = track.getRecording();
                    final String title = recording.getTitle();
                    log.info(">>>>>>>> {}. {}", position, title);
                    pw.printf("%d. %s%n", position, title);
                  }
              }
          }

        log.info(">>>> writing to {}", actualResult);
        Files.createDirectories(actualResult.getParent());
        Files.write(actualResult, sw.toString().getBytes(UTF_8));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] trackResourcesProvider2()
      {
        return streamOfTestSetTriples(TestSetLocator.allTestSets(), name -> METADATA.resolve(name))
//                .filter(triple -> triple.getTestSetName().equals("iTunes-fg-20160504-1"))
//                .filter(triple -> triple.getTestSetName().equals("iTunes-fg-20161210-1"))
//                .filter(triple -> triple.getFilePath().toString().contains("Trio 99_00"))
                .filter(triple -> triple.getFilePath().getFileName().toString().startsWith("01"))
                .collect(toTestNGDataProvider());
      }
  }
