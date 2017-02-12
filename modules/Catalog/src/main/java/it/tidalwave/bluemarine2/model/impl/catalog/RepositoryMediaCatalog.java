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
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.finder.AudioFileFinder;
import it.tidalwave.bluemarine2.model.finder.SourceAwareFinder;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryAudioFileFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import static it.tidalwave.bluemarine2.model.vocabulary.BM.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class RepositoryMediaCatalog implements MediaCatalog
  {
    @Getter @Setter
    private Id source = ID_SOURCE_EMBEDDED;

    @Getter @Setter
    private Id fallback = ID_SOURCE_EMBEDDED;

    @Inject
    private Provider<Repository> repository;

    public void setSourceAsString (final @Nonnull String sourceAsString)
      {
        setSource(new Id(sourceAsString));
      }

    public void setFallbackAsString (final @Nonnull String fallbackAsString)
      {
        setFallback(new Id(fallbackAsString));
      }

    @Override @Nonnull
    public MusicArtistFinder findArtists()
      {
        return configured(new RepositoryMusicArtistFinder(repository.get()));
      }

    @Override @Nonnull
    public RecordFinder findRecords()
      {
        return configured(new RepositoryRecordFinder(repository.get()));
      }

    @Override @Nonnull
    public TrackFinder findTracks()
      {
        return configured(new RepositoryTrackFinder(repository.get()));
      }

    @Override @Nonnull
    public PerformanceFinder findPerformances()
      {
        return configured(new RepositoryPerformanceFinder(repository.get()));
      }

    @Override @Nonnull
    public AudioFileFinder findAudioFiles()
      {
        return configured(new RepositoryAudioFileFinder(repository.get()));
      }

    @PostConstruct
    private void initialize()
      {
        log.info("Catalog configuration source: {} fallback: {}", getSource(), getFallback());
      }

    @Nonnull
    private <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>> FINDER configured (final @Nonnull FINDER finder)
      {
        return finder.importedFrom(getSource()).withFallback(getFallback());
      }
  }
