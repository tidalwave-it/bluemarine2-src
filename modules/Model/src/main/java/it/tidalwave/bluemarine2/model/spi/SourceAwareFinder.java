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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.Nonnull;
import java.util.Optional;
import it.tidalwave.util.Id;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface SourceAwareFinder<ENTITY, FINDER> // extends ExtendedFinderSupport<ENTITY, BaseFinder<ENTITY, FINDER>>
  {
    @Nonnull
    public FINDER withId (@Nonnull final Id id);

    /*******************************************************************************************************************
     *
     * Specifies the data source of this finder.
     *
     * @param   source  the source
     * @return          the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER importedFrom (@Nonnull final Id source);

    /*******************************************************************************************************************
     *
     * Specifies the data source of this finder.
     *
     * @param   optionalSource  the source
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER importedFrom (@Nonnull final Optional<Id> optionalSource);

    /*******************************************************************************************************************
     *
     * Specifies the fallback data source of this finder.
     *
     * @param   fallback        the fallback source
     * @return                  the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER withFallback (@Nonnull final Id fallback);

    /*******************************************************************************************************************
     *
     * Specifies the fallback data source of this finder.
     *
     * @param   optionalFallback  the fallback source
     * @return                    the {@code Finder}, in fluent fashion
     *
     ******************************************************************************************************************/
    @Nonnull
    public FINDER withFallback (@Nonnull final Optional<Id> optionalFallback);
  }
