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
package it.tidalwave.bluemarine2.catalog.impl;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.List;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.catalog.Record;
import it.tidalwave.bluemarine2.catalog.RecordFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RepositoryRecordEntityFinder extends RepositoryFinderSupport<Record, RecordFinder>
                                          implements RecordFinder
  {
    @CheckForNull
    private Id makerId;

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public RepositoryRecordEntityFinder (final @Nonnull Repository repository)  
      {
        super(repository);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder withMaker (final @Nonnull Id artistId)  
      {
        final RepositoryRecordEntityFinder clone = clone();
        clone.makerId = artistId;
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RepositoryRecordEntityFinder clone()
      {
        final RepositoryRecordEntityFinder clone = (RepositoryRecordEntityFinder)super.clone();
        clone.makerId = this.makerId;

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
              "SELECT *" 
            + "WHERE  {\n" 
            + "       ?record       a                       mo:Record.\n" 
            + "       ?record       rdfs:label              ?label.\n" 
////            + "       ?track        mo:track_count         ?track_number.\n" 
//                      
            + ((makerId == null)
                    ? ""
                    : "       {\n"
                    + "         ?record       foaf:maker              ?artist.\n"
                    + "       }\n"
                    + "         UNION\n" 
                    + "       {\n"
                    + "         ?record       foaf:maker              ?artistGroup.\n"
                    + "         ?artistGroup  rel:collaboratesWith    ?artist.\n"
                    + "       }\n")
                
            + "       }\n"
            + "ORDER BY ?label";
        
        return (makerId == null) ? query(RepositoryRecordEntity.class, q)
                                  : query(RepositoryRecordEntity.class, q, 
                                         "artist", ValueFactoryImpl.getInstance().createURI(makerId.stringValue()));
      }
  }
