/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.mediaserver.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.VirtualMediaFolder.EntityCollectionFactory;
import it.tidalwave.bluemarine2.model.role.EntityBrowser;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.impl.PathAwareMediaFolderDecorator;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.mediaserver.spi.MediaServerService;
import lombok.extern.slf4j.Slf4j;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.Identifiable.Identifiable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectory implements ContentDirectory
  {
    private static final Path PATH_ROOT = Paths.get("/");

    private static final Path PATH_VIDEOS = Paths.get("videos");

    private static final Path PATH_PHOTOS = Paths.get("photos");

    private static final Path PATH_MUSIC = Paths.get("music");

    private static final Path PATH_SERVICES = Paths.get("services");

    private static final EntityCollectionFactory EMPTY = x -> Collections.emptyList(); // FIXME: move to ECF

//    @Inject FIXME
    @Autowired(required = false)
    private final List<EntityBrowser> entityBrowsers = Collections.emptyList();

//    @Inject FIXME
    @Autowired(required = false)
    private final List<MediaServerService> services = Collections.emptyList();

    private MediaFolder root;

    @Override @Nonnull
    public MediaFolder findRoot()
      {
        return root;
      }

    @PostConstruct
    private void initialize()
      {
        // FIXME: why is this called multiple times?
        log.info(">>>> discovered entity browsers: {}", entityBrowsers);
        log.info(">>>> discovered services: {}", services);
        root = new VirtualMediaFolder(Optional.empty(), PATH_ROOT, "", this::childrenFactory);
      }

    @Nonnull
    private Collection<PathAwareEntity> childrenFactory (final @Nonnull MediaFolder parent)
      {
        return Arrays.asList(new VirtualMediaFolder(parent, PATH_MUSIC,    "Music",    this::musicFactory),
                             new VirtualMediaFolder(parent, PATH_PHOTOS,   "Photos",   EMPTY),
                             new VirtualMediaFolder(parent, PATH_VIDEOS,   "Videos",   EMPTY),
                             new VirtualMediaFolder(parent, PATH_SERVICES, "Services", this::servicesFactory));
      }

    @Nonnull
    private Collection<MediaFolder> musicFactory (final @Nonnull MediaFolder parent)
      {
        // TODO: filter by MIME type
        return entityBrowsers.stream()
                             .sorted(comparing(browser -> browser.as(Displayable).getDisplayName()))
                             .map(browser -> createMediaFolder(parent, browser))
                             .collect(toList());
      }

    @Nonnull
    private Collection<MediaFolder> servicesFactory (final @Nonnull MediaFolder parent)
      {
        return services.stream().map(service -> service.createRootFolder(parent)).collect(toList());
      }

    @Nonnull
    private static MediaFolder createMediaFolder (final @Nonnull MediaFolder parent,
                                                  final @Nonnull EntityBrowser browser)
      {
        final String fallBack = browser.getClass().getSimpleName();
        final String pathSegment = browser.asOptional(Identifiable).map(i -> i.getId().stringValue()).orElse(fallBack);
        final Displayable displayable = browser.asOptional(Displayable).orElse(new DefaultDisplayable(fallBack));
        log.trace("createMediaFolder({}, {}) - path: {} displayable: {}", parent, browser, pathSegment, displayable);
        return new PathAwareMediaFolderDecorator(browser.getRoot(), parent, Paths.get(pathSegment), displayable);
      }
  }
