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
package it.tidalwave.bluemarine2.model.audio;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Optional;
import java.io.IOException;
import org.springframework.core.io.Resource;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.finder.audio.MusicArtistFinder;

/***********************************************************************************************************************
 *
 * Represents an audio file. Maps the homonymous concept from the Music Ontology.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface AudioFile extends MediaItem, Identifiable // FIXME: MediaItem should not be statically Parentable
  {
    /*******************************************************************************************************************
     *
     * Returns the makers of this audio file.
     *
     * FIXME: shouldn't be here - should be in getTrack().findMakers().
     *
     * @return  the makers
     *
     ******************************************************************************************************************/
    @Nonnull
    public MusicArtistFinder findMakers();

    /*******************************************************************************************************************
     *
     * Returns the composers of the musical expression related to this audio file.
     *
     * FIXME: shouldn't be here - should be in getSignal().findMakers() or such
     *
     * @return  the composer
     *
     ******************************************************************************************************************/
    @Nonnull
    public MusicArtistFinder findComposers();

    /*******************************************************************************************************************
     *
     * Returns the record related to this audio file.
     *
     * FIXME: shouldn't be here - should be in getTrack().getRecord() or such
     *
     * @return  the composer
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Record> getRecord();

    /*******************************************************************************************************************
     *
     * Returns the size of this file.
     *
     * @return                  the size
     * @throws  IOException     if there was an I/O problem
     *
     ******************************************************************************************************************/
    @Nonnegative
    public long getSize()
      throws IOException;

    /*******************************************************************************************************************
     *
     * Returns the a {@link Resource} representing this file's contents, if available.
     *
     * FIXME: not good to use Spring Resource...
     *
     * @return                  the contents
     * @throws  IOException     if there was an I/O problem
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Resource> getContent()
      throws IOException;
  }
