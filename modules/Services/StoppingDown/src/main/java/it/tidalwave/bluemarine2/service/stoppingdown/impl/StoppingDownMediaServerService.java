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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import it.tidalwave.bluemarine2.mediaserver.spi.MediaServerService;
import java.util.stream.Collectors;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class StoppingDownMediaServerService implements MediaServerService
  {
    private static final Supplier<Collection<Entity>> EMPTY_SUPPLIER = () -> Collections.emptyList();

    private PhotoCollectionProvider photoCollectionProvider = new MockPhotoCollectionProvider();

    @Override @Nonnull
    public MediaFolder createRootFolder (final @Nonnull MediaFolder parent)
      {
        final List<Entity> children = new ArrayList<>();

        final MediaFolder root = new VirtualMediaFolder(parent, Paths.get("stoppingdown.net"), "Stopping Down", () -> children);

        children.add(new VirtualMediaFolder(root, Paths.get("diary"),  "Diary",  EMPTY_SUPPLIER));

        final List<Entity> photos = new ArrayList<>();
        final VirtualMediaFolder themes = new VirtualMediaFolder(root, Paths.get("themes"), "Themes", () -> photos);
        children.add(themes);

        photos.addAll(photoCollectionProvider.getPhotoIds().stream()
                                                           .map(id -> new PhotoItem(themes, id))
                                                           .collect(Collectors.toList()));
        return root;
      }
  }
