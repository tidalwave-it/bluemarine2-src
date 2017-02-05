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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.finder.MusicArtistFinder;
import it.tidalwave.bluemarine2.model.finder.PerformanceFinder;
import it.tidalwave.bluemarine2.model.finder.RecordFinder;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.spi.NamedEntity;
import lombok.experimental.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static it.tidalwave.role.Displayable.Displayable;
import java.util.Iterator;

/***********************************************************************************************************************
 *
 * The default implementation of {@link AudioFile}. It basically does nothing, it just acts as an aggregator of roles.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
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
        public ArtistFallback (final @Nonnull String displayName)
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

        @Override @Nonnull
        public int getType()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public Id getId()
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override
        public Optional<Id> getSource()
          {
            return Optional.of(new Id("embedded")); // FIXME
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor
    static class ArtistFallbackFinder extends Finder8Support<MusicArtist, MusicArtistFinder> implements MusicArtistFinder
      {
        private static final long serialVersionUID = 7969726066626602758L;

        @Nonnull
        private final Metadata metadata;

        @Nonnull
        private final Key<String> metadataKey;

        public ArtistFallbackFinder (final @Nonnull ArtistFallbackFinder other, final @Nonnull Object override)
          {
            super(other, override);
            final ArtistFallbackFinder source = getSource(ArtistFallbackFinder.class, other, override);
            this.metadata = source.metadata;
            this.metadataKey = source.metadataKey;
          }

        @Override @Nonnull
        protected List<? extends MusicArtist> computeNeededResults()
          {
            return metadata.get(metadataKey).map(artistName -> asList(new ArtistFallback(artistName))).orElse(emptyList());
          }

        @Override @Nonnull
        public MusicArtistFinder withId (final @Nonnull Id id)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public MusicArtistFinder makerOf (final @Nonnull Id entityId)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public MusicArtistFinder importedFrom (final @Nonnull Optional<Id> optionalSource)
          {
            return optionalSource.map(this::importedFrom).orElse(this);
          }

        @Override @Nonnull
        public MusicArtistFinder importedFrom (final @Nonnull Id source)
          {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME
          }

        @Override @Nonnull
        public MusicArtistFinder withFallback (final @Nonnull Optional<Id> optionalFallback)
          {
            return optionalFallback.map(this::withFallback).orElse(this);
          }

        @Override @Nonnull
        public MusicArtistFinder withFallback (final @Nonnull Id fallback)
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

    @CheckForNull
    private Metadata metadata;

    @Delegate
    private final AsSupport asSupport = new AsSupport(this);

    public FileSystemAudioFile (final @Nonnull Path path,
                                final @Nonnull PathAwareEntity parent,
                                final @Nonnull Path basePath)
      {
        this.path = path;
        this.parent = parent;
        this.relativePath = basePath.relativize(path);
      }

    @Override @Nonnull
    public Id getId()
      {
        return new Id(path.toString());
      }

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.of(parent);
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
        return getParent().map(parent -> new NamedRecord(parent.as(Displayable).getDisplayName()));
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("FileSystemAudioFile(%s)", relativePath);
      }
  }
