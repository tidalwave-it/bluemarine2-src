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
public interface TrackEntityFinder extends Finder8<TrackEntity>
  {
    @Nonnull
    public TrackEntityFinder withArtistId (@Nonnull Id artistId);
  }
