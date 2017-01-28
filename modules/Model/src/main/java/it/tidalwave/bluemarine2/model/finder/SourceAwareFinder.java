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
import java.util.Optional;
import it.tidalwave.util.Id;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id $
 *
 **********************************************************************************************************************/
public interface SourceAwareFinder<ENTITY, FINDER> // extends ExtendedFinder8Support<ENTITY, BaseFinder<ENTITY, FINDER>>
  {
    @Nonnull
    public FINDER withId (final @Nonnull Id id);

    /*******************************************************************************************************************
     *
     * Specifies the data source of this finder.
     *
     * @param   source  the source
     * @return          the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER importedFrom (final @Nonnull Id source);

    /*******************************************************************************************************************
     *
     * Specifies the data source of this finder.
     *
     * @param   optionalSource  the source
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER importedFrom (final @Nonnull Optional<Id> optionalSource);

    /*******************************************************************************************************************
     *
     * Specifies the fallback data source of this finder.
     *
     * @param   fallback        the fallback source
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER withFallback (final @Nonnull Id fallback);

    /*******************************************************************************************************************
     *
     * Specifies the fallback data source of this finder.
     *
     * @param   optionalFallback  the fallback source
     * @return                    the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER withFallback (final @Nonnull Optional<Id> optionalFallback);
  }
