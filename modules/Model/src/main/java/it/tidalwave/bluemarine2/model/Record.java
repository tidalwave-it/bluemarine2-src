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
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;

/***********************************************************************************************************************
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
     * Finds the {@link Track}s in this record.
     *
     * @return  the tracks
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
