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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.PriorityAsSupport;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.finder.audio.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.audio.MusicPerformerFinder;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.audio.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicPerformerFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.SourceAwareFinder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.util.PathNormalization.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
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
    public RepositoryEntitySupport (@Nonnull final Repository repository,
                                    @Nonnull final BindingSet bindingSet,
                                    @Nonnull final String idName)
      {
        this(repository, bindingSet, idName, toString(bindingSet.getBinding("label")).orElse(""));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public RepositoryEntitySupport (@Nonnull final Repository repository,
                                    @Nonnull final BindingSet bindingSet,
                                    @Nonnull final String idName,
                                    @Nonnull final String rdfsLabel)
      {
        this.repository = repository;
        this.id = Id.of(toString(bindingSet.getBinding(idName)).get());
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
    protected static Optional<String> toString (@Nullable final Binding binding)
      {
        return Optional.ofNullable(binding).map(Binding::getValue).map(Value::stringValue);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Id> toId (@Nullable final Binding binding)
      {
        return Optional.ofNullable(binding).map(Binding::getValue).map(Value::stringValue).map(Id::new);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Integer> toInteger (@Nullable final Binding binding)
      {
        return Optional.ofNullable(binding).map(Binding::getValue).map(v -> Integer.parseInt(v.stringValue()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Long> toLong (@Nullable final Binding binding)
      {
        return Optional.ofNullable(binding).map(Binding::getValue).map(v -> Long.parseLong(v.stringValue()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Optional<Duration> toDuration (@Nullable final Binding binding)
      {
        return Optional.ofNullable(binding).map(Binding::getValue).map(v -> Duration.ofMillis((int)Float.parseFloat(v.stringValue())));
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
    protected static Path toPath (@Nonnull final Binding binding)
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
    protected <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>> FINDER configured (@Nonnull final FINDER finder)
      {
        return finder.importedFrom(source).withFallback(fallback);
      }
  }
