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
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import it.tidalwave.bluemarine2.model.role.Parentable;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;

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
public interface MediaItem extends Entity, Parentable<EntityWithPath>, AudioFileSupplier
  {
    /*******************************************************************************************************************
     *
     * A container of metadata objects for a {@link MediaItem}.
     *
     ******************************************************************************************************************/
    public interface Metadata
      {
        public static final Key<Duration> DURATION = new Key<>("mp3.duration");
        public static final Key<Integer> BIT_RATE = new Key<>("mp3.bitRate");
        public static final Key<Integer> SAMPLE_RATE = new Key<>("mp3.sampleRate");
        public static final Key<String> ARTIST = new Key<>("mp3.artist");
        public static final Key<String> COMPOSER = new Key<>("mp3.composer");
        public static final Key<String> PUBLISHER = new Key<>("mp3.publisher");
        public static final Key<String> TITLE = new Key<>("mp3.title");
        public static final Key<Integer> YEAR = new Key<>("mp3.year");
        public static final Key<String> ALBUM = new Key<>("mp3.album");
        public static final Key<Integer> TRACK = new Key<>("mp3.track");
        public static final Key<Integer> DISK_NUMBER = new Key<>("mp3.diskNumber");
        public static final Key<Integer> DISK_COUNT = new Key<>("mp3.diskCount");
        public static final Key<String> COMMENT = new Key<>("mp3.comment");

        public static final Key<Id> MBZ_TRACK_ID = new Key<>("mbz.trackId");
        public static final Key<Id> MBZ_WORK_ID = new Key<>("mbz.workId");
        public static final Key<Id> MBZ_DISC_ID = new Key<>("mbz.discId");
        public static final Key<List<Id>> MBZ_ARTIST_ID = new Key<>("mbz.artistId");

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
    }

    /*******************************************************************************************************************
     *
     * Returns the {@link Path} associated with this object.
     *
     * @return  the path
     *
     ******************************************************************************************************************/
    @Nonnull
    public Path getPath();

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
