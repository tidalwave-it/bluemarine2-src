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
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import lombok.ToString;

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
    private static final long serialVersionUID = -6899011281060253740L;

    private final static String QUERY_RECORDS = readSparql(RepositoryRecordFinder.class, "Records.sparql");

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
        this(repository, Optional.empty(), Optional.empty());
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
        super(repository, "record");
        this.makerId = makerId;
        this.trackId = trackId;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder madeBy (final @Nonnull Id artistId)
      {
        return clone(new RepositoryRecordFinder(repository, Optional.of(artistId), trackId));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RecordFinder containingTrack (final @Nonnull Id trackId)
      {
        return clone(new RepositoryRecordFinder(repository, makerId, Optional.of(trackId)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected QueryAndParameters prepareQuery()
      {
        return QueryAndParameters.withSparql(QUERY_RECORDS)
                                 .withParameter("artist",   makerId.map(this::iriFor))
                                 .withParameter("track",    trackId.map(this::iriFor));
      }
  }
