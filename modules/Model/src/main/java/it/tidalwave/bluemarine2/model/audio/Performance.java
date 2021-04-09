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

import javax.annotation.Nonnull;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.finder.audio.MusicPerformerFinder;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.SourceAware;

/***********************************************************************************************************************
 *
 * Represents a performance. Maps the homonymous concept from the Music Ontology.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface Performance extends Entity, SourceAware, Identifiable
  {
    public static final Class<Performance> _Performance_ = Performance.class;

    /*******************************************************************************************************************
     *
     * Returns the performers of this performance.
     *
     * @return  a {@code Finder} for the performers
     *
     ******************************************************************************************************************/
    @Nonnull
    public MusicPerformerFinder findPerformers();
  }
