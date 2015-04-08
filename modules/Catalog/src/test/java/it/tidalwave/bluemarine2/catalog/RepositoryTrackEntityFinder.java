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
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import it.tidalwave.util.Id;
import it.tidalwave.util.Finder8Support;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class RepositoryTrackEntityFinder extends Finder8Support<TrackEntity, TrackEntityFinder> implements TrackEntityFinder 
  {
    @Nonnull
    private Repository repository;
    
    @CheckForNull
    private Id artistId;
    
    @Override @Nonnull
    protected List<? extends TrackEntity> computeNeededResults() 
      {
        try 
          {
            return QueryUtilities.query(repository, TrackEntity.class, QueryUtilities.PREFIXES  
                + "SELECT *"
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
        catch (RepositoryException | MalformedQueryException | QueryEvaluationException e)
          {
            throw new RuntimeException(e);
          }
      }

    @Override @Nonnull
    public TrackEntityFinder withArtistId (final @Nonnull Id artistId)  
      {
        final RepositoryTrackEntityFinder clone = (RepositoryTrackEntityFinder)clone();
        this.artistId = artistId;
        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * Clones this object. This operation is called whenever a parameter-setting method is called in fluent-interface
     * style.
     *
     * @return  the cloned object
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RepositoryTrackEntityFinder clone()
      {
        final RepositoryTrackEntityFinder clone = (RepositoryTrackEntityFinder)super.clone();
        clone.repository  = this.repository;
        clone.artistId    = this.artistId;

        return clone;
      }
  }
