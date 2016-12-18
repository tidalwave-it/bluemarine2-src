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
package it.tidalwave.bluemarine2.util;

import java.io.File;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Normalizer;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static java.text.Normalizer.Form.*;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE) @Slf4j
public final class Miscellaneous
  {
    private static final Normalizer.Form NATIVE_FORM;

    static
      {
        final String osName = System.getProperty("os.name").toLowerCase();

        switch (osName)
          {
            case "linux":
                NATIVE_FORM = NFC;
                break;

            case "mac os x":
                NATIVE_FORM = NFD;
                break;

            case "windows":
                NATIVE_FORM = NFD; // FIXME: just guessing
                break;

            default:
                throw new ExceptionInInitializerError("Unknown o.s.: " + osName);
          }

        log.info(">>>> Charset normalizer form: {}", NATIVE_FORM);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public static String normalizedToNativeForm (final @Nullable String string)
      {
        return (string == null) ? null : Normalizer.normalize(string, NATIVE_FORM);
      }

    /*******************************************************************************************************************
     *
     * Takes a path, and in case it can't be resolved, it tries to replace with an equivalent representation of an
     * existing path, with the native form of character encoding (i.e. the one used by the file system).
     * If there is no normalized path to replace with, the original path is returned.
     * Note that this method is I/O heavy, as it must access the file system.
     * FIXME: what about using a cache?
     *
     * See http://askubuntu.com/questions/533690/rsync-with-special-character-files-not-working-between-mac-and-linux
     *
     * @param   path    the path
     * @return          the normalized path
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Path normalizedPath (final @Nonnull Path path)
      throws IOException
      {
        log.trace("normalizedPath({}", path);

        if (Files.exists(path))
          {
            return path;
          }

        Path pathSoFar = Paths.get("/");

        for (final Path element : path)
          {
            log.trace(">>>> pathSoFar: {} element: {}", pathSoFar, element);
            final Path resolved = pathSoFar.resolve(element);

            if (Files.exists(resolved))
              {
                pathSoFar = resolved;
              }
            else
              {
                // FIXME: refactor with lambdas
                try (final Stream<Path> stream = Files.list(pathSoFar))
                  {
                    boolean found = false;

                    for (final Path child : stream.collect(toList()))
                      {
                        final Path childName = child.getFileName();
                        found = normalizedToNativeForm(element.toString())
                                .equals(normalizedToNativeForm(childName.toString()));
                        log.trace(">>>> original: {} found: {} same: {}", element, childName, found);

                        if (found)
                          {
                            pathSoFar = pathSoFar.resolve(childName);
                            break;
                          }
                      }

                    if (!found)
                      {
                        return path; // fail
                      }
                  }
              }
          }

        return pathSoFar;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static File toFileBMT46 (final @Nonnull Path path)
      throws IOException
      {
        File file = path.toFile();

        if (probeBMT46(path))
          {
            file = File.createTempFile("temp", ".mp3");
            file.deleteOnExit();
            log.warn("Workaround for BMT-46: copying to temporary file: {}", file);
            Files.copy(path, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
          }

        return file;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private static boolean probeBMT46 (final @Nonnull Path path)
      {
        return Files.exists(path) && !path.toFile().exists();
      }
  }
