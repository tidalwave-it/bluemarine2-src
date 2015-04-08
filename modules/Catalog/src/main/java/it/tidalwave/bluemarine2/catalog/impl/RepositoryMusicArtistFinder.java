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

import javax.annotation.Nonnull;
import java.util.List;
import org.openrdf.repository.Repository;
import it.tidalwave.bluemarine2.catalog.MusicArtist;
import it.tidalwave.bluemarine2.catalog.MusicArtistFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RepositoryMusicArtistFinder extends RepositoryFinderSupport<MusicArtist, MusicArtistFinder> 
                                         implements MusicArtistFinder 
  {
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public RepositoryMusicArtistFinder (final @Nonnull Repository repository)
      {
        super(repository);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends MusicArtist> computeNeededResults() 
      {
        return query(RepositoryMusicArtistEntity.class,  
              "SELECT DISTINCT *"
            + "WHERE  {\n" 
            + "       ?artist       a                 mo:MusicArtist.\n" 
            + "       ?artist       rdfs:label        ?label.\n" 
                // Single artists
            + "         {\n"
            + "            ?artist      vocab:artist_type       \"1\"^^xs:short.\n"
            + "         }\n"
            + "       UNION\n"
                // Groups for which there is no defined collaboration with single persons
            + "         {\n"
            + "             ?artist     vocab:artist_type       \"2\"^^xs:short.\n"
            + "             FILTER NOT EXISTS { ?artist     rel:collaboratesWith    ?any1 }\n"
            + "         }\n"
            + "       UNION\n"
                // Some artists don't have this attribute
            + "         {\n"
            + "             MINUS { ?artist     vocab:artist_type       ?any2 }\n"
//            + "             FILTER NOT EXISTS { ?artist     vocab:artist_type       ?any2 }\n"
            + "         }\n"
            + "       }\n" 
            + "ORDER BY ?label");
      }
  }
