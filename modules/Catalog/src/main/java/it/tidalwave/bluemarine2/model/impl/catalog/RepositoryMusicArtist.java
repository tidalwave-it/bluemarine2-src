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
import javax.annotation.concurrent.Immutable;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.BindingSet;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * An implementation of {@link MusicArtist} that is mapped to a {@link Repository}.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter
public class RepositoryMusicArtist extends RepositoryEntitySupport implements MusicArtist
  {
    @Getter
    private final int type;

    public RepositoryMusicArtist (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "artist");
        type = toInteger(bindingSet.getBinding("artist_type")).orElse(1);
      }

    @Override @Nonnull
    public TrackFinder findTracks()
      {
        return new RepositoryTrackFinder(repository).madeBy(this);
      }

    @Override @Nonnull
    public RecordFinder findRecords()
      {
        return new RepositoryRecordFinder(repository).madeBy(this);
      }

    @Override @Nonnull
    public PerformanceFinder findPerformances()
      {
        return new RepositoryPerformanceFinder(repository).performedBy(this);
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryMusicArtist(rdfs:label=%s, uri=%s)", rdfsLabel, id);
      }
  }
