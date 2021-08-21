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
import javax.annotation.concurrent.Immutable;
import java.util.function.Function;
import java.nio.file.Files;
import java.nio.file.Path;
import com.healthmarketscience.jackcess.RuntimeIOException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Getter @ToString @EqualsAndHashCode @Slf4j @Immutable
public class ExtensionAndMimeType
  {
    private static final Function<Path, String> PROBE = _f(Files::probeContentType);

    @Nonnull
    private final String extension;

    @Nonnull
    private final String mimeType;

    @With @Nonnull
    private final Function<Path, String> probe;

    @Nonnull
    public static ExtensionAndMimeType of (@Nonnull final String extension, @Nonnull final String mimeType)
      {
        return new ExtensionAndMimeType(extension, mimeType, PROBE);
      }

    @Nonnull
    public static ExtensionAndMimeType ofExtension (@Nonnull final String extension)
      {
        return new ExtensionAndMimeType(extension, "*/*", PROBE);
      }

    @Nonnull
    public static ExtensionAndMimeType ofMimeType (@Nonnull final String mimeType)
      {
        return new ExtensionAndMimeType("*", mimeType, PROBE);
      }

    public boolean matches (@Nonnull final Path path)
      {
        if (!extension.equals("*") && !path.toString().endsWith("." + extension))
          {
            return false;
          }

        try
          {
            if (!mimeType.equals("*/*") && !mimeType.equals(probe.apply(path)))
              {
                return false;
              }
          }
        catch (RuntimeIOException e)
          {
            log.warn("Can't guess MIME type of {}", path);
            return false;
          }

        return true;
      }
  }
