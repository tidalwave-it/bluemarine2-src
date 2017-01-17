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
package it.tidalwave.bluemarine2.service.vimeo.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Optional;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * At the moment there's no support for images in the Model.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString
class VideoMediaItem extends EntityWithRoles implements MediaItem
  {
    @Nonnull
    private final MediaFolder parent;

    @Getter @Nonnull
    private final String id;

    @Getter @Nonnull
    private final String title;

    @Getter @Nonnull
    private final String mimeType;

    @Getter @Nonnull
    private final String url;

    @Override @Nonnull
    public Path getPath()
      {
        return parent.getPath().resolve(id);
      }

    @Override @Nonnull
    public Path getRelativePath()
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.of(parent);
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
  }