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
package it.tidalwave.bluemarine2.downloader;

import java.io.IOException;
import java.net.URI;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @RequiredArgsConstructor @Getter @ToString(exclude = "bytes")
public class DownloadComplete 
  {
    public enum Origin
      {
        NETWORK,
        CACHE
      }

    @Nonnull
    private final URL url;

    @Nonnegative
    private final int statusCode;
    
    @Nonnull
    private final byte[] bytes;
    
    @Nonnull
    private final Origin origin;
    
    @Nonnull
    public URI getCachedUri()
      {
        // FIXME: pass the URI of the cached datum instead
        // Refactor getBytes() so it loads on demand from the URI
        try 
          {
            final Path tempFile = Files.createTempFile("tmp", "image");
            Files.write(tempFile, bytes);
            return tempFile.toUri();
          } 
        catch (IOException e) 
          {
            throw new RuntimeException(e);
          }
      }
  }
