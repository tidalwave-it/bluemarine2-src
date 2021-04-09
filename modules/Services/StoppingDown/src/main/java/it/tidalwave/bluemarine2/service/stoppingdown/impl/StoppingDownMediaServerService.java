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
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Collection;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.mediaserver.spi.MediaServerService;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class StoppingDownMediaServerService implements MediaServerService
  {
    private static final Path PATH_ROOT = Paths.get("stoppingdown.net");

    private static final Path PATH_DIARY = Paths.get("diary");

    private static final Path PATH_THEMES = Paths.get("themes");

    @Inject @Named("diaryPhotoCollectionProvider")
    private PhotoCollectionProvider diaryProvider;

    @Inject @Named("themesPhotoCollectionProvider")
    private PhotoCollectionProvider themesProvider;

    @Override @Nonnull
    public MediaFolder createRootFolder (@Nonnull final MediaFolder parent)
      {
        return new VirtualMediaFolder(parent, PATH_ROOT, "Stopping Down", this::childrenFactory);
      }

    @Nonnull
    private Collection<PathAwareEntity> childrenFactory (@Nonnull final MediaFolder parent)
      {
        return List.of(new VirtualMediaFolder(parent, PATH_DIARY,  "Diary",  diaryProvider::findPhotos),
                       new VirtualMediaFolder(parent, PATH_THEMES, "Themes", themesProvider::findPhotos));
      }
  }
