/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import javax.annotation.Nonnull;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Id;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface TrackFinder extends Finder8<Track>
  {
    @Nonnull
    public TrackFinder withArtistId (@Nonnull Id artistId);
  }
