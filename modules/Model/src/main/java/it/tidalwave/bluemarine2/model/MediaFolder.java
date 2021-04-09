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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;
import it.tidalwave.util.Finder;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;
import it.tidalwave.bluemarine2.model.impl.PathAwareEntityFinderDelegate;

/***********************************************************************************************************************
 *
 * Represents a folder on a filesystem that contains media items. It is associated with the {@link SimpleComposite}
 * role. The filesystem can be a physical one (on the disk), or a virtual one (e.g. on a database); the folder concept
 * is flexible and represents any composite collection of items.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface MediaFolder extends PathAwareEntity, SimpleComposite<PathAwareEntity>
  {
    /*******************************************************************************************************************
     *
     * Returns a {@link PathAwareFinder} for retrieving children.
     *
     * @return  the {@code PathAwareFinder}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PathAwareFinder findChildren();

    /*******************************************************************************************************************
     *
     * Decorates an existing {@link Finder}{@code <PathAwareEntity>} with a {@link PathAwareFinder}.
     *
     * @param   delegate    the {@code Finder} to decorate
     * @return              the {@code PathAwareFinder}
     *
     ******************************************************************************************************************/
    @Nonnull
    public default PathAwareFinder finderOf (final @Nonnull Finder<PathAwareEntity> delegate)
      {
        return new PathAwareEntityFinderDelegate(this, delegate);
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link PathAwareFinder} that operates on a collection of {@link PathAwareEntity} items.
     *
     * @param   function    the provider of items
     * @return              the {@code PathAwareFinder}
     *
     ******************************************************************************************************************/
    @Nonnull
    public default PathAwareFinder finderOf (final @Nonnull Function<MediaFolder, Collection<? extends PathAwareEntity>> function)
      {
        return new PathAwareEntityFinderDelegate(this, function);
      }
  }
