/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

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
public class RepositoryCatalog 
  {
    @Nonnull
    private final Repository repository;
    
    @Nonnull
    public MusicArtistFinder findArtists()
      { 
        return new RepositoryMusicArtistFinder(repository);
      }
  }
