/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import it.tidalwave.util.Finder;
import it.tidalwave.util.spi.SimpleFinder8Support;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A {@link Finder} for retrieving children of a {@link MediaFolder}.
 * 
 * @stereotype  Finder
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j 
public class MediaFolderFinder extends SimpleFinder8Support<Entity>
  {
    @Nonnull
    private final MediaFolder mediaFolder;
    
    @Nonnull
    private final Path basePath;
    
    @Override @Nonnull
    protected List<? extends Entity> computeResults() 
      {
        final List<Entity> result = new ArrayList<>();

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(mediaFolder.getPath()))
          {
            for (final Path child : stream)
              {
                result.add(child.toFile().isDirectory() ? new DefaultMediaFolder(child, mediaFolder, basePath)
                                                        : new DefaultMediaItem(child, mediaFolder, basePath));
              }
          } 
        catch (IOException e)
          {
            log.error("", e);
          }
        
        return result;
      }
  }
