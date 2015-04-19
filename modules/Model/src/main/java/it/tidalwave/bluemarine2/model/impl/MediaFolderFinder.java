/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
import java.util.concurrent.atomic.AtomicInteger;
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

    public MediaFolderFinder (final @Nonnull MediaFolderFinder other, final @Nonnull Object override) 
      {
        super(other, override);
        final MediaFolderFinder source = getSource(MediaFolderFinder.class, other, override);
        this.mediaFolder = source.mediaFolder;
        this.basePath = source.basePath;
      }
    
    @Override
    public int count() 
      {
        final AtomicInteger c = new AtomicInteger(0);
        
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(mediaFolder.getPath()))
          {
            stream.forEach(path -> c.incrementAndGet());
          } 
        catch (IOException e)
          {
            log.error("", e);
          }
        
        return c.intValue();
      }
    
    @Override @Nonnull
    protected List<? extends Entity> computeResults() 
      {
        final List<Entity> result = new ArrayList<>();

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(mediaFolder.getPath()))
          {
            for (final Path child : stream)
              {
                result.add(child.toFile().isDirectory() ? new FileSystemMediaFolder(child, mediaFolder, basePath)
                                                        : new FileSystemAudioFile(child, mediaFolder, basePath));
              }
          } 
        catch (IOException e)
          {
            log.error("", e);
          }
        
        return result;
      }
  }
