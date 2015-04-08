/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface Catalog 
  {
    public static final Class<Catalog> Catalog = Catalog.class;
    
    @Nonnull
    public MusicArtistFinder findArtists();
  }
