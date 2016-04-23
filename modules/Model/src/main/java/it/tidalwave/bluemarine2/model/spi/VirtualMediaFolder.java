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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;
import java.nio.file.Path;
import it.tidalwave.util.Id;
import it.tidalwave.role.Identifiable;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.impl.EntityWithRoles;
import it.tidalwave.bluemarine2.model.impl.VirtualMediaFolderFinder;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * An implementation of {@link MediaFolder} that is not backed by a file system or a database, but can be created in
 * memory. Useful, for instance, for creating intermediate folders in a hierarchy.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class VirtualMediaFolder extends EntityWithRoles implements MediaFolder
  {
    @Getter @Nonnull
    private final Path path;

    @Getter @CheckForNull
    private MediaFolder parent;

    @Getter @Nonnull
    private final Supplier<Collection<Entity>> childrenSupplier;

    /*******************************************************************************************************************
     *
     * Creates an instance given a {@link Supplier} of children.
     *
     * @param   parent              an optional parent
     * @param   path                the path (must be a simple path element)
     * @param   displayName         the display name
     * @param   childrenSupplier    the {@code Supplier] of children
     *
     ******************************************************************************************************************/
    public VirtualMediaFolder (final @Nullable MediaFolder parent,
                               final @Nonnull Path path,
                               final @Nonnull String displayName,
                               final @Nonnull Supplier<Collection<Entity>> childrenSupplier)
      {
        super((Identifiable)() -> new Id(absolutePath(parent, path).toString()),
              new DefaultDisplayable(displayName));
        this.path = absolutePath(parent, path);
        this.parent = parent;
        this.childrenSupplier = childrenSupplier;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public boolean isRoot()
      {
        return parent == null;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public EntityFinder findChildren()
      {
        return new VirtualMediaFolderFinder(this);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("VirtualMediaFolder(%s)", path);
      }

    /*******************************************************************************************************************
     *
     * Computes the absolute path given an optional parent.
     *
     * @param   parent  the parent (optional)
     * @param   path    the path
     * @return          the absolute path
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Path absolutePath (final @Nullable MediaFolder parent, final @Nonnull Path path)
      {
        return (parent == null) ? path : parent.getPath().resolve(path);
      }
  }
