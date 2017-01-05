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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.util.AsException;
import it.tidalwave.util.Finder8;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;

/***********************************************************************************************************************
 *
 * FIXME: why is this different than PathAwareMediaFolderDecorator2?
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PathAwareMediaFolderDecorator extends EntityDecorator<Entity> implements MediaFolder
  {
    @Nonnull
    private final PathAwareEntity parent;

    @Nonnull
    private final Path pathSegment;

    public PathAwareMediaFolderDecorator (final @Nonnull Entity delegate,
                                          final @Nonnull PathAwareEntity parent,
                                          final @Nonnull Path pathSegment)
      {
        super(delegate);
        this.parent = parent;
        this.pathSegment = pathSegment;
      }

    @Override @Nonnull
    public EntityFinder findChildren()
      {
        try
          {
            final SimpleComposite8<Entity> composite = delegate.as(SimpleComposite8.class);
            final Finder8<? extends Entity> finder = composite.findChildren();
            return PathAwareMediaFolderDecorator2.wrappedFinder(this, finder);
          }
        catch (AsException e)
          {
            final VirtualMediaFolder.EntityCollectionFactory EMPTY = p -> Collections.emptyList();
            final PathAwareEntityFinderDelegate EMPTY_FINDER = new PathAwareEntityFinderDelegate(this, EMPTY);
            return EMPTY_FINDER;
          }
      }

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.of(parent);
      }

    @Override @Nonnull
    public Path getPath()
      {
        return parent.getPath().resolve(pathSegment);
      }
  }
