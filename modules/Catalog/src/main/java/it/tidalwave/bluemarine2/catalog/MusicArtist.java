/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import it.tidalwave.bluemarine2.model.Entity;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface MusicArtist extends Entity
  {
    public static final Class<MusicArtist> MusicArtist = MusicArtist.class;
    
    @Nonnull
    public TrackFinder findTracks();
  }
