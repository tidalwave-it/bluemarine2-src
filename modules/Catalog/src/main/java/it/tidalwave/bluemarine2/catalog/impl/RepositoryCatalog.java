/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog.impl;

import it.tidalwave.bluemarine2.catalog.Catalog;
import it.tidalwave.bluemarine2.catalog.MusicArtistFinder;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.openrdf.repository.Repository;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class RepositoryCatalog implements Catalog
  {
    @Nonnull
    private final Repository repository;
    
    @Override @Nonnull
    public MusicArtistFinder findArtists()
      { 
        return new RepositoryMusicArtistFinder(repository);
      }
  }
