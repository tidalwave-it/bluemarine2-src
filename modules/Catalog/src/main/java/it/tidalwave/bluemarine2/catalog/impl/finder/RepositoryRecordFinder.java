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
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.catalog.impl.RepositoryRecord;
import lombok.ToString;
import static java.util.Arrays.*;
import static java.util.Collections.*;

/***********************************************************************************************************************
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryRecordFinder extends RepositoryFinderSupport<Record, RecordFinder>
                                    implements RecordFinder
  {
    private final static String QUERY_RECORDS = readSparql(RepositoryMusicArtistFinder.class, "Records.sparql");

    @Nonnull
    private final Optional<Id> makerId;

    @Nonnull
    private final Optional<Id> trackId;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryRecordFinder (final @Nonnull Repository repository)
      {
        super(repository);
        this.makerId = Optional.empty();
        this.trackId = Optional.empty();
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryRecordFinder (final @Nonnull RepositoryRecordFinder other, final @Nonnull Object override) 
      {
        super(other, override);
        final RepositoryRecordFinder source = getSource(RepositoryRecordFinder.class, other, override);
        this.makerId = source.makerId;
        this.trackId = source.trackId;
      }
    
    /*******************************************************************************************************************
     *
     * Override constructor.
     *
     ******************************************************************************************************************/
    private RepositoryRecordFinder (final @Nonnull Repository repository,
                                    final @Nonnull Optional<Id> makerId,
                                    final @Nonnull Optional<Id> trackId)
      {
        super(repository);
        this.makerId = makerId;
        this.trackId = trackId;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder madeBy (final @Nonnull MusicArtist artist)
      {
        return clone(new RepositoryRecordFinder(repository, Optional.of(artist.getId()), trackId));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder recordOf (final @Nonnull Id trackId)
      {
        return clone(new RepositoryRecordFinder(repository, makerId, Optional.of(trackId)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends Record> computeNeededResults()
      {
        final List<Object> parameters = new ArrayList<>();
        parameters.addAll(makerId.map(id -> asList("artist", uriFor(id))).orElse(emptyList()));
        parameters.addAll(trackId.map(id -> asList("track", uriFor(id))).orElse(emptyList()));

        return query(RepositoryRecord.class, QUERY_RECORDS, parameters.toArray());
      }
  }
