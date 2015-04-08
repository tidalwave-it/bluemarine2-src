/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog.impl;

import it.tidalwave.bluemarine2.catalog.MusicArtist;
import it.tidalwave.bluemarine2.catalog.MusicArtistFinder;
import javax.annotation.Nonnull;
import java.util.List;
import org.openrdf.repository.Repository;

/***********************************************************************************************************************
 *
 * @author  fritz
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
              "SELECT *"
            + "WHERE  {\n" 
            + "       ?artist a                 mo:MusicArtist.\n" 
            + "       ?artist rdfs:label        ?label.\n" 
            + "       ?artist vocab:artist_type \"1\"^^xs:short"
//            + "       OPTIONAL { ?artist vocab:artist_type \"1\"^^xs:short }"
            + "       }\n" 
            + "ORDER BY ?label");
      }
  }
