/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.nio.file.Path;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.TypeSafeHashMap;
import it.tidalwave.bluemarine2.model.MediaItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString(of = "rawProperties")
public class MetadataSupport implements MediaItem.Metadata
  {
    @Getter @Nonnull
    protected final Path path;
    
    protected final Map<Key<?>, Object> rawProperties = new HashMap<>();
    
    protected final TypeSafeHashMap properties = new TypeSafeHashMap(rawProperties);
    
    /*******************************************************************************************************************
     *
     * {@inheritDocs}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Optional<T> get (final @Nonnull Key<T> key)
      {
        try 
          {
            return Optional.of(((T)properties.get(key)));
          }
        catch (NotFoundException e) 
          {
            return Optional.empty();
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDocs}
     *
     ******************************************************************************************************************/
    @Override
    public boolean containsKey (final @Nonnull Key<?> key)
      {
        return properties.containsKey(key);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDocs}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Set<Key<?>> getKeys()
      {
        return Collections.unmodifiableSet(properties.getKeys());
      }
    
    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    protected <V> void put (final @Nonnull Key<V> key, final @Nullable V value)
      {
        if (value != null)
          {
            put(key, value);
          }
      }
  }
