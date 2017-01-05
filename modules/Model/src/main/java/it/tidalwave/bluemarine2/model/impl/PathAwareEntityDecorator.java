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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import lombok.Getter;
import static it.tidalwave.role.Identifiable.Identifiable;

/***********************************************************************************************************************
 *
 * An adapter for {@link Entity} to a {@link MediaFolder}. It can be used to adapt entities that naturally do
 * not belong to a hierarchy, such as an artist, to contexts where a hierarchy is needed (e.g. for browsing).
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PathAwareEntityDecorator<ENTITY extends Entity> extends EntityDecorator<ENTITY> implements PathAwareEntity
  {
    @Getter @Nonnull
    protected final Path path;

    @Getter @Nonnull
    protected final Optional<PathAwareEntity> parent;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public PathAwareEntityDecorator (final @Nonnull ENTITY delegate, final @Nonnull Path pathSegment)
      {
        this(pathSegment, delegate, Optional.empty());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public PathAwareEntityDecorator (final @Nonnull ENTITY delegate,
                                     final @Nonnull Path pathSegment,
                                     final @Nonnull PathAwareEntity parent,
                                     final @Nonnull String displayName,
                                     final @Nonnull Object ... roles)
      {
        this(parent.getPath().resolve(pathSegment),
             delegate,
             Optional.of(parent),
             computeRoles(parent, pathSegment, displayName, roles));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected PathAwareEntityDecorator (final @Nonnull Path path,
                                        final @Nonnull ENTITY delegate,
                                        final @Nonnull Optional<PathAwareEntity> parent,
                                        final @Nonnull Object... roles)
      {
        super(delegate, roles);
        this.path = path;
        this.parent = parent;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("%s(path=%s, delegate=%s, parent=%s)", getClass().getSimpleName(), path, delegate, parent);
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
    protected static PathAwareEntity wrappedEntity (final @Nonnull PathAwareEntity parent, final @Nonnull Entity entity)
      {
        if (entity instanceof PathAwareEntity) // FIXME: possibly avoid calling
          {
            return (PathAwareEntity)entity;
          }

        final Path path = parent.getPath().resolve(id(entity));
        final String displayName = entity.asOptional(Displayable.class).map(d -> d.getDisplayName()).orElse("???");

        if (entity.asOptional(SimpleComposite8.class).isPresent())
          {
            return new PathAwareMediaFolderDecorator2(entity, path, parent, displayName);
          }
        else
          {
            return new PathAwareEntityDecorator(entity, path, parent, displayName);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Object[] computeRoles (final @Nonnull PathAwareEntity parent,
                                            final @Nonnull Path pathSegment,
                                            final @Nonnull String displayName,
                                            final @Nonnull Object ... roles)
      {
        final List<Object> r = new ArrayList<>(Arrays.asList(roles));
        r.add(new DefaultDisplayable(displayName));
        return r.toArray();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static String id (final @Nonnull Entity entity)
      {
        return entity.as(Identifiable).getId().stringValue().replace('/', '_');
      }
  }
