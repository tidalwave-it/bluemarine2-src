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
package it.tidalwave.bluemarine2.catalog.impl.finder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openrdf.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.catalog.impl.RepositoryTrack;
import it.tidalwave.util.Finder8;
import lombok.ToString;
import static java.util.Arrays.*;
import static java.util.Collections.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryTrackFinder extends RepositoryFinderSupport<Track, Finder8<Track>>
                                         implements TrackFinder
  {
    private final static String QUERY_TRACKS = readSparql(RepositoryMusicArtistFinder.class, "Tracks.sparql");
    
    @Nonnull
    private Optional<Id> makerId = Optional.empty();

    @Nonnull
    private Optional<Id> recordId = Optional.empty();

    @Override // FIXME
    public TrackFinder withContext(Object context) {
        return (TrackFinder)this;
    }
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public RepositoryTrackFinder (final @Nonnull Repository repository)  
      {
        super(repository);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public TrackFinder madeBy (final @Nonnull MusicArtist artist)  
      {
        final RepositoryTrackFinder clone = clone();
        clone.makerId = Optional.of(artist.getId());
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public TrackFinder inRecord (final @Nonnull Record record)  
      {
        final RepositoryTrackFinder clone = clone();
        clone.recordId = Optional.of(record.getId());
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RepositoryTrackFinder clone()
      {
        final RepositoryTrackFinder clone = (RepositoryTrackFinder)super.clone();
        clone.makerId = this.makerId;
        clone.recordId = this.recordId;

        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends Track> computeNeededResults() 
      {
        final List<Object> parameters = new ArrayList<>();
        parameters.addAll(makerId.map(id -> asList("artist", uriFor(id))).orElse(emptyList()));
        parameters.addAll(recordId.map(id -> asList("record", uriFor(id))).orElse(emptyList()));
        
        return query(RepositoryTrack.class, QUERY_TRACKS, parameters.toArray());
      }
  }
