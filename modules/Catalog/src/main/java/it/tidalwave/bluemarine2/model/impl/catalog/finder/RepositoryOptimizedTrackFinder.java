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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Optional;
import org.eclipse.rdf4j.repository.Repository;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * An optimised specialisation of {@link RepositoryTrackFinder} that eventually uses an optional pre-computed value for
 * the track count.
 *
 * @stereotype  Finder
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryOptimizedTrackFinder extends RepositoryTrackFinder
  {
    private static final long serialVersionUID = -4361362680267527615L;

    @Nonnull
    private final Optional<Integer> trackCount;

    public RepositoryOptimizedTrackFinder (@Nonnull final Repository repository,
                                           @Nonnull final Optional<Integer> trackCount)
      {
        super(repository);
        this.trackCount = trackCount;
      }

    public RepositoryOptimizedTrackFinder (@Nonnull final RepositoryOptimizedTrackFinder other,
                                           @Nonnull final Object override)
      {
        super(other, override);
        final RepositoryOptimizedTrackFinder source = getSource(RepositoryOptimizedTrackFinder.class, other, override);
        this.trackCount = source.trackCount;
      }

    @Override @Nonnegative
    public int count()
      {
        // in case other parameters are added, remember to check for them
        return (recordId.isPresent() && makerId.isEmpty()) ? trackCount.orElseGet(super::count) : super.count();
      }
  }
