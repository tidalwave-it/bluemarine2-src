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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Set;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import it.tidalwave.bluemarine2.model.role.EntityWithPath;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * Represents a media item. It is usually associated with one or more files on a filesystem.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface MediaItem extends EntityWithPath, AudioFileSupplier
  {
    /*******************************************************************************************************************
     *
     * A container of metadata objects for a {@link MediaItem}.
     *
     ******************************************************************************************************************/
    public interface Metadata
      {
        public static final Key<Long> FILE_SIZE = new Key<>("file.size");

        public static final Key<Duration> DURATION = new Key<>("mp3.duration");
        public static final Key<Integer> BIT_RATE = new Key<>("mp3.bitRate");
        public static final Key<Integer> SAMPLE_RATE = new Key<>("mp3.sampleRate");
        public static final Key<String> ARTIST = new Key<>("mp3.artist");
        public static final Key<String> COMPOSER = new Key<>("mp3.composer");
        public static final Key<String> PUBLISHER = new Key<>("mp3.publisher");
        public static final Key<String> TITLE = new Key<>("mp3.title");
        public static final Key<Integer> YEAR = new Key<>("mp3.year");
        public static final Key<String> ALBUM = new Key<>("mp3.album");
        public static final Key<Integer> TRACK_NUMBER = new Key<>("mp3.trackNumber");
        public static final Key<Integer> DISK_NUMBER = new Key<>("mp3.diskNumber");
        public static final Key<Integer> DISK_COUNT = new Key<>("mp3.diskCount");
        public static final Key<List<String>> COMMENT = new Key<>("mp3.comment");
        public static final Key<Integer> BITS_PER_SAMPLE = new Key<>("mp3.bitsPerSample");
        public static final Key<String> FORMAT = new Key<>("mp3.format");
        public static final Key<String> ENCODING_TYPE = new Key<>("mp3.encodingType");
        public static final Key<Integer> CHANNELS = new Key<>("mp3.channels");

        public static final Key<Id> MBZ_TRACK_ID = new Key<>("mbz.trackId");
        public static final Key<Id> MBZ_WORK_ID = new Key<>("mbz.workId");
        public static final Key<Id> MBZ_DISC_ID = new Key<>("mbz.discId");
        public static final Key<List<Id>> MBZ_ARTIST_ID = new Key<>("mbz.artistId");

        public final Key<List<String>> ENCODER = new Key<>("tag.ENCODER"); // FIXME: key name

        public static final Key<ITunesComment> ITUNES_COMMENT = new Key<>("iTunes.comment");

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        @Immutable @AllArgsConstructor(access = PRIVATE) @Getter @ToString @EqualsAndHashCode
        public static class ITunesComment
          {
            @Nonnull
            private final String cddb1;

            @Nonnull
            private final String cddbTrackNumber;

            @Nonnull
            public String getTrackId()
              {
                return cddb1 + "/" + cddbTrackNumber;
              }

            @Nonnull
            public static Optional<ITunesComment> from (final @Nonnull Metadata metadata)
              {
                return metadata.get(ENCODER).flatMap(
                        encoders -> encoders.stream().anyMatch(encoder -> encoder.startsWith("iTunes"))
                                                        ? metadata.get(COMMENT).flatMap(comments -> from(comments))
                                                        : Optional.empty());
              }

            @Nonnull
            private static Optional<ITunesComment> from (final @Nonnull List <String> comments)
              {
                return comments.get(comments.size() - 2).contains("+")
                        ? Optional.of(new ITunesComment(comments.get(3), comments.get(4)))
                        : Optional.empty();
              }
          }

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> Optional<T> get (@Nonnull Key<T> key);

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> T getAll (@Nonnull Key<T> key);

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        public boolean containsKey (@Nonnull Key<?> key);

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        @Nonnull
        public Set<Key<?>> getKeys();

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        @Nonnull
        public Set<Map.Entry<Key<?>, ?>> getEntries();

        /***************************************************************************************************************
         *
         * Returns a clone of this object with an additional value.
         *
         * @para        <T>     the value type
         * @param       key     the key
         * @param       value   the value
         * @return              the clone
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> Metadata with (@Nonnull Key<T> key, T value);

        /***************************************************************************************************************
         *
         * Returns a clone of this object with an additional optional value.
         *
         * @para        <T>     the value type
         * @param       key     the key
         * @param       value   the value
         * @return              the clone
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> Metadata with (@Nonnull Key<T> key, Optional<T> value);
    }

    /*******************************************************************************************************************
     *
     * Returns the {@link Metadata} associated with this object.
     *
     * @return  the metadata
     *
     ******************************************************************************************************************/
    @Nonnull
    public Metadata getMetadata();
  }
