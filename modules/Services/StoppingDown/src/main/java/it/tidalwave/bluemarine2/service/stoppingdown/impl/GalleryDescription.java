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
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.function.BiFunction;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.role.EntityWithPath;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder.EntityCollectionFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable
@RequiredArgsConstructor @Getter @EqualsAndHashCode @ToString
public class GalleryDescription
  {
    @Nonnull
    private final String displayName;

    @Nonnull
    private final String url;

    /*******************************************************************************************************************
     *
     * Creates a {@link MediaFolder} with the given parent and the children provided by a factory.
     *
     * @param   parent              the parent folder
     * @param   entitiesFactory     a function which, given the parent and a URL, provides the entities
     * @return                      the folder
     *
     ******************************************************************************************************************/
    // FIXME: even though the finder is retrieved later, through the factory, the translation to DIDL does compute
    // the finder because it calls the count() for the children count
    @Nonnull
    public EntityWithPath createFolder (final @Nonnull MediaFolder parent,
                                        final @Nonnull BiFunction<MediaFolder, String, Collection<EntityWithPath>> entitiesFactory)
      {
        final Path path = Paths.get(url.replaceAll("^.*(themes|diary\\/[0-9]{4})\\/(.*)\\/images\\.xml", "$2"));
        final EntityCollectionFactory ecf = p -> entitiesFactory.apply(p, url);
        return new VirtualMediaFolder(parent, path, displayName, ecf);
      }
  }
