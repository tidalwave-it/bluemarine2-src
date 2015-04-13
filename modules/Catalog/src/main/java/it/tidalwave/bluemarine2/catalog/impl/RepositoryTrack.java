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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.time.Duration;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Configurable;
import org.openrdf.repository.Repository;
import org.openrdf.query.BindingSet;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An implementation of {@link Track} that is mapped to a {@link Repository}.
 * 
 * @stereotype  Datum
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Configurable @Getter @Slf4j
public class RepositoryTrack extends RepositoryEntitySupport implements Track, AudioFileSupplier
  {
    private final Integer trackNumber;
    
    @Nonnull
    private final Duration duration;
    
    @Nonnull
    private final Path audioFilePath;
//    private final String recordRdfsLabel;
//    
//    private final Integer trackCount;
    
    @CheckForNull
    private AudioFile audioFile;
    
    @Inject
    private MediaFileSystem fileSystem;
    
    public RepositoryTrack (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "track");
        this.audioFilePath = Paths.get(toString(bindingSet.getBinding("path")));
        this.duration = toDuration(bindingSet.getBinding("duration"));
        this.trackNumber = toInteger(bindingSet.getBinding("track_number"));
//        this.recordRdfsLabel = toString(bindingSet.getBinding("record_label"));
//        this.trackCount = toInteger(bindingSet.getBinding("track_number")));
      }
    
    @Override @Nonnull
    public synchronized AudioFile getAudioFile()
      {
        if (audioFile == null)
          {
            audioFile = new RepositoryAudioFile(repository, 
                                                id, // FIXME: this should really be the AudioFileId
                                                id,
                                                fileSystem.getRootPath().resolve(audioFilePath),
                                                this,
                                                audioFilePath,
                                                duration,   
                                                rdfsLabel);
          }
        
        return audioFile;
      }

    @Override @Nonnull
    public String toString() 
      {
        return String.format("RepositoryTrack(%02d, %s, rdfs:label=%s, %s, %s)",
                             trackNumber, duration, rdfsLabel, audioFilePath, id);
      }
  }
