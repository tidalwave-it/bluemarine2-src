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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.time.Duration;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.PriorityAsSupport;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.finder.SourceAwareFinder;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.MusicPerformerFinder;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicPerformerFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import lombok.experimental.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedToNativeForm;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString(of = { "rdfsLabel", "id"}) @Slf4j
public class RepositoryEntitySupport implements Entity, Identifiable
  {
    @Nonnull
    protected final Repository repository;

    @Getter @Nonnull
    protected final Id id;

    @Getter @Nonnull
    protected final String rdfsLabel;

    @Getter @Nonnull
    protected final Optional<Id> source;

    @Getter @Nonnull
    protected final Optional<Id> fallback;

    @Delegate
    private final PriorityAsSupport asSupport = new PriorityAsSupport(this);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public RepositoryEntitySupport (final @Nonnull Repository repository,
                                    final @Nonnull BindingSet bindingSet,
                                    final @Nonnull String idName)
      {
        this(repository, bindingSet, idName, toString(bindingSet.getBinding("label")).orElse(""));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public RepositoryEntitySupport (final @Nonnull Repository repository,
                                    final @Nonnull BindingSet bindingSet,
                                    final @Nonnull String idName,
                                    final @Nonnull String rdfsLabel)
      {
        this.repository = repository;
        this.id = new Id(toString(bindingSet.getBinding(idName)).get());
        this.rdfsLabel = rdfsLabel;
        this.fallback = toId(bindingSet.getBinding("fallback"));
        this.source = toId(Optional.ofNullable(bindingSet.getBinding("source"))
                                       .orElse(bindingSet.getBinding("fallback")));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected MusicArtistFinder _findArtists()
      {
        return configured(new RepositoryMusicArtistFinder(repository));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected MusicPerformerFinder _findPerformers()
      {
        return configured(new RepositoryMusicPerformerFinder(repository));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected RecordFinder _findRecords()
      {
        return configured(new RepositoryRecordFinder(repository));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected TrackFinder _findTracks()
      {
        return configured(new RepositoryTrackFinder(repository));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected PerformanceFinder _findPerformances()
      {
        return configured(new RepositoryPerformanceFinder(repository));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<String> toString (final @Nullable Binding binding)
      {
        return Optional.ofNullable(binding).map(b -> b.getValue()).map(v -> v.stringValue());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Id> toId (final @Nullable Binding binding)
      {
        return Optional.ofNullable(binding).map(b -> b.getValue()).map(v -> v.stringValue()).map(s -> new Id(s));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Integer> toInteger (final @Nullable Binding binding)
      {
        return Optional.ofNullable(binding).map(b -> b.getValue()).map(v -> Integer.parseInt(v.stringValue()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Long> toLong (final @Nullable Binding binding)
      {
        return Optional.ofNullable(binding).map(b -> b.getValue()).map(v -> Long.parseLong(v.stringValue()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Duration> toDuration (final @Nullable Binding binding)
      {
        return Optional.ofNullable(binding).map(b -> b.getValue()).map(v -> Duration.ofMillis((int)Float.parseFloat(v.stringValue())));
      }

    /*******************************************************************************************************************
     *
     * Tries to fix a path for character normalization issues (see BMT-46). The idea is to first normalize the encoding
     * to the native form. If it doesn't work, a broken path is replaced to avoid further errors (of course, the
     * resource won't be available when requested).
     * It doesn't try to call normalizedPath() because it's expensive.
     *
     * @param   binding     the binding
     * @return              the path
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Path toPath (final @Nonnull Binding binding)
      {
        try // FIXME: see BMT-46 - try all posibile normalizations
          {
            return Paths.get(normalizedToNativeForm(toString(binding).get()));
          }
        catch (InvalidPathException e)
          {
            // FIXME: perhaps we could try a similar trick to normalizedPath() - the problem being the fact that it
            // currently accepts a Path, but we can't convert to a Path. It should be rewritten to work with a String
            // in input.
            log.error("Invalid path {}", e.toString());
            return Paths.get("broken SEE BMT-46");
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>> FINDER configured (final @Nonnull FINDER finder)
      {
        return finder.importedFrom(source).withFallback(fallback);
      }
  }
