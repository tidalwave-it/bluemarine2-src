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
package it.tidalwave.bluemarine2.model.audio;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.SourceAware;

/***********************************************************************************************************************
 *
 * Represents an audio track in a record. Maps the homonymous concept from the Music Ontology.
 *
 * NOTE: a Track is an abstract concept - it is associated to MediaItems (as AudioFiles), but it's not a MediaItem.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface Track extends Entity, SourceAware, Identifiable
  {
    public static final Class<Track> Track = Track.class;

    /*******************************************************************************************************************
     *
     * A {@link Record} property that it's handy to have here. See {@link Record#getDiskNumber()}.
     *
     * @see Record#getDiskNumber()
     *
     * @return  the disk number
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Integer> getDiskNumber();

    /*******************************************************************************************************************
     *
     * A {@link Record} property that it's handy to have here. See {@link Record#getDiskCount()}.
     *
     * @see Record#getDiskCount()
     *
     * @return  the disk count
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Integer> getDiskCount();

    /*******************************************************************************************************************
     *
     * The position of this track in the containing record
     *
     * @return  the track position
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Integer> getTrackNumber();

    /*******************************************************************************************************************
     *
     * The duration of this track
     *
     * @return  the duration
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Duration> getDuration();

    /*******************************************************************************************************************
     *
     * Returns the {@link Metadata}.
     *
     * @return  the metadata
     *
     ******************************************************************************************************************/
    @Nonnull
    public Metadata getMetadata();

    /*******************************************************************************************************************
     *
     * Returns the {@link Record} that contains this track
     *
     * @return  the record
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Record> getRecord();

    /*******************************************************************************************************************
     *
     * Returns the {@link Performance} that this track is a recording of.
     *
     * @return  the performance
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Performance> getPerformance();
  }
