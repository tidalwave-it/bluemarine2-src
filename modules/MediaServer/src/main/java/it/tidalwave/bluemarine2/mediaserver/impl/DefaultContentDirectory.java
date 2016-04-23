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
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.mediaserver.spi.MediaServerService;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectory implements ContentDirectory
  {
    private static final Supplier<Collection<Entity>> EMPTY_SUPPLIER = () -> Collections.emptyList();

//    @Inject FIXME
    @Autowired(required = false)
    private List<MediaServerService> services = Collections.emptyList();

    private MediaFolder root;

    @Override @Nonnull
    public MediaFolder findRoot()
      {
        return root;
      }

    @PostConstruct
    private void initialize()
      {
        final List<Entity> children = new ArrayList<>();

        root =       new VirtualMediaFolder(null, Paths.get("/"),      "",       () -> children);
        children.add(new VirtualMediaFolder(root, Paths.get("music"),  "Music",  EMPTY_SUPPLIER));
        children.add(new VirtualMediaFolder(root, Paths.get("photos"), "Photos", EMPTY_SUPPLIER));
        children.add(new VirtualMediaFolder(root, Paths.get("videos"), "Videos", EMPTY_SUPPLIER));

        children.add(createServicesRootFolder(root));
      }

    @Nonnull
    private MediaFolder createServicesRootFolder (final @Nonnull MediaFolder root)
      {
        log.info(">>>> discovered services: {}", services);

        final List<Entity> children = new ArrayList<>();
        final MediaFolder servicesRootFolder = new VirtualMediaFolder(root, Paths.get("services"), "Services", () -> children);
        services.stream().forEach(service -> children.add(service.createRootFolder(servicesRootFolder)));

        return servicesRootFolder;
      }
  }
