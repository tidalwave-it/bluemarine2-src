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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.bluemarine2.model.role.EntityWithPath;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * An {@link EntityFinder} for retrieving children of a {@link FileSystemMediaFolder}.
 *
 * @stereotype  Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class FileSystemMediaFolderFinder extends Finder8Support<EntityWithPath, EntityFinder> implements EntityFinder
  {
    private static final long serialVersionUID = 7656309392185783930L;

    @Nonnull
    private final FileSystemMediaFolder mediaFolder;

    @Nonnull
    private final Path basePath;

    // FIXME: implement a better filter looking at the file name suffix
    private final Predicate<? super Path> fileFilter = path -> !path.toFile().getName().equals(".DS_Store");

    public FileSystemMediaFolderFinder (final @Nonnull FileSystemMediaFolderFinder other, final @Nonnull Object override)
      {
        super(other, override);
        final FileSystemMediaFolderFinder source = getSource(FileSystemMediaFolderFinder.class, other, override);
        this.mediaFolder = source.mediaFolder;
        this.basePath = source.basePath;
      }

    @Override @Nonnegative
    public int count()
      {
        return evaluateDirectoryStream(stream -> stream.filter(fileFilter).collect(counting())).intValue();
      }

    @Override @Nonnull
    public EntityFinder withPath (final @Nonnull Path path)
      {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
      }

    @Override @Nonnull
    protected List<? extends EntityWithPath> computeResults()
      {
        return evaluateDirectoryStream(stream -> stream
                                                .filter(fileFilter)
                                                .map(child -> Files.isDirectory(child)
                                                            ? new FileSystemMediaFolder(child, mediaFolder, basePath)
                                                            : new FileSystemAudioFile(child, mediaFolder, basePath))
                                                .collect(toList()));
      }

    private <T> T evaluateDirectoryStream (final @Nonnull Function<Stream<Path>, T> function)
      {
        if (!Files.exists(mediaFolder.getPath()))
          {
            return function.apply(Stream.of());
          }

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(mediaFolder.getPath()))
          {
            return function.apply(StreamSupport.stream(stream.spliterator(), false));
          }
        catch (IOException e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }
      }
  }
