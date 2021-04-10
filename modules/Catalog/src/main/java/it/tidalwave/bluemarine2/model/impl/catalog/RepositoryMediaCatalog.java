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
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.finder.audio.AudioFileFinder;
import it.tidalwave.bluemarine2.model.finder.audio.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.audio.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryAudioFileFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import it.tidalwave.bluemarine2.model.spi.SourceAwareFinder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.vocabulary.BMMO.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
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

    public void setSourceAsString (@Nonnull final String sourceAsString)
      {
        setSource(Id.of(sourceAsString));
      }

    public void setFallbackAsString (@Nonnull final String fallbackAsString)
      {
        setFallback(Id.of(fallbackAsString));
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
    private <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>> FINDER configured (@Nonnull final FINDER finder)
      {
        return finder.importedFrom(getSource()).withFallback(getFallback());
      }
  }
