/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.model.audio;

import javax.annotation.Nonnull;
import java.util.Optional;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.spi.Entity;

/***********************************************************************************************************************
 *
 * The association of a {@link MusicArtist} to a role (typically in a {@link Performance}. Roles are stuff such as
 * "conductor", "violin player", "soprano", "engineer", and so on.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface MusicPerformer extends Entity, Identifiable
  {
    public static final Class<MusicPerformer> _MusicPerformer_ = MusicPerformer.class;

    /*******************************************************************************************************************
     *
     * Returns the {@link MusicArtist}.
     *
     * @return  the music artist
     *
     ******************************************************************************************************************/
    @Nonnull
    public MusicArtist getMusicArtist();

    /*******************************************************************************************************************
     *
     * Returns the role.
     *
     * @return  the role
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Entity> getRole();
  }
