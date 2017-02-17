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
import java.net.URL;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;

/***********************************************************************************************************************
 *
 * Represents a record made of audio tracks. Maps the homonymous concept from the Music Ontology.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface Record extends Entity, SourceAware, Identifiable
  {
    public static final Class<Record> Record = Record.class;

    /*******************************************************************************************************************
     *
     * If this record is part of a multiple record release, return its disk number.
     *
     * @return  the disk number
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Integer> getDiskNumber();

    /*******************************************************************************************************************
     *
     * If this record is part of a multiple record release, return the count of disks in the release.
     *
     * @return  the disk count
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Integer> getDiskCount();

    /*******************************************************************************************************************
     *
     * Returns the number of tracks in this record, if available. Note that this value is the number of tracks
     * contained in the release, and might differ from {@code findTracks().count()} if only a subset of tracks is
     * available in the catalog (for instance, if not all of them have been bought/imported).
     *
     * @see #findTracks()
     *
     * @return  the track count
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Integer> getTrackCount();

    /*******************************************************************************************************************
     *
     * Finds the {@link Track}s in this record.
     *
     * @see #getTrackCount()
     *
     * @return  a {@code Finder} for the tracks
     *
     ******************************************************************************************************************/
    @Nonnull
    public TrackFinder findTracks();

    /*******************************************************************************************************************
     *
     * Returns the Amazon ASIN of this record.
     *
     * @return  the Amazon ASIN
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<String> getAsin();

    /*******************************************************************************************************************
     *
     * Returns the bar code of this record.
     *
     * @return  the bar code
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<String> getGtin();

    /*******************************************************************************************************************
     *
     * Returns the cover image URL of this record.
     *
     * @return  the cover image URL
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<URL> getImageUrl();
  }
