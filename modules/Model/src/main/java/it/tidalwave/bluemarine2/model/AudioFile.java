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
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Path;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import javax.annotation.Nonnegative;

/***********************************************************************************************************************
 *
 * Represents an audio file. Maps the homonymous concept from the Music Ontology.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface AudioFile extends MediaItem, Identifiable // FIXME: MediaItem should not be statically Parentable
  {
    @Nonnull
    public Path getPath(); // FIXME: rename to getRelativePath?

    @Nonnegative
    public long getSize()
      throws IOException;

    @Nonnull
    public Optional<byte[]> getContent()
      throws IOException;

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
  }
