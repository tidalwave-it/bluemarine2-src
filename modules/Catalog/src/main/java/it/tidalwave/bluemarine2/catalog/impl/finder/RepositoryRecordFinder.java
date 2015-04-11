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
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.catalog.impl.RepositoryRecord;
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
public class RepositoryRecordFinder extends RepositoryFinderSupport<Record, RecordFinder>
                                    implements RecordFinder
  {
    @Nonnull
    private Optional<Id> makerId = Optional.empty();

    @Nonnull
    private Optional<Id> trackId = Optional.empty();

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public RepositoryRecordFinder (final @Nonnull Repository repository)  
      {
        super(repository);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder madeBy (final @Nonnull MusicArtist artist)  
      {
        final RepositoryRecordFinder clone = clone();
        clone.makerId = Optional.of(artist.getId());
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder recordOf (final @Nonnull Id trackId)  
      {
        final RepositoryRecordFinder clone = clone();
        clone.trackId = Optional.of(trackId);
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RepositoryRecordFinder clone()
      {
        final RepositoryRecordFinder clone = (RepositoryRecordFinder)super.clone();
        clone.makerId = this.makerId;
        clone.trackId = this.trackId;

        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends Record> computeNeededResults() 
      {
        final String q =
              "SELECT *\n" 
            + "WHERE  {\n" 
            + "       ?record       a                       mo:Record.\n" 
            + "       ?record       rdfs:label              ?label.\n" 
////            + "       ?track        mo:track_count         ?track_number.\n" 
//                      
            + (!makerId.isPresent()
                    ? ""
                    : "       {\n"
                    + "         ?record       foaf:maker              ?artist.\n"
                    + "       }\n"
                    + "         UNION\n" 
                    + "       {\n"
                    + "         ?record       foaf:maker              ?artistGroup.\n"
                    + "         ?artistGroup  rel:collaboratesWith    ?artist.\n"
                    + "       }\n")
                
            + (!trackId.isPresent()
                    ? ""
                    : "       ?record       mo:track              ?track.\n")
                
            + "       }\n"
            + "ORDER BY ?label";
        
        final List<Object> parameters = new ArrayList<>();
        parameters.addAll(makerId.map(id -> asList("artist", uriFor(id))).orElse(emptyList()));
        parameters.addAll(trackId.map(id -> asList("track", uriFor(id))).orElse(emptyList()));
        
        return query(RepositoryRecord.class, q, parameters.toArray());
      }
  }
