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
package it.tidalwave.bluemarine2.model.finder;

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.ExtendedFinder8Support;
import it.tidalwave.bluemarine2.model.MusicPerformer;
import it.tidalwave.bluemarine2.model.Performance;

/***********************************************************************************************************************
 *
 * A {@code Finder} for {@link MusicPerformer}s.
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface MusicPerformerFinder extends SourceAwareFinder<MusicPerformer, MusicPerformerFinder>,
                                              ExtendedFinder8Support<MusicPerformer, MusicPerformerFinder>
  {
    /*******************************************************************************************************************
     *
     * Constrains the search to artists who are performers of the given entity.
     *
     * @param       performanceId   the id of the performance
     * @return                      the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public MusicPerformerFinder performerOf (@Nonnull Id performanceId);

    /*******************************************************************************************************************
     *
     * Constrains the search to artists who are performers of the given entity.
     *
     * @param       performance     the  performance
     * @return                      the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public default MusicPerformerFinder performerOf (final @Nonnull Performance performance)
      {
        return performerOf(performance.getId());
      }
  }
