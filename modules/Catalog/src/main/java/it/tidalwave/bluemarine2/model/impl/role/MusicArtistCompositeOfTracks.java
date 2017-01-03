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
package it.tidalwave.bluemarine2.model.impl.role;

import javax.annotation.Nonnull;
import it.tidalwave.role.Composite;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.impl.catalog.browser.RepositoryBrowserByArtistThenTrack;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * A role that makes a {@link MusicArtist} act as a {@link Composite} of {@link Track}s. It is only injected in the
 * {@link RepositoryBrowserByArtistThenTrack} context.
 *
 * @stereotype  Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = MusicArtist.class, context = RepositoryBrowserByArtistThenTrack.class)
@RequiredArgsConstructor
public class MusicArtistCompositeOfTracks implements SimpleComposite8<Track>
  {
    @Nonnull
    private final MusicArtist artist;

    @Override @Nonnull
    public TrackFinder findChildren()
      {
        return artist.findTracks();
      }
  }
