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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * Holder of miscellaneous utility methods.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class UpnpUtilities
  {
    public static final String ID_NONE = "-1";

    private static final String ID_ROOT = "0";

    private static final String PATH_ROOT = "/";

    private static final String REGEXP_ROOT = "^/$";

    /*******************************************************************************************************************
     *
     * Converts to a {@link Path} to a a DIDL id.
     *
     * @param       path    the path
     * @return              the DIDL id
     *
     ******************************************************************************************************************/
    @Nonnull
    public static String pathToDidlId (final @Nonnull Path path)
      {
        return path.toString().replaceAll(REGEXP_ROOT, ID_ROOT);
      }

    /*******************************************************************************************************************
     *
     * Converts a DIDL id to a {@link Path}.
     *
     * @param       id      the DIDL id
     * @return              the path
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Path didlIdToPath (final @Nonnull String id)
      {
        return Paths.get(id.equals(ID_ROOT) ? PATH_ROOT : id);
      }

    /*******************************************************************************************************************
     *
     * Fixes the {@code maxCount} parameter of ContentDirectory {@code browse()}.
     *
     * @param   value   the input value
     * @return          the max count
     *
     ******************************************************************************************************************/
    @Nonnegative
    public static int maxCount (final @Nonnegative long value)
      {
        return (value == 0) ? Integer.MAX_VALUE : (int)value;
      }
  }
