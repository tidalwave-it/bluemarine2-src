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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private static final Form NATIVE_FORM;

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


    @Nonnull
    public static Path normalizedPath (final @Nonnull String string)
      {
        try
          {
            return Paths.get(string);
          }
        catch (InvalidPathException e1)
          {
            log.trace(">>>> invalid path, now trying normalisation {}", e1.toString());

            for (final Form form : Form.values())
              {
                try
                  {
                    return Paths.get(normalized(string, form));
                  }
                catch (InvalidPathException e2)
                  {
                    log.trace(">>>> failed path normalisation with {}", form);
                  }
              }

            log.error("Invalid path, all normalisations failed: {}", string);
            return Paths.get("broken SEE BMT-46");
          }
      }

    // See http://askubuntu.com/questions/533690/rsync-with-special-character-files-not-working-between-mac-and-linux
    @CheckForNull
    public static String normalized (final @Nullable String string)
      {
        return normalized(string, NATIVE_FORM);
      }

    // See http://askubuntu.com/questions/533690/rsync-with-special-character-files-not-working-between-mac-and-linux
    @CheckForNull
    public static String normalized (final @Nullable String string, Form form)
      {
        return (string == null) ? null : Normalizer.normalize(string, form);
      }
  }
