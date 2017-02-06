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
import javax.inject.Inject;
import java.time.Duration;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.Repository;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Id;
import it.tidalwave.util.Memoize;
import it.tidalwave.bluemarine2.util.Formatters;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.spi.MetadataSupport;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.vocabulary.BM;
import it.tidalwave.bluemarine2.model.impl.AudioMetadataFactory;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryMusicArtistFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordFinder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

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
@Immutable @Configurable @EqualsAndHashCode(of = { "path", "trackId" }, callSuper = false)
@Slf4j
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

    @Inject
    private MediaFileSystem fileSystem;

    private final Memoize<Metadata> fallbackMetadata = new Memoize<>();

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
                                                 .with(FILE_SIZE, fileSize)
                                                 .withFallback(key -> fallbackMetadata.get(this::loadFallbackMetadata));
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
                                                 .with(FILE_SIZE, fileSize)
                                                 .withFallback(key -> fallbackMetadata.get(this::loadFallbackMetadata));
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
    public Optional<byte[]> getContent()
      throws IOException
      {
        final Path absolutePath = getAbsolutePath();
        return Files.exists(absolutePath) ? Optional.of(Files.readAllBytes(absolutePath)) : Optional.empty();
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

    @Nonnull
    private Metadata loadFallbackMetadata()
      {
        final Path absolutePath = getAbsolutePath();
        return Files.exists(absolutePath) ? AudioMetadataFactory.loadFrom(absolutePath) : new MetadataSupport(path);
      }

    @Nonnull
    private Path getAbsolutePath()
      {
        return fileSystem.getRootPath().resolve(path);
      }
  }
