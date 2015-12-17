/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.catalog.impl.finder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openrdf.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.catalog.impl.RepositoryMusicArtist;
import lombok.ToString;
import static java.util.Arrays.*;
import static java.util.Collections.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryMusicArtistFinder extends RepositoryFinderSupport<MusicArtist, MusicArtistFinder> 
                                         implements MusicArtistFinder 
  {
    private final static String QUERY_ARTISTS = readSparql(RepositoryMusicArtistFinder.class, "AllMusicArtists.sparql");
    private final static String QUERY_ARTISTS_MAKER_OF = readSparql(RepositoryMusicArtistFinder.class, "MakerArtists.sparql");
    
    @Nonnull
    private final Optional<Id> madeEntityId;
    
    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryMusicArtistFinder (final @Nonnull Repository repository)
      {
        super(repository);
        this.madeEntityId = Optional.empty();
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryMusicArtistFinder (final @Nonnull RepositoryMusicArtistFinder other,
                                        final @Nonnull Object override) 
      {
        super(other, override);
        final RepositoryMusicArtistFinder source = getSource(RepositoryMusicArtistFinder.class, other, override);
        this.madeEntityId = source.madeEntityId;
      }
    
    /*******************************************************************************************************************
     *
     * Override constructor.
     *
     ******************************************************************************************************************/
    private RepositoryMusicArtistFinder (final @Nonnull Repository repository, 
                                         final @Nonnull Optional<Id> madeEntityId) 
      {
        super(repository);
        this.madeEntityId = madeEntityId;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public MusicArtistFinder makerOf (final @Nonnull Id madeEntityId)
      {
        return clone(new RepositoryMusicArtistFinder(repository, Optional.of(madeEntityId)));
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends MusicArtist> computeNeededResults() 
      {
        final List<Object> parameters = new ArrayList<>();
        parameters.addAll(madeEntityId.map(id -> asList("madeEntity", uriFor(id))).orElse(emptyList()));
        
        // Two different queries because for the 'makerOf' we want to include collborations, as it's important their label
        // In other words, 'Ella Fitzgerald and Duke Ellington' matters, rather than a list of the two individuals
        return madeEntityId.isPresent()
                ? query(RepositoryMusicArtist.class, QUERY_ARTISTS_MAKER_OF, parameters.toArray())
                : query(RepositoryMusicArtist.class, QUERY_ARTISTS);
      }
  }
