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
package it.tidalwave.bluemarine2.model.finder.audio;

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.ExtendedFinderSupport;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.Performance;
import it.tidalwave.bluemarine2.model.audio.Track;
import it.tidalwave.bluemarine2.model.spi.SourceAwareFinder;

/***********************************************************************************************************************
 *
 * A {@code Finder} for {@link Performance}s.
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface PerformanceFinder extends SourceAwareFinder<Performance, PerformanceFinder>,
                                           ExtendedFinderSupport<Performance, PerformanceFinder>
  {
    /*******************************************************************************************************************
     *
     * Constrains the search to performances of the given track.
     *
     * @param       trackId     the id of the track
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public PerformanceFinder ofTrack (@Nonnull Id trackId);

    /*******************************************************************************************************************
     *
     * Constrains the search to performances of the given track.
     *
     * @param       track       the track
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default PerformanceFinder ofTrack (@Nonnull final Track track)
      {
        return ofTrack(track.getId());
      }

    /*******************************************************************************************************************
     *
     * Constrains the search to performances of the given performer.
     *
     * @param       performerId the id of the performer
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public PerformanceFinder performedBy (@Nonnull Id performerId);

    /*******************************************************************************************************************
     *
     * Constrains the search to performances of the given performer.
     *
     * @param       performer   the id of the performer
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default PerformanceFinder performedBy (@Nonnull final MusicArtist performer)
      {
        return performedBy(performer.getId());
      }
  }
