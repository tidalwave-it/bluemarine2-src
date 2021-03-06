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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * At the moment there's no support for images in the Model.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString(doNotUseGetters = true)
public class PhotoItem extends EntityWithRoles implements MediaItem, PathAwareEntity
  {
    @Nonnull
    private final MediaFolder parent;

    @Getter @Nonnull
    private final String id;

    @Getter @Nonnull
    private final String title;

    @Override @Nonnull
    public Path getPath()
      {
        return parent.getPath().resolve(id);
      }

    @Override @Nonnull
    public Metadata getMetadata()
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override @Nonnull
    public AudioFile getAudioFile()
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.of(parent);
      }

    @Override @Nonnull
    public String toDumpString()
      {
        return String.format("PhotoItem(parent=%s, id=%s, title=%s)", parent.getPath().toString(), id, title);
      }
  }
