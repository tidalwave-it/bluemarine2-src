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
package it.tidalwave.bluemarine2.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Normalizer;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.annotation.VisibleForTesting;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import static java.text.Normalizer.Form.*;

/***********************************************************************************************************************
 *
 * See the related test for detailed information.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@UtilityClass @Slf4j
public class PathNormalization
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

        log.info("Charset normalizer form: {}", NATIVE_FORM);
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
    public static Path normalizedPath (@Nonnull final Path path)
      throws IOException
      {
//        log.trace("normalizedPath({})", path);

        if (Files.exists(path))
          {
            return path;
          }

        Path pathSoFar = Paths.get("/");

        for (final Path segment : path)
          {
//            log.trace(">>>> pathSoFar: {} segment: {}", pathSoFar, segment);
            final Path resolved = pathSoFar.resolve(segment);

            if (Files.exists(resolved))
              {
                pathSoFar = resolved;
              }
            else // didn't find 'resolved' because of wrong normalisation, searching in alternative way
              {
                try (final Stream<Path> stream = Files.list(pathSoFar))
                  {
                    final Optional<Path> child = stream.map(Path::getFileName)
                                                       .filter(p -> equalsNormalized(segment, p))
                                                       .findFirst();
                    if (child.isEmpty())
                      {
                        log.warn(">>>> normalization failed - did you pass an absolute path? At {}", pathSoFar);
                        return path;
                      }

                    pathSoFar = pathSoFar.resolve(child.get());
                    assert Files.exists(pathSoFar) : "Normalization failed at: " + pathSoFar;
                  }
              }
          }

        return pathSoFar;
      }

    /*******************************************************************************************************************
     *
     * Checks whether two Paths are equal after normalisation of their string representation.
     *
     * @param   path1   the former path
     * @param   path2   the latter path
     * @return          {@code true} if they are equal
     *
     ******************************************************************************************************************/
    @VisibleForTesting static boolean equalsNormalized (@Nonnull final Path path1, @Nonnull final Path path2)
      {
        return Objects.equals(normalizedToNativeForm(path1.toString()), normalizedToNativeForm(path2.toString()));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nullable
    public static String normalizedToNativeForm (@Nullable final String string)
      {
        return (string == null) ? null : Normalizer.normalize(string, NATIVE_FORM);
      }
  }
