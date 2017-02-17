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
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.spi.ExtendedFinder8Support;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;

/***********************************************************************************************************************
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface PathAwareFinder extends ExtendedFinder8Support<PathAwareEntity, PathAwareFinder>
  {
    /*******************************************************************************************************************
     *
     * Constrains the search to the entity with the given path.
     *
     * @param       path    the path
     * @return              the {@code Finder}
     *
     ******************************************************************************************************************/
    @Nonnull
    public PathAwareFinder withPath (@Nonnull Path path);

    /*******************************************************************************************************************
     *
     * Constrains the search to the entity with the given path.
     *
     * @param       path    the path
     * @return              the {@code Finder}
     *
     ******************************************************************************************************************/
    @Nonnull
    public default PathAwareFinder withPath (@Nonnull String path)
      {
        return withPath(Paths.get(path));
      }
  }
