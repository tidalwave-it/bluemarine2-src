/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
import static java.util.Arrays.*;
import static java.util.Collections.*;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryTrackEntityFinder extends RepositoryFinderSupport<Track, TrackFinder>
                                         implements TrackFinder
  {
    @Nonnull
    private Optional<Id> makerId = Optional.empty();

    @Nonnull
    private Optional<Id> recordId = Optional.empty();

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public RepositoryTrackEntityFinder (final @Nonnull Repository repository)  
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
        final RepositoryTrackEntityFinder clone = clone();
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
        final RepositoryTrackEntityFinder clone = clone();
        clone.recordId = Optional.of(record.getId());
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RepositoryTrackEntityFinder clone()
      {
        final RepositoryTrackEntityFinder clone = (RepositoryTrackEntityFinder)super.clone();
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
        final String q =
              "SELECT *" 
            + "WHERE  {\n" 
            + "       ?track        a                       mo:Track.\n" 
            + "       ?track        rdfs:label              ?label.\n" 
            + "       ?track        mo:track_number         ?track_number.\n" 
                      
            + (!makerId.isPresent()
                    ? ""
                    : "       {\n"
                    + "         ?track        foaf:maker              ?artist.\n"
                    + "       }\n"
                    + "         UNION\n" 
                    + "       {\n"
                    + "         ?track        foaf:maker              ?artistGroup.\n"
                    + "         ?artistGroup  rel:collaboratesWith    ?artist.\n"
                    + "       }\n")
                      
            + (!recordId.isPresent()
                    ? ""
                    : "       {\n"
                    + "         ?record       mo:track                ?track.\n"
                    + "       }\n")
                      
            + "       ?signal       a                       mo:DigitalSignal.\n" 
            + "       ?signal       mo:published_as         ?track.\n"
            + "       ?signal       mo:duration             ?duration.\n" 
                      
            + "       ?audioFile    a                       mo:AudioFile.\n" 
            + "       ?audioFile    mo:encodes              ?signal.\n" 
            + "       ?audioFile    bm:path                 ?path.\n" 

            + "       ?record       a                       mo:Record.\n" 
            + "       ?record       mo:track                ?track.\n" 
            + "       ?record       rdfs:label              ?record_label.\n" 
            + "       }\n"
            + "ORDER BY ?record_label ?track_number ?label";
        
        final List<Object> parameters = new ArrayList<>();
        parameters.addAll(makerId.map(id -> asList("artist", uriFor(id))).orElse(emptyList()));
        parameters.addAll(recordId.map(id -> asList("record", uriFor(id))).orElse(emptyList()));
        
        return query(RepositoryTrack.class, q, parameters.toArray());
      }
  }
