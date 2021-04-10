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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import it.tidalwave.util.spi.FinderSupport;
import it.tidalwave.util.spi.PriorityAsSupport;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.finder.audio.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.audio.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.audio.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.spi.NamedEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import static java.util.Collections.emptyList;
import static it.tidalwave.role.ui.Displayable._Displayable_;

/***********************************************************************************************************************
 *
 * The default implementation of {@link AudioFile}. It basically does nothing, it just acts as an aggregator of roles.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable
public class FileSystemAudioFile implements AudioFile, PathAwareEntity
  {
    /*******************************************************************************************************************
     *
     * Minimal implementation of {@link MusicArtist} without search capabilities.
     *
     ******************************************************************************************************************/
    static class ArtistFallback extends NamedEntity implements MusicArtist
      {
        public ArtistFallback (@Nonnull final String displayName)
          {
            super(displayName);
          }

        @Override @Nonnull
        public TrackFinder findTracks()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public RecordFinder findRecords()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public PerformanceFinder findPerformances()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override
        public int getType()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public Id getId()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public Optional<Id> getSource()
          {
            return Optional.of(Id.of("embedded")); // FIXME
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor
    static class ArtistFallbackFinder extends FinderSupport<MusicArtist, MusicArtistFinder> implements MusicArtistFinder
      {
        private static final long serialVersionUID = 7969726066626602758L;

        @Nonnull
        private final Metadata metadata;

        @Nonnull
        private final Key<String> metadataKey;

        public ArtistFallbackFinder (@Nonnull final ArtistFallbackFinder other, @Nonnull final Object override)
          {
            super(other, override);
            final ArtistFallbackFinder source = getSource(ArtistFallbackFinder.class, other, override);
            this.metadata = source.metadata;
            this.metadataKey = source.metadataKey;
          }

        @Override @Nonnull
        protected List<? extends MusicArtist> computeNeededResults()
          {
            return metadata.get(metadataKey).map(artistName -> List.of(new ArtistFallback(artistName))).orElse(emptyList());
          }

        @Override @Nonnull
        public MusicArtistFinder withId (@Nonnull final Id id)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public MusicArtistFinder makerOf (@Nonnull final Id entityId)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public MusicArtistFinder importedFrom (@Nonnull final Optional<Id> optionalSource)
          {
            return optionalSource.map(this::importedFrom).orElse(this);
          }

        @Override @Nonnull
        public MusicArtistFinder importedFrom (@Nonnull final Id source)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public MusicArtistFinder withFallback (@Nonnull final Optional<Id> optionalFallback)
          {
            return optionalFallback.map(this::withFallback).orElse(this);
          }

        @Override @Nonnull
        public MusicArtistFinder withFallback (@Nonnull final Id fallback)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }
      }

    @Getter @Nonnull
    private final Path path;

    @Getter @Nonnull
    private final Path relativePath;

    @Nonnull
    private final PathAwareEntity parent;

    @Nullable
    private Metadata metadata;

    @Delegate
    private final PriorityAsSupport asSupport = new PriorityAsSupport(this);

    public FileSystemAudioFile (@Nonnull final Path path,
                                @Nonnull final PathAwareEntity parent,
                                @Nonnull final Path basePath)
      {
        this.path = path;
        this.parent = parent;
        this.relativePath = basePath.relativize(path);
      }

    @Override @Nonnull
    public Id getId()
      {
        return Id.of(path.toString());
      }

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.of(parent);
      }


    @Override @Nonnull
    public Optional<Resource> getContent()
      throws IOException
      {
        return Files.exists(path) ? Optional.of(new FileSystemResource(path.toFile())) : Optional.empty();
      }

    @Override @Nonnegative
    public long getSize()
      throws IOException
      {
        return Files.size(path);
      }

    @Override @Nonnull
    public synchronized Metadata getMetadata()
      {
        if (metadata == null)
          {
            metadata = AudioMetadataFactory.loadFrom(path);
          }

        return metadata;
      }

    @Override @Nonnull
    public MusicArtistFinder findComposers()
      {
        // FIXME: when present, should use a Repository finder
        return new ArtistFallbackFinder(getMetadata(), Metadata.COMPOSER);
      }

    @Override @Nonnull
    public MusicArtistFinder findMakers()
      {
        // FIXME: when present, should use a Repository finder
        return new ArtistFallbackFinder(getMetadata(), Metadata.ARTIST);
      }

    @Override @Nonnull
    public AudioFile getAudioFile()
      {
        return this;
      }

    @Override @Nonnull
    public Optional<Record> getRecord()
      {
            // FIXME: check - parent should be always present - correct?
        return getParent().map(parent -> new NamedRecord(parent.as(_Displayable_).getDisplayName()));
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("FileSystemAudioFile(%s)", relativePath);
      }
  }
