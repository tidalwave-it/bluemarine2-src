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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import it.tidalwave.util.Finder;
import it.tidalwave.util.MappingFinder;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class PathAwareMediaFolderDecorator extends PathAwareEntityDecorator implements MediaFolder
  {
    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     * @param   delegate            the delegate
     * @param   parent              the parent
     * @param   pathSegment         the path segment of this object
     * @param   additionalRoles     some additional roles
     *
     ******************************************************************************************************************/
    public PathAwareMediaFolderDecorator (final @Nonnull Entity delegate,
                                          final @Nonnull PathAwareEntity parent,
                                          final @Nonnull Path pathSegment,
                                          final @Nonnull Object ... additionalRoles)
      {
        super(delegate, parent, pathSegment, additionalRoles);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PathAwareFinder findChildren()
      {
        final SimpleComposite<Entity> composite = delegate.as(SimpleComposite.class);
        return wrappedFinder(this, composite.findChildren());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toDumpString()
      {
        return String.format("Folder(path=%s, parent=Folder(path=%s))", getPath(), parent.getPath());
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
    private static PathAwareFinder wrappedFinder (final @Nonnull MediaFolder parent,
                                               final @Nonnull Finder<? extends Entity> finder)
      {
        if (finder instanceof PathAwareEntityFinderDelegate)
          {
            return (PathAwareFinder)finder;
          }

        return new PathAwareEntityFinderDelegate(parent,
                                                 (Finder)new MappingFinder<>((Finder)finder,
                                                                              child -> wrappedEntity(parent, (Entity)child)));
      }
  }
