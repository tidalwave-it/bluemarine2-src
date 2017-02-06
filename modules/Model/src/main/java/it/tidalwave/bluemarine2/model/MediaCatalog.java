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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import it.tidalwave.bluemarine2.model.finder.AudioFileFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface MediaCatalog
  {
    public static final Class<MediaCatalog> MediaCatalog = MediaCatalog.class;

    /*******************************************************************************************************************
     *
     * Finds the {@link MusicArtist}s in this catalog.
     *
     * @return  a {@link Finder} for the artists
     *
     ******************************************************************************************************************/
    @Nonnull
    public MusicArtistFinder findArtists();

    /*******************************************************************************************************************
     *
     * Finds the {@link Record}s in this catalog.
     *
     * @return  a {@link Finder} for the records
     *
     ******************************************************************************************************************/
    @Nonnull
    public RecordFinder findRecords();

    /*******************************************************************************************************************
     *
     * Finds the {@link Track}s in this catalog.
     *
     * @return  a {@link Finder} for the tracks
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder findTracks();

    /*******************************************************************************************************************
     *
     * Finds the {@link Performance}s in this catalog.
     *
     * @return  a {@link Finder} for the performances
     *
     ******************************************************************************************************************/
    @Nonnull
    public PerformanceFinder findPerformances();

    /*******************************************************************************************************************
     *
     * Finds the {@link AudioFile}s in this catalog.
     *
     * @return  a {@link Finder} for the audio files
     *
     ******************************************************************************************************************/
    @Nonnull
    public AudioFileFinder findAudioFiles();
  }
