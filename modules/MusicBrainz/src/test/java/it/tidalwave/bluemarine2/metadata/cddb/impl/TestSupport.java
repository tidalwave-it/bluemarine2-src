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
package it.tidalwave.bluemarine2.metadata.cddb.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testng.annotations.DataProvider;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.ITunesComment;
import it.tidalwave.bluemarine2.model.spi.MetadataSupport;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
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
    protected Metadata mockMetadataFrom (final @Nonnull Path file)
      throws IOException
      {
        final Optional<String> albumTitle =
                Files.lines(file, UTF_8).filter(s -> s.contains("[mp3.album]"))
                                        .findFirst()
                                        .map(s -> s.replaceAll("^.* = ", ""));
        final Optional<ITunesComment> iTunesComment =
                Files.lines(file, UTF_8).filter(s -> s.contains("[iTunes.comment]"))
                                        .findFirst()
                                        .map(s -> ITunesComment.fromToString(s.replaceAll("^.* = ", "")));

        return new MetadataSupport(file).with(TITLE, albumTitle)
                                        .with(ITUNES_COMMENT, iTunesComment);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] trackResourcesProvider()
      {
        return streamOfTestSetTriples(TestSetLocator.allTestSets(), name -> METADATA.resolve(name))
                .filter(triple -> triple.getFilePath().getFileName().toString().startsWith("01"))
                                                    .collect(toTestNGDataProvider());
      }
  }