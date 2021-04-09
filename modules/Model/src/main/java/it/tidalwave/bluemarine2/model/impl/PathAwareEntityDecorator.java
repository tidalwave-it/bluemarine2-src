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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import lombok.Getter;
import static it.tidalwave.role.Identifiable._Identifiable_;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;

/***********************************************************************************************************************
 *
 * An adapter for {@link Entity} to a {@link MediaFolder}. It can be used to adapt entities that naturally do
 * not belong to a hierarchy, such as an artist, to contexts where a hierarchy is needed (e.g. for browsing).
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class PathAwareEntityDecorator extends EntityDecorator implements PathAwareEntity
  {
    @Nonnull
    protected final PathAwareEntity parent;

    @Getter @Nonnull
    protected final Path pathSegment;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected PathAwareEntityDecorator (@Nonnull final Entity delegate,
                                        @Nonnull final PathAwareEntity parent,
                                        @Nonnull final Path pathSegment,
                                        @Nonnull final Collection<Object> roles)
      {
        super(delegate, roles);
        this.pathSegment = pathSegment;
        this.parent = parent;
      }

    protected PathAwareEntityDecorator (@Nonnull final Entity delegate,
                                        @Nonnull final PathAwareEntity parent,
                                        @Nonnull final Path pathSegment)
      {
        this(delegate, parent, pathSegment, Collections.emptyList());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.of(parent);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Path getPath()
      {
        return parent.getPath().resolve(pathSegment);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("%s(path=%s, delegate=%s, parent=%s)", getClass().getSimpleName(), getPath(), delegate, parent);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toDumpString()
      {
        return String.format("Entity(path=%s, delegate=%s, parent=Folder(path=%s))", getPath(), delegate, parent.getPath());
      }

    /*******************************************************************************************************************
     *
     * Creates a wrapped entity, with the given parent.
     *
     * @param       parent      the parent
     * @param       entity      the source entity
     * @return                  the wrapped entity
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static PathAwareEntity wrappedEntity (@Nonnull final PathAwareEntity parent, @Nonnull final Entity entity)
      {
        if (entity instanceof PathAwareEntity) // FIXME: possibly avoid calling
          {
            return (PathAwareEntity)entity;
          }

        final Path pathSegment = idToPathSegment(entity);
        return entity.maybeAs(_SimpleComposite_).isPresent()
                ? new PathAwareMediaFolderDecorator(entity, parent, pathSegment)
                : new PathAwareEntityDecorator(entity, parent, pathSegment);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Path idToPathSegment (@Nonnull final Entity entity)
      {
        return Paths.get(entity.as(_Identifiable_).getId().stringValue().replace('/', '_'));
      }
  }
