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
package it.tidalwave.bluemarine2.metadata.cddb.impl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.spi.MetadataSupport;
import org.testng.annotations.DataProvider;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class TestSupport
  {
    protected static final Path METADATA = Paths.get("target/metadata");

    protected static final Path CACHE = Paths.get("target/cache");

    protected static final Path CDDB_CACHE = CACHE.resolve("cddb");

    protected static final Path MUSICBRAINZ_CACHE = CACHE.resolve("musicbrainz");

    protected static final Path TEST_RESULTS = Paths.get("target/test-results");

    protected static final Path EXPECTED_RESULTS = Paths.get("target/expected-results");

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Metadata mockMetadataFrom (@Nonnull final Path file)
      throws IOException
      {
        final List<String> lines = Files.readAllLines(file, UTF_8);

        final Optional<String> albumTitle =
                lines.stream().filter(s -> s.contains("[mp3.album]"))
                              .findFirst()
                              .map(s -> s.replaceAll("^.* = ", ""));
        final Optional<Integer> trackNumber =
                lines.stream().filter(s -> s.contains("[mp3.trackNumber]"))
                              .findFirst()
                              .map(s -> s.replaceAll("^.* = ", ""))
                              .map(Integer::parseInt);
        final Optional<Integer> diskNumber =
                lines.stream().filter(s -> s.contains("[mp3.diskNumber]"))
                              .findFirst()
                              .map(s -> s.replaceAll("^.* = ", ""))
                              .map(Integer::parseInt);
        final Optional<Integer> diskCount =
                lines.stream().filter(s -> s.contains("[mp3.diskCount]"))
                              .findFirst()
                              .map(s -> s.replaceAll("^.* = ", ""))
                              .map(Integer::parseInt);
        final Optional<ITunesComment> iTunesComment =
                lines.stream().filter(s -> s.contains("[iTunes.comment]"))
                              .findFirst()
                              .map(s -> ITunesComment.fromToString(s.replaceAll("^.* = ", "")));

        return new MetadataSupport(file).with(ALBUM, albumTitle)
                                        .with(TRACK_NUMBER, trackNumber)
                                        .with(DISK_NUMBER, diskNumber)
                                        .with(DISK_COUNT, diskCount)
                                        .with(ITUNES_COMMENT, iTunesComment);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] trackResourcesProvider()
      {
        return streamOfTestSetTriples(TestSetLocator.allTestSets(), METADATA::resolve)
                // Files there apparendly don't have CDDB offsets
                .filter(triple -> !triple.getTestSetName().equals("iTunes-aac-fg-20170131-1"))
                .filter(triple -> !triple.getTestSetName().equals("amazon-autorip-fg-20170131-1"))
                
                .filter(triple -> triple.getFilePath().getFileName().toString().startsWith("01")) // FIXME: should test all tracks
                                                    .collect(toTestNGDataProvider());
      }
  }
