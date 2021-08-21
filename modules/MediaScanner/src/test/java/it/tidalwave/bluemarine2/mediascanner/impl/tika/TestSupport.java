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
package it.tidalwave.bluemarine2.mediascanner.impl.tika;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.tika.metadata.Metadata;
import org.testng.annotations.DataProvider;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.bluemarine2.mediascanner.impl.tika.TikaMetadataLoader.Config;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class TestSupport
  {
    protected static final Path TEST_RESOURCES = Path.of("src/test/resources");
    protected static final Path ACTUAL_RESULT_FOLDER = Path.of("target/test-results");
    protected static final Path EXPECTED_RESULT_FOLDER = TEST_RESOURCES.resolve("expected-results");
    protected static final List<String> ITEMS_TO_IGNORE = List.of("File Modified Date", "File Name");

    /*******************************************************************************************************************
     *
     * Dumps metadata to a file.
     *
     * @param metadata      the metadata to dump
     * @param path          the path to write to
     * @param itemsToIgnore a collections of items to ignore
     * @throws IOException
     *
     ******************************************************************************************************************/
    protected static void dumpMetadata (@Nonnull final Metadata metadata,
                                        @Nonnull final Path path,
                                        @Nonnull final Collection<String> itemsToIgnore)
            throws IOException
      {
        final List<String> lines = stream(metadata.names())
                .filter(n -> !itemsToIgnore.contains(n))
                .sorted()
                .map(n -> String.format("%s=%s", n, metadata.get(n)))
                .collect(toList());
        Files.write(path, lines, UTF_8);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] testFolderData()
      {
        final String jpegs = System.getenv("STOPPING_DOWN");
        final String xmps = System.getenv("PHOTOS");
        assert jpegs != null;
        assert xmps != null;

        return new Object[][]
          {
            // { Path.of(jpegs).resolve("ExternalMedia/stillimages/100") },
            { Path.of(xmps).resolve("Digital") },
            { Path.of(xmps).resolve("Incoming") }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    protected static Object[][] testFileData()
      {
        return new Object[][]
          {
            { Config.DEFAULT, TEST_RESOURCES.resolve("images/20210813-0091.jpg") },     // [1]
            { Config.XMP_SIDECAR, TEST_RESOURCES.resolve("images/20210813-0091.xmp") }, // [2]
            { Config.XMP_SIDECAR, TEST_RESOURCES.resolve("images/20180520-0261.xmp") }  // [2]
            // [1] Exported from Capture One 21
            // [2] Generated by Photo Supreme
          };
      }
  }
