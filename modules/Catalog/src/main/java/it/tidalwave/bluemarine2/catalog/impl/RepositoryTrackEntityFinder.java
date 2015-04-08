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
import it.tidalwave.bluemarine2.catalog.Track;
import it.tidalwave.bluemarine2.catalog.TrackFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RepositoryTrackEntityFinder extends RepositoryFinderSupport<Track, TrackFinder>
                                         implements TrackFinder
  {
    @CheckForNull
    private Id artistId;

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
    public TrackFinder withArtistId (final @Nonnull Id artistId)  
      {
        final RepositoryTrackEntityFinder clone = clone();
        clone.artistId = artistId;
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
        clone.artistId = this.artistId;

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
                      
            + ((artistId == null)
                    ? ""
                    : "       {\n"
                    + "         ?track        foaf:maker              ?artist.\n"
                    + "       }\n"
                    + "         UNION\n" 
                    + "       {\n"
                    + "         ?track        foaf:maker              ?artistGroup.\n"
                    + "         ?artistGroup  rel:collaboratesWith    ?artist.\n"
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
        
        return (artistId == null) ? query(RepositoryTrackEntity.class, q)
                                  : query(RepositoryTrackEntity.class, q, 
                                         "artist", ValueFactoryImpl.getInstance().createURI(artistId.stringValue()));
      }
  }
