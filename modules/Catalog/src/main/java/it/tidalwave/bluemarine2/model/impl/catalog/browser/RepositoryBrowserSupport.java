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
package it.tidalwave.bluemarine2.model.impl.catalog.browser;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.function.Function;
import it.tidalwave.util.Finder;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import it.tidalwave.bluemarine2.model.role.EntityBrowser;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.util.Parameters.r;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class RepositoryBrowserSupport extends EntityWithRoles implements EntityBrowser
  {
    @Inject
    private MediaCatalog catalog;

    @Nonnull
    protected final SimpleComposite<? extends Entity> compositeForRootEntity;

    protected RepositoryBrowserSupport (final @Nonnull Function<MediaCatalog, Finder<? extends Entity>> finderFactory)
      {
        compositeForRootEntity = () -> finderFactory.apply(catalog).withContext(RepositoryBrowserSupport.this);
      }

    @Override @Nonnull
    public Entity getRoot()
      {
        // FIXME: review if first should be prioritised
        return new EntityWithRoles(r(compositeForRootEntity, this.as(_Displayable_))); // FIXME: what about an
        // EntityDecorator?
      }
  }
