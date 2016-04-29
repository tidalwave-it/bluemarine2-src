/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

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
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryTrack;
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
public class RepositoryTrackFinder extends RepositoryFinderSupport<Track, TrackFinder>
                                   implements TrackFinder
  {
    private final static String QUERY_TRACKS = readSparql(RepositoryMusicArtistFinder.class, "Tracks.sparql");
    
    @Nonnull
    private final Optional<Id> makerId;

    @Nonnull
    private final Optional<Id> recordId;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryTrackFinder (final @Nonnull Repository repository)
      {
        super(repository);
        this.makerId = Optional.empty();
        this.recordId = Optional.empty();
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryTrackFinder (final @Nonnull RepositoryTrackFinder other, final @Nonnull Object override) 
      {
        super(other, override);
        final RepositoryTrackFinder source = getSource(RepositoryTrackFinder.class, other, override);
        this.makerId = source.makerId;
        this.recordId = source.recordId;
      }
    
    /*******************************************************************************************************************
     *
     * Override constructor.
     *
     ******************************************************************************************************************/
    private RepositoryTrackFinder (final @Nonnull Repository repository,
                                   final @Nonnull Optional<Id> makerId,
                                   final @Nonnull Optional<Id> recordId)
      {
        super(repository);
        this.makerId = makerId;
        this.recordId = recordId;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public TrackFinder madeBy (final @Nonnull MusicArtist artist)  
      {
        return clone(new RepositoryTrackFinder(repository, Optional.of(artist.getId()), recordId));
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public TrackFinder inRecord (final @Nonnull Record record)  
      {
        return clone(new RepositoryTrackFinder(repository, makerId, Optional.of(record.getId())));
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
