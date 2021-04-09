/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.nio.file.Path;

/***********************************************************************************************************************
 *
 * A specialisation of {@link Entity} that has, or can have, a parent - hence, a {@link Path}.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface PathAwareEntity extends Entity
  {
    public static final Class<PathAwareEntity> _PathAwareEntity_ = PathAwareEntity.class;

    /*******************************************************************************************************************
     *
     * Returns the optional parent of this object.
     *
     * @return  the parent
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<PathAwareEntity> getParent();

    /*******************************************************************************************************************
     *
     * Returns the {@link Path} associated with this entity.
     *
     * @see #getRelativePath()
     *
     * @return  the path
     *
     ******************************************************************************************************************/
    @Nonnull
    public Path getPath();

    /*******************************************************************************************************************
     *
     * Returns the relative path of this entity. For instances without a parent, this method returns the same value as
     * {@link #getPath()}.
     *
     * @see #getPath()
     *
     * @return      the relative path
     *
     ******************************************************************************************************************/
    @Nonnull
    public default Path getRelativePath()
      {
        return getParent().map(parent -> parent.getPath().relativize(getPath())).orElse(getPath());
      }
  }
