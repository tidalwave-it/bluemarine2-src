/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
 *
 **********************************************************************************************************************/
public interface MediaItem extends PathAwareEntity, AudioFileSupplier
  {
    /*******************************************************************************************************************
     *
     * A container of metadata objects for a {@link MediaItem}.
     *
     ******************************************************************************************************************/
    public interface Metadata
      {
        public static final Key<Long> FILE_SIZE = Key.of("file.size", Long.class);

        public static final Key<Duration> DURATION = Key.of("mp3.duration", Duration.class);
        public static final Key<Integer> BIT_RATE = Key.of("mp3.bitRate", Integer.class);
        public static final Key<Integer> SAMPLE_RATE = Key.of("mp3.sampleRate", Integer.class);
        public static final Key<String> ARTIST = Key.of("mp3.artist", String.class);
        public static final Key<String> COMPOSER = Key.of("mp3.composer", String.class);
        public static final Key<String> PUBLISHER = Key.of("mp3.publisher", String.class);
        public static final Key<String> TITLE = Key.of("mp3.title", String.class);
        public static final Key<Integer> YEAR = Key.of("mp3.year", Integer.class);
        public static final Key<String> ALBUM = Key.of("mp3.album", String.class);
        public static final Key<Integer> TRACK_NUMBER = Key.of("mp3.trackNumber", Integer.class);
        public static final Key<Integer> DISK_NUMBER = Key.of("mp3.diskNumber", Integer.class);
        public static final Key<Integer> DISK_COUNT = Key.of("mp3.diskCount", Integer.class);
        public static final Key<List<String>> COMMENT = new Key<>("mp3.comment") {};
        public static final Key<Integer> BITS_PER_SAMPLE = Key.of("mp3.bitsPerSample", Integer.class);
        public static final Key<String> FORMAT = Key.of("mp3.format", String.class);
        public static final Key<String> ENCODING_TYPE = Key.of("mp3.encodingType", String.class);
        public static final Key<Integer> CHANNELS = Key.of("mp3.channels", Integer.class);

        public static final Key<List<byte[]>> ARTWORK = new Key<>("mp3.artwork") {};

        public static final Key<Id> MBZ_TRACK_ID = Key.of("mbz.trackId", Id.class);
        public static final Key<Id> MBZ_WORK_ID = Key.of("mbz.workId", Id.class);
        public static final Key<Id> MBZ_DISC_ID = Key.of("mbz.discId", Id.class);
        public static final Key<List<Id>> MBZ_ARTIST_ID = new Key<>("mbz.artistId") {};

        public final Key<List<String>> ENCODER = new Key<>("tag.ENCODER") {}; // FIXME: key name

        public static final Key<ITunesComment> ITUNES_COMMENT = Key.of("iTunes.comment", ITunesComment.class);
        public static final Key<Cddb> CDDB = Key.of("cddb", Cddb.class);

        /***************************************************************************************************************
         *
         * The CDDB item.
         *
         **************************************************************************************************************/
        @Immutable @AllArgsConstructor(access = PRIVATE) @Getter @Builder @ToString @EqualsAndHashCode
        public static class Cddb
          {
            @Nonnull
            private final String discId;

            @Nonnull
            private final int[] trackFrameOffsets;

            private final int discLength;

            /***********************************************************************************************************
             *
             * Returns the TOC (Table Of Contents) of this CDDB in string form (e.g. {@code 1+3+4506+150+3400+4000})
             *
             * @return  the TOC
             *
             **********************************************************************************************************/
            @Nonnull
            public String getToc()
              {
                return String.format("1+%d+%d+%s", trackFrameOffsets.length, discLength,
                                                   Arrays.toString(trackFrameOffsets).replace(", ", "+").replace("[", "").replace("]", ""));
              }

            /***********************************************************************************************************
             *
             * Returns the number of tracks in the TOC
             *
             * @return  the number of tracks
             *
             **********************************************************************************************************/
            @Nonnegative
            public int getTrackCount()
              {
                return trackFrameOffsets.length;
              }

            /***********************************************************************************************************
             *
             * Returns {@code true} if this object matches the other CDDB within a given threshold.
             *
             * @param   other       the other CDDB
             * @param   threshold   the threshold of the comparison
             * @return              {@code true} if this object matches
             *
             **********************************************************************************************************/
            public boolean matches (final @Nonnull Cddb other, final @Nonnegative int threshold)
              {
                if (Arrays.equals(this.trackFrameOffsets, other.trackFrameOffsets))
                  {
                    return true;
                  }

                if (!this.sameTrackCountOf(other))
                  {
                    return false;
                  }

                return this.computeDifference(other) <= threshold;
              }

            /***********************************************************************************************************
             *
             * Returns {@code true} if this object contains the same number of tracks of the other CDDB
             *
             * @param   other       the other CDDB
             * @return              {@code true} if the number of tracks matches
             *
             **********************************************************************************************************/
            public boolean sameTrackCountOf (final @Nonnull Cddb other)
              {
                return this.trackFrameOffsets.length == other.trackFrameOffsets.length;
              }

            /***********************************************************************************************************
             *
             * Computes the difference to another CDDB.
             *
             * @param   other       the other CDDB
             * @return              the difference
             *
             **********************************************************************************************************/
            public int computeDifference (final @Nonnull Cddb other)
              {
                final int delta = this.trackFrameOffsets[0] - other.trackFrameOffsets[0];
                double acc = 0;

                for (int i = 1; i < this.trackFrameOffsets.length; i++)
                  {
                    final double x = (this.trackFrameOffsets[i] - other.trackFrameOffsets[i] - delta)
                                            / (double)other.trackFrameOffsets[i];
                    acc += x * x;
                  }

                return (int)Math.round(acc * 1E6);
              }
          }

        /***************************************************************************************************************
         *
         *
         *
         **************************************************************************************************************/
        @Immutable @AllArgsConstructor(access = PRIVATE) @Getter @ToString @EqualsAndHashCode
        public static class ITunesComment
          {
            private static final Pattern PATTERN_TO_STRING = Pattern.compile(
                    "MediaItem.Metadata.ITunesComment\\(cddb1=([^,]*), cddbTrackNumber=([0-9]+)\\)");

            @Nonnull
            private final String cddb1;

            @Nonnull
            private final String cddbTrackNumber;

            /***********************************************************************************************************
             *
             * Returns an unique track id out of the data in this object.
             *
             * @return              the track id
             *
             **********************************************************************************************************/
            @Nonnull
            public String getTrackId()
              {
                return cddb1 + "/" + cddbTrackNumber;
              }

            /***********************************************************************************************************
             *
             * Returns the same data in form of a CDDB.
             *
             * @return              the CDDB
             *
             **********************************************************************************************************/
            @Nonnull
            public Cddb getCddb()
              {
                return Cddb.builder().discId(cddb1.split("\\+")[0])
                                     .discLength(Integer.parseInt(cddb1.split("\\+")[1]))
                                     .trackFrameOffsets(Stream.of(cddb1.split("\\+"))
                                                              .skip(3)
                                                              .mapToInt(Integer::parseInt)
                                                              .toArray())
                                     .build();
              }

            /***********************************************************************************************************
             *
             * Factory method extracting data from a {@link Metadata} instance.
             *
             * @param   metadata    the data source
             * @return              the {@code ITunesComment}
             *
             **********************************************************************************************************/
            @Nonnull
            public static Optional<ITunesComment> from (final @Nonnull Metadata metadata)
              {
                return metadata.get(ENCODER).flatMap(
                        encoders -> encoders.stream().anyMatch(encoder -> encoder.startsWith("iTunes"))
                                                        ? metadata.get(COMMENT).flatMap(ITunesComment::from)
                                                        : Optional.empty());
              }

            /***********************************************************************************************************
             *
             * Factory method extracting data from a string representation.
             *
             * @param   string      the string source
             * @return              the {@code ITunesComment}
             *
             **********************************************************************************************************/
            @Nonnull
            public static ITunesComment fromToString (final @Nonnull String string)
              {
                final Matcher matcher = PATTERN_TO_STRING.matcher(string);

                if (!matcher.matches())
                  {
                    throw new IllegalArgumentException("Invalid string: " + string);
                  }

                return new ITunesComment(matcher.group(1), matcher.group(2));
              }

            /***********************************************************************************************************
             *
             * Factory method extracting data from a string representation as in the iTunes Comment MP3 tag.
             *
             * @param   comments    the source
             * @return              the {@code ITunesComment}
             *
             **********************************************************************************************************/
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
         * Extracts a single metadata item associated to the given key.
         *
         * @param       <T>     the type of the item
         * @param       key     the key
         * @return              the item
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> Optional<T> get (@Nonnull Key<T> key);

        /***************************************************************************************************************
         *
         * Extracts a metadata item (typically a collection) associated to the given key.
         *
         * @param       <T>     the type of the item
         * @param       key     the key
         * @return              the item
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> T getAll (@Nonnull Key<T> key);

        /***************************************************************************************************************
         *
         * Returns {@code true} if an item with the given key is present.
         *
         * @param       key     the key
         * @return              {@code true} if found
         *
         **************************************************************************************************************/
        public boolean containsKey (@Nonnull Key<?> key);

        /***************************************************************************************************************
         *
         * Returns all the keys contained in this instance.
         *
         * @return              all the keys
         *
         **************************************************************************************************************/
        @Nonnull
        public Set<Key<?>> getKeys();

        /***************************************************************************************************************
         *
         * Returns all the entries (key -> value) contained in this instance.
         *
         * @return              all the entries
         *
         **************************************************************************************************************/
        @Nonnull
        public Set<Map.Entry<Key<?>, ?>> getEntries();

        /***************************************************************************************************************
         *
         * Returns a clone of this object with an additional item.
         *
         * @param       <T>     the type of the item
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
         * @param       <T>     the type of the item
         * @param       key     the key
         * @param       value   the value
         * @return              the clone
         *
         **************************************************************************************************************/
        @Nonnull
        public <T> Metadata with (@Nonnull Key<T> key, Optional<T> value);

        /***************************************************************************************************************
         *
         * Returns a clone of this object with a fallback data source; when an item is searched and not found, before
         * giving up it will be searched in the given fallback.
         *
         * @param       fallback    the fallback
         * @return                  the clone
         *
         **************************************************************************************************************/
        @Nonnull
        public Metadata withFallback (@Nonnull Function<Key<?>, Metadata> fallback);
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
