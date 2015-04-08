/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import it.tidalwave.bluemarine2.model.Entity;

/***********************************************************************************************************************
 *
 * NOTE: a Track is an abstract concept - it is associated to MediaItems (as AudioFiles), but it's not a MediaItem.
 * 
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface Track extends Entity
  {
    public static final Class<Track> Track = Track.class;
  }
