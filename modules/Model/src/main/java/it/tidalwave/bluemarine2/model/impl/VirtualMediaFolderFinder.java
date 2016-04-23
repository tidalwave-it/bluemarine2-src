/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.nio.file.Path;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An {@link EntityFinder} for retrieving children of a {@link VirtualMediaFolder}.
 *
 * @stereotype  Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class VirtualMediaFolderFinder extends Finder8Support<Entity, EntityFinder> implements EntityFinder
  {
    private static final long serialVersionUID = 4429676480224742813L;

    @Nonnull
    private final VirtualMediaFolder mediaFolder;

    @Nonnull
    private final Optional<Path> path;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public VirtualMediaFolderFinder (final @Nonnull VirtualMediaFolder mediaFolder)
      {
        this.mediaFolder = mediaFolder;
        this.path = Optional.empty();
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public VirtualMediaFolderFinder (final @Nonnull VirtualMediaFolderFinder other, final @Nonnull Object override)
      {
        super(other, override);
        final VirtualMediaFolderFinder source = getSource(VirtualMediaFolderFinder.class, other, override);
        this.mediaFolder = source.mediaFolder;
        this.path = source.path;
      }

    /*******************************************************************************************************************
     *
     * Override constructor.
     *
     ******************************************************************************************************************/
    private VirtualMediaFolderFinder (final @Nonnull VirtualMediaFolder mediaFolder,
                                      final @Nonnull Optional<Path> path)
      {
        this.mediaFolder = mediaFolder;
        this.path = path;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public EntityFinder withPath (final @Nonnull Path path)
      {
        return clone(new VirtualMediaFolderFinder(mediaFolder, Optional.of(path)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends Entity> computeResults()
      {
        return path.isPresent() ? filteredByPath(path.get())
                                : new CopyOnWriteArrayList<>(mediaFolder.getChildrenSupplier().get());
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private List<? extends Entity> filteredByPath (final @Nonnull Path path)
      {
        // FIXME: please refactor this mess!
        if (mediaFolder.getPath().equals(path))
          {
            return Collections.singletonList(mediaFolder);
          }

        try
          {
            final Path relativePath = relative(path);

            final List<Entity> filtered = mediaFolder.getChildrenSupplier().get().stream()
                    .filter(entity -> sameHead(relative(pathOf(entity)), relativePath))
                    .collect(Collectors.toList());

            if (filtered.isEmpty())
              {
                return filtered;
              }
            else
              {
                final Entity e = filtered.get(0);

                if (path.equals(pathOf(e)))
                  {
                    return filtered;
                  }
                else
                  {
                    return ((MediaFolder)e).findChildren().withPath(path).results();
                  }
              }
          }
        catch (IllegalArgumentException e) // path can't be relativised
          {
            return Collections.emptyList();
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Path relative (final @Nonnull Path path)
      {
        return mediaFolder.isRoot() ? path :
                path.startsWith(mediaFolder.getPath()) ? path.subpath(mediaFolder.getPath().getNameCount(), path.getNameCount())
                                                       : null;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    private static Path pathOf (final @Nonnull Entity entity)
      {
        if (entity instanceof MediaFolder)
          {
            return ((MediaFolder)entity).getPath();
          }
        else if (entity instanceof MediaItem)
          {
            return ((MediaItem)entity).getPath();
          }
        else
          {
            return null;
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private static boolean sameHead (final @Nullable Path path1, final @Nullable Path path2)
      {
        return (path1 != null) && (path2 != null) &&  path1.getName(0).equals(path2.getName(0));
      }
  }
