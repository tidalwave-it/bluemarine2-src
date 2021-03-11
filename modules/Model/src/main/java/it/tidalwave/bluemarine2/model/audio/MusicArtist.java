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
import it.tidalwave.util.Id;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.audio.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.spi.Entity;

/***********************************************************************************************************************
 *
 * Represents a music artist. Maps the homonymous concept from the Music Ontology.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface MusicArtist extends Entity, Identifiable
  {
    public static final Class<MusicArtist> MusicArtist = MusicArtist.class;

    /*******************************************************************************************************************
     *
     * Finds the tracks made by this artist.
     *
     * @return  a {@code Finder} of the tracks
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder findTracks();

    /*******************************************************************************************************************
     *
     * Finds the records made by this artist.
     *
     * @return  a {@code Finder} of the records
     *
     ******************************************************************************************************************/
    @Nonnull
    public RecordFinder findRecords();

    /*******************************************************************************************************************
     *
     * Finds the performances made by this artist.
     *
     * @return  a {@code Finder} of the performances
     *
     ******************************************************************************************************************/
    @Nonnull
    public PerformanceFinder findPerformances();

    public int getType(); // FIXME: use an enum

    /*******************************************************************************************************************
     *
     * Returns the data source of this datum (typically {@code embedded}, {@code musicbrainz} or such).
     *
     * @return  the data source
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Id> getSource();
  }
