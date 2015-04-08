/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.List;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import it.tidalwave.util.Id;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RepositoryTrackEntityFinder extends RepositoryFinderSupport<TrackEntity, TrackEntityFinder>
                                         implements TrackEntityFinder
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
    public TrackEntityFinder withArtistId (final @Nonnull Id artistId)  
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
    protected List<? extends TrackEntity> computeNeededResults() 
      {
        return query(TrackEntity.class,  
              "SELECT *"
            + "WHERE  {\n" 
            + "       ?track  a                 mo:Track.\n" 
            + "       ?track  foaf:maker        ?artist.\n" 
            + "       ?track  rdfs:label        ?label.\n" 
            + "       ?track  mo:track_number   ?track_number.\n" 
            + "       ?track  mo:duration       ?duration.\n" 
            + "       ?track  mo:AudioFile      ?audioFile.\n" // FIXME: wrong! This is a type, not a property!
            + "       ?record mo:track          ?track.\n" 
            + "       ?record rdfs:label        ?record_label.\n" 
            + "       }\n"
            + "ORDER BY ?record_label ?track_number ?label",
            "artist", ValueFactoryImpl.getInstance().createURI(artistId.stringValue()));
      }
  }
