/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.finder;

import javax.annotation.Nonnull;
import it.tidalwave.util.Finder8;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.Track;

/***********************************************************************************************************************
 *
 * @stereotype      Finder
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface TrackFinder extends Finder8<Track>
  {
    /*******************************************************************************************************************
     *
     * Constrains the search to records made by the given artist.
     * 
     * @param       artist      the artist
     * @return      the {@code Finder}
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder madeBy (@Nonnull MusicArtist artist);
    
    /*******************************************************************************************************************
     *
     * Constrains the search to tracks contained in the given record.
     * 
     * @param       record      the record
     * @return      the {@code Finder}
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder inRecord (@Nonnull Record record);
    
    // FIXME: push to Finder
    @Nonnull
    public TrackFinder withContext (@Nonnull Object context);
  }