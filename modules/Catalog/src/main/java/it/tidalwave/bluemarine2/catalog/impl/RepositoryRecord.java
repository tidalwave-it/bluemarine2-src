/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.catalog.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.openrdf.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.catalog.impl.finder.RepositoryTrackEntityFinder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An implementation of {@link Record} that is mapped to a {@link Repository}.
 * 
 * @stereotype  Datum
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @Slf4j
public class RepositoryRecord extends RepositoryEntitySupport implements Record
  {
    public RepositoryRecord (final @Nonnull Repository repository, 
                                  final @Nonnull Id id, 
                                  final @Nonnull String rdfsLabel)
      {
        super(repository, id);
        this.rdfsLabel = rdfsLabel;
      }
   
    @Override @Nonnull
    public TrackFinder findTracks() 
      {
        return new RepositoryTrackEntityFinder(repository).inRecord(this);
      }

    @Override @Nonnull
    public String toString() 
      {
        return String.format("RepositoryRecordEntity(rdfs:label=%s, %s)",
                             rdfsLabel, id);
      }
  }
