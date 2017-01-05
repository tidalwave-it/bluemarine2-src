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
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.MappingFilter;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.impl.PathAwareEntityDecorator.wrappedEntity;

/***********************************************************************************************************************
 *
 * An adapter for {@link Entity} to {@link MediaFolder}. It can be used to adapt entities that naturally do
 * not belong to a hierarchy, such as an artist, to contexts where a hierarchy is needed (e.g. for browsing).
 *
 * @stereotype Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class PathAwareMediaFolderDecorator2 extends PathAwareEntityDecorator<MediaFolder> implements MediaFolder
  {
    public PathAwareMediaFolderDecorator2 (final @Nonnull Entity delegate,
                                 final @Nonnull Path pathSegment,
                                 final @Nonnull PathAwareEntity parent,
                                 final @Nonnull String displayName,
                                 final @Nonnull Object ... roles)
      {
        this((delegate instanceof MediaFolder) ? (MediaFolder)delegate
                                               : new PathAwareMediaFolderDecorator(delegate, parent, pathSegment),
             pathSegment, parent, displayName, roles);
      }

    public PathAwareMediaFolderDecorator2 (final @Nonnull MediaFolder delegate,
                                 final @Nonnull Path pathSegment,
                                 final @Nonnull PathAwareEntity parent,
                                 final @Nonnull String displayName,
                                 final @Nonnull Object ... roles)
      {
        super(parent.getPath().resolve(pathSegment),
              delegate, Optional.of(parent),
              computeRoles(parent, pathSegment, displayName, roles));
      }

    public PathAwareMediaFolderDecorator2 (final @Nonnull MediaFolder delegate, final @Nonnull Path pathSegment)
      {
        super(pathSegment, delegate, Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public EntityFinder findChildren()
      {
        return wrappedFinder(this, delegate.findChildren());
      }

    /*******************************************************************************************************************
     *
     * Creates a wrapped finder, that wraps all entities in its result.
     *
     * @param   parent          the parent
     * @param   finder          the source finder
     * @return                  the wrapped finder
     *
     ******************************************************************************************************************/
    @Nonnull
    public static EntityFinder wrappedFinder (final @Nonnull MediaFolder parent,
                                              final @Nonnull Finder8<? extends Entity> finder)
      {
        return new PathAwareEntityFinderDelegate(parent,
                                                 (Finder8)new MappingFilter<>((Finder8)finder,
                                                                              child -> wrappedEntity(parent, (Entity)child)));
      }
  }
