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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.AudioFile;
import lombok.Delegate;
import lombok.Getter;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

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
public class DefaultAudioFile implements AudioFile
  {
    @Getter @Nonnull
    private final Path path;
    
    @Getter @Nonnull
    private final Path relativePath;
    
    @Getter @CheckForNull
    private final Entity parent;
    
    @CheckForNull
    private Metadata metadata;
    
    @Delegate
    private final AsSupport asSupport = new AsSupport(this);

    public DefaultAudioFile (final @Nonnull Path path,
                             final @Nonnull Entity parent,
                             final @Nonnull Path basePath)
      {
        this.path = path;
        this.parent = parent;
        this.relativePath = basePath.relativize(path);
      }
    
    @Override @Nonnull
    public String toString() 
      {
        return String.format("DefaultAudioFile(%s)", relativePath);
      }

    @Override @Nonnull
    public synchronized Metadata getMetadata() 
      {
        if (metadata == null)
          {
            metadata = new AudioMetadata(path);
          }
        
        return metadata;
      }


    @Override @Nonnull
    public Optional<String> getLabel() 
      {
        return getMetadata().get(TITLE);
      }

    @Override @Nonnull
    public Optional<Duration> getDuration()
      {
        return getMetadata().get(DURATION);
      }

    @Override @Nonnull
    public Finder8<? extends Entity> findComposers() 
      {
        return new Finder8Support<Entity, Finder8<Entity>>()
          {
            @Override
            protected List<? extends Entity> computeNeededResults() 
              {
                return getMetadata().get(Metadata.COMPOSER)
                                    .map(artistName -> asList(new NamedEntity(artistName)))
                                    .orElse(emptyList());
              }
          };
      }

    @Override @Nonnull
    public Finder8<Entity> findMakers() 
      {
        return new Finder8Support<Entity, Finder8<Entity>>()
          {
            @Override
            protected List<? extends Entity> computeNeededResults() 
              {
                return getMetadata().get(Metadata.ARTIST)
                                    .map(artistName -> asList(new NamedEntity(artistName)))
                                    .orElse(emptyList());
              }
          };
      }
    
    @Override @Nonnull
    public AudioFile getAudioFile()
      {
        return this;
      }

    @Override @Nonnull
    public Optional<Entity> getRecord() 
      {
            // FIXME: check - parent should be always present - correct?
        return Optional.of(new NamedEntity(getParent().as(Displayable).getDisplayName()));
      }
  }
