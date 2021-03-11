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
package it.tidalwave.bluemarine2.model.finder.audio;

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.ExtendedFinder8Support;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.audio.Track;
import it.tidalwave.bluemarine2.model.spi.SourceAwareFinder;

/***********************************************************************************************************************
 *
 * A {@code Finder} for {@link Record}s.
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface RecordFinder extends SourceAwareFinder<Record, RecordFinder>,
                                      ExtendedFinder8Support<Record, RecordFinder>
  {
    /*******************************************************************************************************************
     *
     * Constrains the search to records made by the given artist.
     *
     * @param       artistId    the artist id
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public RecordFinder madeBy (@Nonnull Id artistId);

    /*******************************************************************************************************************
     *
     * Constrains the search to records made by the given artist.
     *
     * @param       artist      the artist
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default RecordFinder madeBy (final @Nonnull MusicArtist artist)
      {
        return madeBy(artist.getId());
      }

    /*******************************************************************************************************************
     *
     * Constrains the search to records containing the given track.
     *
     * @param       trackId     the id of the track
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public RecordFinder containingTrack (@Nonnull Id trackId);

    /*******************************************************************************************************************
     *
     * Constrains the search to records containing the given track.
     *
     * @param       track       the track
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default RecordFinder containingTrack (final @Nonnull Track track)
      {
        return RecordFinder.this.containingTrack(track.getId());
      }
  }