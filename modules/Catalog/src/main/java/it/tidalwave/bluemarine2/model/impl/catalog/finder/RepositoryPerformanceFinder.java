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
import it.tidalwave.bluemarine2.model.Performance;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
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
public class RepositoryPerformanceFinder extends RepositoryFinderSupport<Performance, PerformanceFinder>
                                         implements PerformanceFinder
  {
    private static final long serialVersionUID = -5065032203985453428L;

    private final static String QUERY_PERFORMANCES = readSparql(RepositoryPerformanceFinder.class, "Performances.sparql");

    @Nonnull
    private final Optional<Id> trackId;

    @Nonnull
    private final Optional<Id> performerId;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryPerformanceFinder (final @Nonnull Repository repository)
      {
        this(repository, Optional.empty(), Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryPerformanceFinder (final @Nonnull RepositoryPerformanceFinder other,
                                        final @Nonnull Object override)
      {
        super(other, override);
        final RepositoryPerformanceFinder source = getSource(RepositoryPerformanceFinder.class, other, override);
        this.trackId = source.trackId;
        this.performerId = source.performerId;
      }

    /*******************************************************************************************************************
     *
     * Override constructor.
     *
     ******************************************************************************************************************/
    private RepositoryPerformanceFinder (final @Nonnull Repository repository,
                                         final @Nonnull Optional<Id> trackId,
                                         final @Nonnull Optional<Id> performerId)
      {
        super(repository);
        this.trackId = trackId;
        this.performerId = performerId;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PerformanceFinder ofTrack (final @Nonnull Id trackId)
      {
        return clone(new RepositoryPerformanceFinder(repository, Optional.of(trackId), performerId));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PerformanceFinder performedBy (final @Nonnull Id performerId)
      {
        return clone(new RepositoryPerformanceFinder(repository, trackId, Optional.of(performerId)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected QueryAndParameters prepareQuery()
      {
        return QueryAndParameters.withSparql(QUERY_PERFORMANCES)
                                 .withParameter("track",  trackId.map(this::iriFor))
                                 .withParameter("artist", performerId.map(this::iriFor));
      }
  }
