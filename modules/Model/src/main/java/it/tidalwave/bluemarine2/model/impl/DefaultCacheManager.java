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

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.lang.ref.SoftReference;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.spi.CacheManager;
import it.tidalwave.bluemarine2.message.PersistenceInitializedNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.Objects.requireNonNull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j @SimpleMessageSubscriber
public class DefaultCacheManager implements CacheManager
  {
    private final Map<Object, Cache> cacheMap = new ConcurrentHashMap<>();

    @RequiredArgsConstructor
    static class DefaultCache implements Cache
      {
        @Nonnull
        private final String name;
        // TODO: use Spring ConcurrentReferenceHashMap?
        private final Map<Object, SoftReference<?>> objectMap = new ConcurrentHashMap<>();

        @Override @Nonnull
        public <T> T getCachedObject (@Nonnull final Object key, @Nonnull final Supplier<T> supplier)
          {
            SoftReference<T> ref = (SoftReference<T>)(objectMap.computeIfAbsent(key, k -> computeObject(k, supplier)));
            T object = ref.get();

            if (object == null)
              {
                objectMap.put(key, ref = computeObject(key, supplier));
                object = requireNonNull(ref.get(), "Cache returned null");
              }

            return object;
          }

        @Nonnull
        private <T> SoftReference<T> computeObject (@Nonnull final Object key, @Nonnull final Supplier<T> supplier)
          {
            log.info(">>>> cache {} miss for {}", name, key);
            return new SoftReference<>(requireNonNull(supplier.get(), "Supplier returned null"));
          }
      }

    @Override @Nonnull
    public Cache getCache (@Nonnull final Object cacheKey)
      {
        return cacheMap.computeIfAbsent(cacheKey, key -> new DefaultCache(cacheKey.toString()));
      }

    /* visible for testing FIXME */ public void onPersistenceUpdated (@ListensTo final PersistenceInitializedNotification notification)
      {
        log.debug("onPersistenceUpdated({})", notification);
        cacheMap.clear();
      }
  }
