/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;

/***********************************************************************************************************************
 *
 * The role for an {@link MusicArtist} that is capable to render details upon selection, in the context of
 * {@link DefaultAudioExplorerPresentationControl}.
 * 
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = MusicArtist.class, context = DefaultAudioExplorerPresentationControl.class)
public class MusicArtistDetailRendererSelectable extends DetailRendererSelectable<MusicArtist>
  {
    public MusicArtistDetailRendererSelectable (final @Nonnull MusicArtist artist) 
      {
        super(artist);
      }
    
    @Override
    protected void renderDetails() 
      {
        renderDetails("");
        renderCoverArt(Optional.empty());
      }
  }
