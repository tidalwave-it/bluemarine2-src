/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

import javax.annotation.Nonnull;
import java.util.Optional;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.finder.audio.MusicArtistFinder;
import lombok.ToString;

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
    private static final long serialVersionUID = 2271161001432418095L;

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
        this(repository, Optional.empty());
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
        super(repository, "artist");
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
    protected QueryAndParameters prepareQuery()
      {
        // Two different queries because for the 'makerOf' we want to include collborations, as it's important their label
        // In other words, 'Ella Fitzgerald and Duke Ellington' matters, rather than a list of the two individuals
        return QueryAndParameters.withSparql(madeEntityId.isPresent() ? QUERY_ARTISTS_MAKER_OF : QUERY_ARTISTS)
                                 .withParameter("madeEntity", madeEntityId.map(this::iriFor));
      }
  }
