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
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryTrackFinder extends RepositoryFinderSupport<Track, TrackFinder>
                                   implements TrackFinder
  {
    private static final long serialVersionUID = 770942161753738572L;

    private final static String QUERY_TRACKS = readSparql(RepositoryTrackFinder.class, "Tracks.sparql");

    @Nonnull
    protected final Optional<Id> makerId;

    @Nonnull
    protected final Optional<Id> recordId;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryTrackFinder (final @Nonnull Repository repository)
      {
        this(repository, Optional.empty(), Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryTrackFinder (final @Nonnull RepositoryTrackFinder other, final @Nonnull Object override)
      {
        super(other, override);
        final RepositoryTrackFinder source = getSource(RepositoryTrackFinder.class, other, override);
        this.makerId = source.makerId;
        this.recordId = source.recordId;
      }

    /*******************************************************************************************************************
     *
     * Override constructor.
     *
     ******************************************************************************************************************/
    private RepositoryTrackFinder (final @Nonnull Repository repository,
                                   final @Nonnull Optional<Id> makerId,
                                   final @Nonnull Optional<Id> recordId)
      {
        super(repository, "track");
        this.makerId = makerId;
        this.recordId = recordId;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public TrackFinder madeBy (final @Nonnull Id artistId)
      {
        return clone(new RepositoryTrackFinder(repository, Optional.of(artistId), recordId));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public TrackFinder inRecord (final @Nonnull Id recordId)
      {
        return clone(new RepositoryTrackFinder(repository, makerId, Optional.of(recordId)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected QueryAndParameters prepareQuery()
      {
        return QueryAndParameters.withSparql(QUERY_TRACKS)
                                 .withParameter("artist", makerId.map(this::iriFor))
                                 .withParameter("record", recordId.map(this::iriFor));
      }
  }
