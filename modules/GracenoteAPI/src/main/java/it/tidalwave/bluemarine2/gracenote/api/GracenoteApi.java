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
package it.tidalwave.bluemarine2.gracenote.api;

import javax.annotation.Nonnull;
import java.io.IOException;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public interface GracenoteApi
  {
    /*******************************************************************************************************************
     *
     *
     * @param   offsets     the CD offsets
     * @return              an {@code Album}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Response<Album> findAlbumByToc (@Nonnull String offsets)
      throws IOException;

    /*******************************************************************************************************************
     *
     *
     * @param   gnId        the Gracenote Id
     * @return              an {@code Album}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Response<Album> findAlbumByGnId (@Nonnull String gnId)
      throws IOException;
  }
