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
package it.tidalwave.bluemarine2.catalog;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import it.tidalwave.util.Id;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @ToString
@Slf4j
public class MusicArtistEntity extends EntitySupport
  {
    private final String rdfsLabel;
    
    private final String foafName;
    // https://bitbucket.org/openrdf/alibaba/src/master/object-repository/

    public MusicArtistEntity (final @Nonnull Repository repository,
                              final @Nonnull Id id, 
                              final @Nonnull String rdfsLabel, 
                              final @Nonnull String foafName)
      {
        super(repository, id);
        this.rdfsLabel = rdfsLabel;
        this.foafName = foafName;
      }
    
    @Nonnull
    public TrackEntityFinder findTracks() 
      {
        return new RepositoryTrackEntityFinder(repository);
      }
  }
