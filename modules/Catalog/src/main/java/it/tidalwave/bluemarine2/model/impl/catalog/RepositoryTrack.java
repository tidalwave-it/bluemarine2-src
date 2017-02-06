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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.util.Optional;
import java.nio.file.Path;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.BindingSet;
import it.tidalwave.util.Id;
import it.tidalwave.util.Memoize;
import it.tidalwave.bluemarine2.util.Formatters;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.Performance;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import it.tidalwave.bluemarine2.model.spi.MetadataSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

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
@Immutable @Slf4j
public class RepositoryTrack extends RepositoryEntitySupport implements Track, AudioFileSupplier
  {
    @Getter @Nonnull
    private final Optional<Integer> diskNumber; // really property of Record, but handy here

    @Getter @Nonnull
    private final Optional<Integer> diskCount; // really property of Record, but handy here

    @Getter @Nonnull
    private final Optional<Integer> trackNumber;

    @Getter @Nonnull
    private final Optional<Duration> duration;

    @Getter @Nonnull
    private final Metadata metadata;

    @Nonnull
    private final Path audioFilePath;

    @Nonnull
    private final Memoize<AudioFile> audioFile;

    private final Memoize<Optional<Record>> record = new Memoize<>();

    private final Memoize<Optional<Performance>> performance = new Memoize<>();

    public RepositoryTrack (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "track");

        audioFilePath = toPath(bindingSet.getBinding("path"));
        duration      = toDuration(bindingSet.getBinding("duration"));
        trackNumber   = toInteger(bindingSet.getBinding("track_number"));
        diskNumber    = toInteger(bindingSet.getBinding("disk_number"));
        diskCount     = toInteger(bindingSet.getBinding("disk_count"));
//        this.recordRdfsLabel = toString(bindingSet.getBinding("record_label"));

        metadata = new MetadataSupport(audioFilePath).with(DURATION, duration)
                                                     .with(TRACK_NUMBER, trackNumber)
                                                     .with(DISK_NUMBER, diskNumber)
                                                     .with(DISK_COUNT, diskCount);

        audioFile = new Memoize<>(() -> new RepositoryAudioFile(repository, bindingSet));
      }

    @Override @Nonnull
    public Optional<Record> getRecord()
      {
        return record.get(() -> _findRecords().recordOf(this).optionalFirstResult());
      }

    @Override @Nonnull
    public Optional<Performance> getPerformance()
      {
        return performance.get(() -> _findPerformances().ofTrack(this).optionalFirstResult());
      }

    @Override @Nonnull
    public synchronized AudioFile getAudioFile()
      {
        return audioFile.get();
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryTrack(%02d/%02d %02d, %s, rdfs:label=%s, %s, %s)",
                             diskNumber.orElse(1), diskCount.orElse(1), trackNumber.orElse(1),
                             duration.map(Formatters::format).orElse("??:??"), rdfsLabel, audioFilePath, id);
      }

    @Override @Nonnull
    public String toDumpString()
      {
        return String.format("%02d/%02d %02d %s %s (%s) %s - %s",
                             diskNumber.orElse(1), diskCount.orElse(1), trackNumber.orElse(1),
                             duration.map(Formatters::format).orElse("??:??"), rdfsLabel, id, audioFilePath,
                             source.orElse(new Id("unknown")));
      }
  }
