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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import it.tidalwave.util.As;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.nio.file.Path;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.role.EntityWithPath;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.role.SimpleComposite8;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A decorator of an {@link Finder} of {@link EntityWithPath} that creates a virtual tree of entities. Each entity is
 * given a path, which starts with the path of a {@link MediaFolder} and continues with the id of the entity.
 *
 * This {@code Finder} can filter by path. If a filter path is provided, the filtering happens in memory: this means
 * that if the delegate queries a repository, all the data are first retrieve in memory.
 *
 * FIXME: rename to PathAwareEntityFinder
 *
 * @stereotype  Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor(access = PRIVATE) @Slf4j
public class FactoryBasedEntityFinder extends Finder8Support<EntityWithPath, EntityFinder> implements EntityFinder
  {
    /*******************************************************************************************************************
     *
     * FIXME: get rid of this, and use a delegate Finder instead.
     *
     ******************************************************************************************************************/
    public static interface EntityProvider<SOURCE, ENTITY extends EntityWithPath>
      {
        @Nonnull
        public Collection<? extends ENTITY> entities (@Nonnull SOURCE source);

        @Nonnegative
        public int count (@Nonnull SOURCE source);

        @Nonnull
        public static <SOURCE, ENTITY extends EntityWithPath> EntityProvider<SOURCE, ENTITY>
                        of (@Nonnull Function<SOURCE, Collection<? extends ENTITY>> function)
          {
            return new EntityProvider<SOURCE, ENTITY>()
              {
                @Override @Nonnull
                public Collection<? extends ENTITY> entities (final @Nonnull SOURCE source)
                  {
                    return function.apply(source);
                  }

                @Override @Nonnegative
                public int count (final @Nonnull SOURCE source)
                  {
                    return entities(source).size();
                  }
              };
          }
      }

    private static final long serialVersionUID = 4429676480224742813L;

    @Nonnull
    private final MediaFolder mediaFolder;

    @Nonnull
    private final EntityProvider<MediaFolder, EntityWithPath> childrenFactory;

    @Nonnull
    private final Optional<Path> optionalPath;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public FactoryBasedEntityFinder (final @Nonnull MediaFolder mediaFolder,
                                     final @Nonnull EntityProvider<MediaFolder, EntityWithPath> childrenFactory)
      {
        this(mediaFolder, childrenFactory, Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public FactoryBasedEntityFinder (final @Nonnull MediaFolder mediaFolder,
                                     final @Nonnull Function<MediaFolder, Collection<? extends EntityWithPath>> function)
      {
        this(mediaFolder, EntityProvider.of(function), Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public FactoryBasedEntityFinder (final @Nonnull FactoryBasedEntityFinder other, final @Nonnull Object override)
      {
        super(other, override);
        final FactoryBasedEntityFinder source = getSource(FactoryBasedEntityFinder.class, other, override);
        this.mediaFolder = source.mediaFolder;
        this.childrenFactory = source.childrenFactory;
        this.optionalPath = source.optionalPath;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public EntityFinder withPath (final @Nonnull Path path)
      {
        return clone(new FactoryBasedEntityFinder(mediaFolder, childrenFactory, Optional.of(path)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends EntityWithPath> computeResults()
      {
        return new CopyOnWriteArrayList<>(optionalPath.map(this::filteredByPath)
                                                      .orElse(childrenFactory.entities(mediaFolder)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnegative
    public int count()
      {
//        return optionalPath.map(path -> filteredByPath(path).size())
//                           .orElse(childrenFactory.count(mediaFolder));
        return optionalPath.map(this::filteredByPath)
                           .map(Collection::size)
                           .orElse(childrenFactory.count(mediaFolder));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<? extends EntityWithPath> filteredByPath (final @Nonnull Path path)
      {
        if (mediaFolder.getPath().equals(path))
          {
            return Collections.singletonList(mediaFolder);
          }

        try
          {
            final Path relativePath = relative(path);

            final List<EntityWithPath> filtered = childrenFactory.entities(mediaFolder).stream()
                    .filter(entity -> sameHead(relativePath, relative(entity.getPath())))
                    .collect(Collectors.toList());

            if (filtered.isEmpty())
              {
                return filtered;
              }
            else
              {
                assert filtered.size() == 1;
                final EntityWithPath e = filtered.get(0);
                return path.equals(e.getPath()) ? filtered
                                                : ((EntityFinder)asSimpleComposite(e).findChildren())
                                                                                     .withPath(path)
                                                                                     .results();
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
    @Nonnull // FIXME: this should be normally done by as()
    private SimpleComposite8 asSimpleComposite (final @Nonnull As object)
      {
        return (object instanceof SimpleComposite8) ? (SimpleComposite8)object : object.as(SimpleComposite8);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    private Path relative (final @Nullable Path path)
      {
        if (path == null)
          {
            return null;
          }

        return !mediaFolder.getParent().isPresent() ? path :
                path.startsWith(mediaFolder.getPath()) ? path.subpath(mediaFolder.getPath().getNameCount(), path.getNameCount())
                                                       : null;
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
