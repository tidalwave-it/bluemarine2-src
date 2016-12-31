/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collection;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.role.EntityWithPath;
import it.tidalwave.bluemarine2.mediaserver.spi.MediaServerService;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
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
    public MediaFolder createRootFolder (final @Nonnull MediaFolder parent)
      {
        return new VirtualMediaFolder(parent, PATH_ROOT, "Stopping Down", this::childrenFactory);
      }

    @Nonnull
    private Collection<EntityWithPath> childrenFactory (final @Nonnull MediaFolder parent)
      {
        return Arrays.asList(new VirtualMediaFolder(parent, PATH_DIARY,  "Diary",  diaryProvider::findPhotos),
                             new VirtualMediaFolder(parent, PATH_THEMES, "Themes", themesProvider::findPhotos));
      }
  }
