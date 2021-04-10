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
import javax.annotation.concurrent.Immutable;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.audio.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * An implementation of {@link MusicArtist} that is mapped to a {@link Repository}.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @Getter
public class RepositoryMusicArtist extends RepositoryEntitySupport implements MusicArtist
  {
    @Getter
    private final int type;

    public RepositoryMusicArtist (@Nonnull final Repository repository, @Nonnull final BindingSet bindingSet)
      {
        super(repository, bindingSet, "artist");
        type = toInteger(bindingSet.getBinding("artist_type")).orElse(1);
      }

    @Override @Nonnull
    public TrackFinder findTracks()
      {
        return _findTracks().madeBy(this);
      }

    @Override @Nonnull
    public RecordFinder findRecords()
      {
        return _findRecords().madeBy(this);
      }

    @Override @Nonnull
    public PerformanceFinder findPerformances()
      {
        return _findPerformances().performedBy(this);
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryMusicArtist(rdfs:label=%s, uri=%s)", rdfsLabel, id);
      }
  }
