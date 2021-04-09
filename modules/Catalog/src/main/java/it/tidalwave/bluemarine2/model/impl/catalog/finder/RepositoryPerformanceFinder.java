/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

import javax.annotation.Nonnull;
import java.util.Optional;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.audio.Performance;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryPerformanceFinder extends RepositoryFinderSupport<Performance, PerformanceFinder>
                                         implements PerformanceFinder
  {
    private static final long serialVersionUID = -5065032203985453428L;

    private static final String QUERY_PERFORMANCES = readSparql(RepositoryPerformanceFinder.class, "Performances.sparql");

    @Nonnull
    private final Optional<Id> trackId;

    @Nonnull
    private final Optional<Id> performerId;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryPerformanceFinder (@Nonnull final Repository repository)
      {
        this(repository, Optional.empty(), Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryPerformanceFinder (@Nonnull final RepositoryPerformanceFinder other,
                                        @Nonnull final Object override)
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
    private RepositoryPerformanceFinder (@Nonnull final Repository repository,
                                         @Nonnull final Optional<Id> trackId,
                                         @Nonnull final Optional<Id> performerId)
      {
        super(repository, "performance");
        this.trackId = trackId;
        this.performerId = performerId;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PerformanceFinder ofTrack (@Nonnull final Id trackId)
      {
        return clonedWith(new RepositoryPerformanceFinder(repository, Optional.of(trackId), performerId));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PerformanceFinder performedBy (@Nonnull final Id performerId)
      {
        return clonedWith(new RepositoryPerformanceFinder(repository, trackId, Optional.of(performerId)));
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
