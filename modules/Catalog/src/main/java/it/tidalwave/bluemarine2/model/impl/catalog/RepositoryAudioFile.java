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
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.spi.MetadataSupport;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.vocabulary.BM;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import it.tidalwave.bluemarine2.util.Formatters;
import java.nio.file.Paths;
import org.eclipse.rdf4j.query.BindingSet;

/***********************************************************************************************************************
 *
 * An implementation of {@link AudioFile} that is mapped to a {@link Repository}.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @EqualsAndHashCode(of = { "path", "trackId" }, callSuper = false)
public class RepositoryAudioFile extends RepositoryEntitySupport implements AudioFile
  {
    @Getter @Nonnull
    private final Path path;

    @Getter @Nonnull
    private final Path relativePath;

    @Nonnull
    private final Id trackId;

    @Nonnull
    private final Optional<Duration> duration;

    @Nonnull
    private final Optional<Long> fileSize;

    @Getter @Nonnull
    private final Metadata metadata;

    public RepositoryAudioFile (final @Nonnull Repository repository,
                                final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "audioFile");

        // See BMT-36
        this.path         = Paths.get(toString(bindingSet.getBinding("path")).get());
        this.relativePath = path;// FIXME basePath.relativize(path);

        this.duration     = toDuration(bindingSet.getBinding("duration"));
        this.fileSize     = toLong(bindingSet.getBinding("fileSize"));

        this.trackId = null; // FIXME

        this.metadata = new MetadataSupport(path).with(TITLE, rdfsLabel)
                                                 .with(DURATION, duration)
                                                 .with(FILE_SIZE, fileSize);
      }

    // FIXME: too maby arguments, pass a BindingSet
    public RepositoryAudioFile (final @Nonnull Repository repository,
                                final @Nonnull Id id,
                                final @Nonnull Id trackId,
                                final @Nonnull Path path,
                                final @Nonnull Path basePath,
                                final @Nonnull Optional<Duration> duration,
                                final String rdfsLabel,
                                final @Nonnull Optional<Long> fileSize)
      {
        super(repository, id, rdfsLabel, Optional.of(BM.ID_SOURCE_EMBEDDED), Optional.of(BM.ID_SOURCE_EMBEDDED));
        this.trackId = trackId;
        // See BMT-36
        this.path = path;
        this.relativePath = path;// FIXME basePath.relativize(path);

        this.duration = duration;
        this.fileSize = fileSize;

        this.metadata = new MetadataSupport(path).with(TITLE, rdfsLabel)
                                                 .with(DURATION, duration)
                                                 .with(FILE_SIZE, fileSize);
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryAudioFileEntity(%s, %s)", relativePath, id);
      }

    @Override @Nonnull
    public AudioFile getAudioFile()
      {
        return this;
      }

    @Override @Nonnull
    public MusicArtistFinder findMakers()
      {
        return new RepositoryMusicArtistFinder(repository).makerOf(trackId);
      }

    @Override @Nonnull
    public MusicArtistFinder findComposers()
      {
        return new RepositoryMusicArtistFinder(repository).makerOf(trackId);
//        return new RepositoryAudioFileArtistFinder(this); FIXME
      }

    @Override @Nonnull
    public Optional<Record> getRecord()
      {
        return new RepositoryRecordFinder(repository).recordOf(id).optionalFirstResult();
      }

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent() // FIXME: drop it
      {
        throw new UnsupportedOperationException();
      }

    @Override @Nonnull
    public String toDumpString()
      {
        return String.format("%s %8s %s %s", duration.map(Formatters::format).orElse("??:??"),
                                             fileSize.map(l -> l.toString()).orElse(""),
                                             id, relativePath);
      }
  }
