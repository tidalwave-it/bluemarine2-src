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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.util.spi.PriorityAsSupport;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;

/***********************************************************************************************************************
 *
 * The default implementation of {@link MediaFolder}. It basically does nothing, it just acts as an aggregator of roles.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @AllArgsConstructor
public class FileSystemMediaFolder implements MediaFolder
  {
    @Getter @Nonnull
    private final Path path;

    @Nullable
    private final MediaFolder parent;

    @Getter @Nonnull
    private final Path basePath;

    @Delegate
    private final PriorityAsSupport asSupport = new PriorityAsSupport(this);

    @Override @Nonnull
    public Optional<PathAwareEntity> getParent()
      {
        return Optional.ofNullable(parent);
      }

    @Override @Nonnull
    public PathAwareFinder findChildren()
      {
        return new FileSystemMediaFolderFinder(this, basePath).sort(new MediaItemComparator());
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("FileSystemMediaFolder(%s)", basePath.relativize(path));
      }
  }
