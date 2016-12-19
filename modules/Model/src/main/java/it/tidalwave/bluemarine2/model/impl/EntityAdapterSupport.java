/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.Finder8;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.role.EntityWithPath;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.spi.EntityWithPathAdapter;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import it.tidalwave.bluemarine2.model.spi.FactoryBasedEntityFinder;
import it.tidalwave.bluemarine2.model.spi.MediaFolderAdapter;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder.EntityCollectionFactory;
import lombok.Getter;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.role.Identifiable.Identifiable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class EntityAdapterSupport<ENTITY extends Entity> extends EntityWithRoles
  {
    @Getter @Nonnull
    protected final Path path;

    @Getter @Nonnull
    protected final ENTITY adaptee;

    @Getter @Nonnull
    protected final Optional<EntityWithPath> parent;

    protected EntityAdapterSupport (final @Nonnull Path path,
                                    final @Nonnull ENTITY adaptee,
                                    final @Nonnull Optional<EntityWithPath> parent,
                                    final @Nonnull Object... roles)
      {
        super(roles);
        this.path = path;
        this.adaptee = adaptee;
        this.parent = parent;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type)
      {
        return as(type, As.Defaults.throwAsException(type));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Collection<T> asMany (Class<T> type)
      {
        throw new UnsupportedOperationException(); // TODO
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Optional<T> asOptional (final @Nonnull Class<T> type)
      {
        return Optional.ofNullable(as(type, throwable -> null));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("%s(path=%s, delegate=%s, parent=%s)", getClass().getSimpleName(), path, adaptee, parent);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type, final @Nonnull NotFoundBehaviour<T> notFoundBehaviour)
      {
        try
          {
            return super.as(type);
          }
        catch (AsException e1)
          {
            try
              {
                return adaptee.as(type);
              }
            catch (AsException e2)
              {
                return notFoundBehaviour.run(e2);
              }
          }
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
    protected static EntityFinder wrappedFinder (final @Nonnull MediaFolder parent,
                                                 final @Nonnull Finder8<? extends Entity> finder)
      {
        final EntityCollectionFactory factory = p -> finder.results().stream()
                                                                     .map(child -> wrappedEntity(p, child))
                                                                     .collect(toList());
        return new FactoryBasedEntityFinder(parent, factory);
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
    protected static EntityWithPath wrappedEntity (final @Nonnull EntityWithPath parent, final @Nonnull Entity entity)
      {
        if (entity instanceof EntityWithPath) // FIXME: possibly avoid calling
          {
            return (EntityWithPath)entity;
          }

        final Path path = parent.getPath().resolve(id(entity));
        final String displayName = entity.asOptional(Displayable.class).map(d -> d.getDisplayName()).orElse("???");

        if (entity.asOptional(SimpleComposite8.class).isPresent())
          {
            return new MediaFolderAdapter(entity, path, parent, displayName);
          }
        else
          {
            return new EntityWithPathAdapter(entity, path, parent, displayName);
          }
      }

    @Nonnull
    protected static Object[] computeRoles (final @Nonnull EntityWithPath parent,
                                            final @Nonnull Path pathSegment,
                                            final @Nonnull String displayName,
                                            final @Nonnull Object ... roles)
      {
        final List<Object> r = new ArrayList<>(Arrays.asList(roles));
        r.add(new DefaultDisplayable(displayName));
        return r.toArray();
      }

    @Nonnull
    protected static String id (final @Nonnull Entity entity)
      {
        return entity.as(Identifiable).getId().stringValue().replace('/', '_');
      }
  }
