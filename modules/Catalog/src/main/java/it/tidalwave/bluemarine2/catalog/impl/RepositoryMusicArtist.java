/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.catalog.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.openrdf.repository.Repository;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.catalog.impl.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.catalog.impl.finder.RepositoryTrackFinder;
import lombok.Getter;
import org.openrdf.query.BindingSet;

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
    private int type;
    
    public RepositoryMusicArtist (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "artist");
        type = Integer.parseInt(bindingSet.getBinding("artist_type").getValue().stringValue());
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
    public String toString() 
      {
        return String.format("RepositoryMusicArtist(rdfs:label=%s, uri=%s)", rdfsLabel, id);
      }
  }
