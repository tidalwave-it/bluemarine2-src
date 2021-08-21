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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/***********************************************************************************************************************
 *
 * A temporary class to work with Tika, for performing tests. Should be merged to DefaultMediaScanner.
 * TODO: This class could be kept (without references to Tika) as a SimpleMediaScanner that would allow to write simpler
 * tests.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j @RequiredArgsConstructor
class Scanner
  {
    @Nonnull
    private final TikaMetadataLoader metadataLoader;

    /*******************************************************************************************************************
     *
     * Parameters for the scanner.
     *
     ******************************************************************************************************************/
    @Getter @ToString @RequiredArgsConstructor @Immutable
    public static class Params
      {
        protected Params()
          {
            this((__ -> true), Integer.MAX_VALUE, TikaMetadataLoader.Config.DEFAULT_ASSOCIATIONS);
          }

        /** A predicate to filter files to scan. */
        @Nonnull @With
        private final Predicate<Path> filter;

        /** The maximum number of files to scan. */
        @Nonnegative @With
        private final int limit;

        /** A map that associates TikaMetadataLoader.Config to a specific kind of file (described by file extension
         * and/or MIME type). Default associations can be overridden by calling
         * {@link #withMetadataLoaderConfig(ExtensionAndMimeType, TikaMetadataLoader.Config)}  */
        @Nonnull @With
        private final Map<ExtensionAndMimeType, TikaMetadataLoader.Config> loaderConfig;

        /***************************************************************************************************************
         *
         * Creates/overrides the metadata loader configuration for the given kind of file.
         *
         * @param extensionAndMimeType    the kind of file
         * @param config                  the loader configuration
         * @return                        the updated parameters
         *
         **************************************************************************************************************/
        @Nonnull
        public Params withMetadataLoaderConfig (@Nonnull final ExtensionAndMimeType extensionAndMimeType,
                                                @Nonnull final TikaMetadataLoader.Config config)
          {
            final Map<ExtensionAndMimeType, TikaMetadataLoader.Config> clone = new HashMap<>(this.loaderConfig);
            clone.put(extensionAndMimeType, config);
            return new Params(filter, limit, clone);
          }

        /***************************************************************************************************************
         *
         * Retrieves the metadata loader configuration for the given file.
         *
         * @param path    the file to analyze
         * @return        the metadata loader configuration
         *
         **************************************************************************************************************/
        @Nonnull
        public TikaMetadataLoader.Config loaderConfigFor (@Nonnull final Path path)
          {
            return loaderConfig.entrySet()
                               .stream()
                               .filter(e -> e.getKey().matches(path))
                               .findFirst()
                               .map(Map.Entry::getValue)
                               .orElse(TikaMetadataLoader.Config.DEFAULT);
          }

        /***************************************************************************************************************
         *
         * Returns a predicate that matches the given extension.
         *
         * @param extension   the extension
         * @return            the predicate
         *
         **************************************************************************************************************/
        @Nonnull
        public static Predicate<Path> extensionFilter (@Nonnull final String extension)
          {
            return p -> p.getFileName().toString().toLowerCase().endsWith("." + extension);
          }
      }

    /*******************************************************************************************************************
     *
     * Creates default parameters.
     *
     * @return    default parameters
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Params params()
      {
        return new Params();
      }

    /*******************************************************************************************************************
     *
     * Scans a folder and applies the given metadata processor.
     *
     * @param basePath  the path of the base folder to scan
     * @param params    the parameters of the scan
     * @param processor the metadata processor
     *
     ******************************************************************************************************************/
    public void scan (@Nonnull final Path basePath,
                      @Nonnull final Params params,
                      @Nonnull final Consumer<MetadataWithPath> processor)
            throws IOException
      {
        log.info("Scanning {}", basePath);

        try (final Stream<Path> dirStream = Files.walk(basePath, FOLLOW_LINKS))
          {
            dirStream.parallel().filter(params.getFilter()).limit(params.getLimit()).forEach(path ->
              {
                try
                  {
                    final TikaMetadataLoader.Config loaderConfig = params.loaderConfigFor(path);
                    final MetadataWithPath metadata = metadataLoader.loadMetadata(path, loaderConfig);
                    processor.accept(metadata);
                  }
                catch (TikaException | IOException | SAXException e)
                  {
                    log.error("While processing {}: {}", path, e.toString());
                  }
              });
          }
      }
  }
