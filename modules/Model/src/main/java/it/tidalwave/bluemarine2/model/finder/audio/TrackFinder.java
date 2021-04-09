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
import it.tidalwave.util.spi.ExtendedFinderSupport;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.audio.Track;
import it.tidalwave.bluemarine2.model.spi.SourceAwareFinder;

/***********************************************************************************************************************
 *
 * A {@code Finder} for {@link Track}s.
 * 
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface TrackFinder extends SourceAwareFinder<Track, TrackFinder>,
                                     ExtendedFinderSupport<Track, TrackFinder>
  {
    /*******************************************************************************************************************
     *
     * Constrains the search to tracks made by the given artist.
     *
     * @param       artistId    the artist id
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder madeBy (@Nonnull Id artistId);

    /*******************************************************************************************************************
     *
     * Constrains the search to tracks made by the given artist.
     *
     * @param       artist      the artist
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default TrackFinder madeBy (final @Nonnull MusicArtist artist)
      {
        return madeBy(artist.getId());
      }

    /*******************************************************************************************************************
     *
     * Constrains the search to tracks contained in the given record.
     *
     * @param       recordId    the record id
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder inRecord (@Nonnull Id recordId);

    /*******************************************************************************************************************
     *
     * Constrains the search to tracks contained in the given record.
     *
     * @param       record      the record
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default TrackFinder inRecord (final @Nonnull Record record)
      {
        return inRecord(record.getId());
      }
  }
