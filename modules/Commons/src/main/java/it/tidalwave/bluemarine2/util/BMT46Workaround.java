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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE) @Slf4j
public final class BMT46Workaround
  {
    private final static String PREFIX = "fix-for-BMT46-";

    @Nonnull
    public static Path fixedPathBMT46 (final @Nonnull Path path)
      throws IOException
      {
        if (!isTroubled(path))
          {
            return path;
          }
        else
          {
            final String suffix = path.getFileName().toString().replaceAll("^.*\\.", "");
            final Path tempFile = Files.createTempFile(PREFIX, "." + suffix);
            Files.delete(tempFile); // it was a trick to get a temporary path
            Files.createSymbolicLink(tempFile, path);
            log.warn(">>>> workaround for BMT-46: file linked from {}", tempFile);
            return tempFile;
          }
      }

    public static void deleteBMT46 (final @Nullable Path path)
      {
        if (path != null)
          {
            final String fileName = path.getFileName().toString();

            if (fileName.startsWith(PREFIX) && Files.isSymbolicLink(path))
              {
                try
                  {
                    log.debug(">>>> deleting temp link for BMT-46 workaround {}", path);
                    Files.delete(path);
                  }
                catch (IOException e)
                  {
                    log.warn("Cannot delete {}", path);
                  }
              }
          }
      }

    private static boolean isTroubled (final @Nonnull Path path)
      {
        return Files.exists(path) != path.toFile().exists();
      }
  }
