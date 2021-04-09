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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.nio.file.Path;
import it.tidalwave.util.Key;
import it.tidalwave.bluemarine2.model.MediaItem;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString(of = "properties") @Slf4j
public class MetadataSupport implements MediaItem.Metadata
  {
    @Getter @Nonnull
    protected final Path path;

    @Nonnull
    protected Optional<Function<Key<?>, MediaItem.Metadata>> fallback;

    protected final Map<Key<?>, Object> properties = new HashMap<>();

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public MetadataSupport (@Nonnull final Path path)
      {
        this(path, Optional.empty());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Optional<T> get (@Nonnull final Key<T> key)
      {
        return properties.containsKey(key) ? Optional.ofNullable((T)properties.get(key))
                                           : fallback.flatMap(fb -> fb.apply(key).get(key));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> T getAll (@Nonnull final Key<T> key)
      {
        final T list = (T)properties.get(key);
        return (list != null) ? list :
                fallback.flatMap(fb -> fb.apply(key).get(key)).orElse((T)Collections.emptyList());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public boolean containsKey (@Nonnull final Key<?> key)
      {
        return properties.containsKey(key);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Set<Key<?>> getKeys()
      {
        return Collections.unmodifiableSet(properties.keySet());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Set<Map.Entry<Key<?>, ?>> getEntries()
      {
        return Collections.unmodifiableSet(properties.entrySet());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> MediaItem.Metadata with (@Nonnull final Key<T> key, @Nonnull final T value)
      {
        final MetadataSupport clone = new MetadataSupport(path, fallback);
        clone.properties.putAll(this.properties);
        clone.put(key, value);

        if (value instanceof ITunesComment)
          {
            clone.put(CDDB, ((ITunesComment) value).getCddb());
          }

        return clone;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public MediaItem.Metadata withFallback (@Nonnull final Function<Key<?>, MediaItem.Metadata> fallback)
      {
        final MetadataSupport clone = new MetadataSupport(path, Optional.of(fallback));
        clone.properties.putAll(this.properties);
        return clone;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> MediaItem.Metadata with (@Nonnull final Key<T> key, @Nonnull final Optional<T> value)
      {
        return value.map(t -> with(key, t)).orElse(this);
      }

    /*******************************************************************************************************************
     *
     * FIXME: remove this, make it truly immutable.
     *
     ******************************************************************************************************************/
    protected <V> void put (@Nonnull final Key<V> key, @Nullable final V value)
      {
        if (value != null)
          {
            properties.put(key, value);
          }
      }
  }