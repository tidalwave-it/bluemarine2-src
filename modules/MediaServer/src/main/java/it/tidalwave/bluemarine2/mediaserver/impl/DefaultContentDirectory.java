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
package it.tidalwave.bluemarine2.mediaserver.impl;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.nio.file.Paths;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.ArrayListFinder8;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DefaultContentDirectory implements ContentDirectory
  {
    private final MediaFolder root;

    private final Map<Id, Entity> entityMapById = new HashMap<>();

    public DefaultContentDirectory()
      {
        final List<Entity> children = new ArrayList<>();

        root = new VirtualMediaFolder(new Id("0"), Paths.get("Root"), null)
          {
            @Override @Nonnull
            public Finder8<Entity> findChildren()
              {
                return new ArrayListFinder8<>(children);
              }
          };

        entityMapById.put(new Id("0"), root);

        children.add(createFolder(root, "Music"));
        children.add(createFolder(root, "Photos"));
        children.add(createFolder(root, "Videos"));
        children.add(createFolder(root, "Services"));
      }

    @Override @Nonnull
    public Entity findEntityById (final @Nonnull Id id) // FIXME: use a Finder
      {
        return entityMapById.get(id); // FIXME: NPE
      }

    @Nonnull
    private MediaFolder createFolder (final @Nonnull MediaFolder owner, final @Nonnull String displayName)
      {
        final Id id = new Id(UUID.randomUUID().toString());
        final VirtualMediaFolder folder = new VirtualMediaFolder(id, Paths.get(displayName), owner);
        entityMapById.put(id, folder);
        return folder;
      }
  }
