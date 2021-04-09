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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.nio.file.Path;
import it.tidalwave.util.As;
import it.tidalwave.util.Finder;
import it.tidalwave.util.spi.FinderSupport;
import it.tidalwave.util.SupplierBasedFinder;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.*;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A decorator of an {@link Finder} of {@link PathAwareEntity} that creates a virtual tree of entities. Each entity is
 * given a path, which starts with the path of a {@link MediaFolder} and continues with the id of the entity.
 *
 * This {@code Finder} can filtered by path. If a filter path is provided, the filtering happens in memory: this means
 * that even when the delegate queries a native store, all the data are first retrieved in memory.
 *
 * @stereotype  Finder
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor(access = PRIVATE) @Slf4j
public class PathAwareEntityFinderDelegate extends FinderSupport<PathAwareEntity, PathAwareFinder> implements PathAwareFinder
  {
    private static final long serialVersionUID = 4429676480224742813L;

    @Nonnull
    private final MediaFolder mediaFolder;

    @Nonnull
    private final Finder<PathAwareEntity> delegate;

    @Nonnull
    private final Optional<Path> optionalPath;

    /*******************************************************************************************************************
     *
     * Creates an instance associated to a given {@link MediaFolder} and a delegate finder.
     *
     * @see #PathAwareEntityFinderDelegate(it.tidalwave.bluemarine2.model.MediaFolder, java.util.function.Function)
     *
     * @param   mediaFolder     the folder associated to this finder
     * @param   delegate        the delegate finder to provide data
     *
     ******************************************************************************************************************/
    public PathAwareEntityFinderDelegate (final @Nonnull MediaFolder mediaFolder,
                                          final @Nonnull Finder<PathAwareEntity> delegate)
      {
        this(mediaFolder, delegate, Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Creates an instance associated to a given {@link MediaFolder} and a function for providing children. This
     * constructor is typically used when the children are already present in memory (e.g. they are
     * {@link VirtualMediaFolder}s. Because the function doesn't have the full semantics of a {@link Finder} - it can't
     * optimise a query in function of search parameters, nor optimise the count of results - when a
     * {@code PathAwareEntityFinderDelegate} is created in this way all operations will be performed in memory. If one
     * can provide data from a native store and enjoy optimised queries, instead of this constructor use
     * {@link #PathAwareEntityFinderDelegate(it.tidalwave.bluemarine2.model.MediaFolder, it.tidalwave.util.Finder)}
     *
     * @see #PathAwareEntityFinderDelegate(it.tidalwave.bluemarine2.model.MediaFolder, it.tidalwave.util.Finder)
     *
     * @param   mediaFolder     the folder associated to this finder
     * @param   function        the function that provides children
     *
     ******************************************************************************************************************/
    public PathAwareEntityFinderDelegate (final @Nonnull MediaFolder mediaFolder,
                                          final @Nonnull Function<MediaFolder, Collection<? extends PathAwareEntity>> function)
      {
        this(mediaFolder, new SupplierBasedFinder<>(() -> function.apply(mediaFolder),
                                                    () -> mediaFolder.findChildren().count()),
             Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public PathAwareEntityFinderDelegate (final @Nonnull PathAwareEntityFinderDelegate other,
                                          final @Nonnull Object override)
      {
        super(other, override);
        final PathAwareEntityFinderDelegate source = getSource(PathAwareEntityFinderDelegate.class, other, override);
        this.mediaFolder = source.mediaFolder;
        this.delegate = source.delegate;
        this.optionalPath = source.optionalPath;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PathAwareFinder withPath (final @Nonnull Path path)
      {
        return clonedWith(new PathAwareEntityFinderDelegate(mediaFolder, delegate, Optional.of(path)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends PathAwareEntity> computeResults()
      {
        return new CopyOnWriteArrayList<>(optionalPath.flatMap(path -> filteredByPath(path).map(e -> singletonList(e)))
                                                      .orElse((List)delegate.results()));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnegative
    public int count()
      {
        optionalPath.ifPresent(path -> log.warn("Path present: {} - count won't be a native query", path));
        return optionalPath.map(path -> filteredByPath(path).map(entity -> 1).orElse(0))
                           .orElse(delegate.count());
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<? extends PathAwareEntity> filteredByPath (final @Nonnull Path path)
      {
        log.debug("filteredByPath({})", path);
        return mediaFolder.getPath().equals(path)
                                        ? Optional.of(mediaFolder)
                                        : childMatchingPathHead(path).flatMap(entity -> path.equals(entity.getPath())
                                                ? Optional.of(entity)
                                                : childMatchingPath(entity, path));
      }

    /*******************************************************************************************************************
     *
     * Returns the child entity that matches the first element of the path, if present. The path can be exactly the one
     * of the found entity, or it can be of one of its children.
     *
     * This method performs a bulk query of all children and then filters by path in memory. It is not possible to
     * use a query to the native store for the path - which would be good for performance reasons - , because even
     * though each segment of the path is function of some attribute of the related {@code PathAwareEntity} - typically
     * the id - it is not a matter of the native store. Performance of this section relies upon memory caching. Some
     * experiment showed that it's not useful to add another caching layer here, and the one in
     * {@code RepositoryFinderSupport} is enough.
     *
     * @param   path    the path
     * @return          the entity, if present
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<PathAwareEntity> childMatchingPathHead (final @Nonnull Path path)
      {
//                assert filtered.size() == 1 or 0;
        log.debug(">>>> bulk query to {}, filtering in memory", delegate);
        return (Optional<PathAwareEntity>)delegate.results().stream()
                                                  .filter(entity -> sameHead(relative(path), relative(entity.getPath())))
                                                  .findFirst();
      }

    /*******************************************************************************************************************
     *
     * @param   entity
     * @param   path    the path
     * @return          the entity, if present
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Optional<PathAwareEntity> childMatchingPath (final @Nonnull PathAwareEntity entity,
                                                                final @Nonnull Path path)
      {
        return ((PathAwareFinder)asSimpleComposite(entity).findChildren()).withPath(path).optionalResult();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull // FIXME: this should be normally done by as()
    private static SimpleComposite asSimpleComposite (final @Nonnull As object)
      {
        return (object instanceof SimpleComposite) ? (SimpleComposite)object : object.as(_SimpleComposite_);
      }

    /*******************************************************************************************************************
     *
     * Relativizes a path against the finder path, that is it removes the parent path. If the path can't be
     * relativized, that is it doesn't start with the finder path, returns null.
     *
     ******************************************************************************************************************/
    @Nullable
    private Path relative (final @Nonnull Path path)
      {
        return mediaFolder.getParent().isEmpty() ? path :
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
        return (path1 != null) && (path2 != null) && path1.getName(0).equals(path2.getName(0));
      }
  }
