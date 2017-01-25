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
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.bluemarine2.model.vocabulary.BM.*;
import it.tidalwave.bluemarine2.model.finder.SourceAwareFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class RepositoryMediaCatalog implements MediaCatalog
  {
    @Nonnull
    private final Repository repository;

    @Override @Nonnull
    public MusicArtistFinder findArtists()
      {
        return configured(new RepositoryMusicArtistFinder(repository));
      }

    @Override @Nonnull
    public RecordFinder findRecords()
      {
        return configured(new RepositoryRecordFinder(repository));
      }

    @Override @Nonnull
    public TrackFinder findTracks()
      {
        return configured(new RepositoryTrackFinder(repository));
      }

    @Override @Nonnull
    public PerformanceFinder findPerformances()
      {
        return configured(new RepositoryPerformanceFinder(repository));
      }

    @Nonnull
    private <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>> FINDER configured (final @Nonnull FINDER finder)
      {
        return finder.importedFrom(getSource()).withFallback(getFallback());
      }

    @Nonnull
    private Id getSource()
      {
        return new Id(System.getProperty("bluemarine2.source", ID_SOURCE_EMBEDDED.stringValue())); // FIXME: get from Preferences
      }

    @Nonnull
    private Id getFallback()
      {
        return new Id(System.getProperty("bluemarine2.fallback", ID_SOURCE_EMBEDDED.stringValue())); // FIXME: get from Preferences
      }
  }
