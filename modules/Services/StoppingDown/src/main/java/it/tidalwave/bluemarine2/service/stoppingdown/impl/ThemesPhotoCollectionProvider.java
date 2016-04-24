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
import java.util.List;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.spi.SupplierBasedEntityFinder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ThemesPhotoCollectionProvider extends PhotoCollectionProviderSupport
  {
    @Override @Nonnull
    public EntityFinder findPhotos (final @Nonnull MediaFolder parent)
      {
        final List<Entity> children = new ArrayList<>();
        children.add(c(parent, "birds", "Birds"));
        children.add(c(parent, "castles", "Castles"));
        children.add(c(parent, "churches", "Churches & chapels"));
        children.add(c(parent, "clouds", "Clouds"));
        children.add(c(parent, "fog", "Fog"));
        children.add(c(parent, "lonely-trees", "Lonely Trees"));

        return new SupplierBasedEntityFinder(parent, () -> children);
      }

    // FIXME: even though the finder is retrived later, through the supplier, the translation to DIDL does compute
    // the finder because it calls the count() for the children count

    @Nonnull
    private MediaFolder c (final @Nonnull MediaFolder parent,
                           final @Nonnull String path,
                           final @Nonnull String displayName)
      {
        return new VirtualMediaFolder(parent, Paths.get(path),  displayName,
            (p) -> findCachedPhotos(p, String.format("http://stoppingdown.net/themes/%s/images.xml", path)));
      }
  }
