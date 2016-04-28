/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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

import it.tidalwave.bluemarine2.catalog.impl.finder.RepositoryRecordFinder;
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
import it.tidalwave.bluemarine2.util.Formatters;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import it.tidalwave.bluemarine2.model.spi.EntityWithPathDecorator;
import java.util.Optional;
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

    @Nonnull
    private final Optional<Integer> diskNumber;

    @Nonnull
    private final Optional<Integer> diskCount;

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
        this.diskNumber = toOptionalInteger(bindingSet.getBinding("disk_number"));
        this.diskCount = toOptionalInteger(bindingSet.getBinding("disk_count"));
//        this.recordRdfsLabel = toString(bindingSet.getBinding("record_label"));
//        this.trackCount = toInteger(bindingSet.getBinding("track_number")));
      }

    @Override @Nonnull
    public Optional<Record> getRecord()
      {
        return new RepositoryRecordFinder(repository).recordOf(id).optionalFirstResult();
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
                                                new EntityWithPathDecorator(this, Paths.get("record")),
                                                audioFilePath,
                                                duration,
                                                rdfsLabel);
          }

        return audioFile;
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryTrack(%02d/%02d %02d, %s, rdfs:label=%s, %s, %s)",
                             diskNumber.orElse(1), diskCount.orElse(1),
                             trackNumber, Formatters.format(duration), rdfsLabel, audioFilePath, id);
      }
  }
