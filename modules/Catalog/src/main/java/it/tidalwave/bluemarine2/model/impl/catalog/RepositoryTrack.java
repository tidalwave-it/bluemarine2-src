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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Optional;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Configurable;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.BindingSet;
import it.tidalwave.bluemarine2.util.Formatters;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.Performance;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryPerformanceFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import it.tidalwave.bluemarine2.model.spi.MetadataSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.query.Binding;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedToNativeForm;
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
@Immutable @Configurable @Slf4j
public class RepositoryTrack extends RepositoryEntitySupport implements Track, AudioFileSupplier
  {
    private final Optional<Integer> trackNumber;

    @Nonnull
    private final Optional<Duration> duration;

    @Nonnull
    private final Path audioFilePath;

    @Nonnull
    private final Optional<Integer> diskNumber;

    @Nonnull
    private final Optional<Integer> diskCount;

    @CheckForNull
    private AudioFile audioFile;

    @Inject
    private MediaFileSystem fileSystem;

    @Getter
    private final Metadata metadata;

    private final Optional<Long> fileSize;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public RepositoryTrack (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "track");

        this.audioFilePath = fixedPath(bindingSet.getBinding("path"));
        this.duration = toDuration(bindingSet.getBinding("duration"));
        this.trackNumber = toInteger(bindingSet.getBinding("track_number"));
        this.diskNumber = toInteger(bindingSet.getBinding("disk_number"));
        this.diskCount = toInteger(bindingSet.getBinding("disk_count"));
        this.fileSize = toLong(bindingSet.getBinding("fileSize"));
//        this.recordRdfsLabel = toString(bindingSet.getBinding("record_label"));
//        this.trackCount = toInteger(bindingSet.getBinding("track_number")));

        this.metadata = new MetadataSupport(audioFilePath).with(DURATION, duration)
                                                          .with(TRACK_NUMBER, trackNumber)
                                                          .with(DISK_NUMBER, diskNumber)
                                                          .with(DISK_COUNT, diskCount);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Optional<Record> getRecord()
      {
        return new RepositoryRecordFinder(repository).recordOf(id).optionalFirstResult();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Optional<Performance> getPerformance()
      {
        return new RepositoryPerformanceFinder(repository).importedFrom(source).ofTrack(id).optionalFirstResult();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public synchronized AudioFile getAudioFile()
      {
        if (audioFile == null)
          {
            audioFile = new RepositoryAudioFile(repository,
                                                id, // FIXME: this should really be the AudioFileId
                                                id,
                                                fileSystem.getRootPath().resolve(audioFilePath),
                                                audioFilePath,
                                                duration,
                                                rdfsLabel,
                                                fileSize);
          }

        return audioFile;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryTrack(%02d/%02d %02d, %s, rdfs:label=%s, %s, %s)",
                             diskNumber.orElse(1), diskCount.orElse(1), trackNumber.orElse(1),
                             duration.map(Formatters::format).orElse("??:??"), rdfsLabel, audioFilePath, id);
      }

    /*******************************************************************************************************************
     *
     * Tries to fix a path for character normalization issues (see BMT-46). The idea is to first normalize the encoding
     * to the native form. If it doesn't work, a broken path is replaced to avoid further errors (of course, the
     * resource won't be available when requested).
     * It doesn't try to call normalizedPath() because it's expensive.
     *
     * @param   binding     the binding
     * @return              the path
     *
     ******************************************************************************************************************/
    @Nonnull
    private Path fixedPath (final @Nonnull Binding binding)
      {
        try // FIXME: see BMT-46 - try all posibile normalizations
          {
            return Paths.get(normalizedToNativeForm(toString(binding).get()));
          }
        catch (InvalidPathException e)
          {
            // FIXME: perhaps we could try a similar trick to normalizedPath() - the problem being the fact that it
            // currently accepts a Path, but we can't convert to a Path. It should be rewritten to work with a String
            // in input.
            log.error("Invalid path {}", e.toString());
            return Paths.get("broken SEE BMT-46");
          }
      }
  }
