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
import java.time.Duration;
import java.nio.file.Path;
import org.openrdf.repository.Repository;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.catalog.Track;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @ToString
@Slf4j
public class RepositoryTrackEntity extends RepositoryEntitySupport implements Track
  {
    private final Integer trackNumber;
    
    @Nonnull
    private final String rdfsLabel;
    
    @Nonnull
    private final Duration duration;
    
//    private final String recordRdfsLabel;
//    
//    private final Integer trackCount;
    
    @Nonnull
    private final Path audioFile;

    public RepositoryTrackEntity (final @Nonnull Repository repository, 
                        final @Nonnull Id id, 
                        final @Nonnull Path audioFile,
                        final @Nonnull String rdfsLabel,
                        final @Nonnull Duration duration,
                        final @Nonnull Integer trackNumber,
                        final @Nonnull String recordRdfsLabel,
                        final @Nonnull Integer trackCount)
      {
        super(repository, id);
        this.audioFile = audioFile;
        this.rdfsLabel = rdfsLabel;
        this.duration = duration;
        this.trackNumber = trackNumber;
//        this.recordRdfsLabel = recordRdfsLabel;
//        this.trackCount = trackCount;
      }
  }
