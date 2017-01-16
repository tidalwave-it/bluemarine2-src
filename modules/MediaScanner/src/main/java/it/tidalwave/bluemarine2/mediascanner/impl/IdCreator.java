/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import java.util.concurrent.Semaphore;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import it.tidalwave.util.Id;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static it.tidalwave.bluemarine2.util.Formatters.*;
import static it.tidalwave.bluemarine2.util.Miscellaneous.toFileBMT46;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class IdCreator
  {
    private static final String ALGORITHM = "SHA1";
    // With magnetic disks it's better to access files one at a time
    // TODO: with SSD, this is not true
    private final Semaphore diskSemaphore = new Semaphore(1);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Id createSha1Id (final @Nonnull Path path)
      {
        try
          {
            diskSemaphore.acquire();
            final File file = toFileBMT46(path);

            try (final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r"))
              {
                final MappedByteBuffer byteBuffer = randomAccessFile.getChannel().map(READ_ONLY, 0, file.length());
                final MessageDigest digestComputer = MessageDigest.getInstance(ALGORITHM);
                digestComputer.update(byteBuffer);
                return new Id(toHexString(digestComputer.digest()));
              }
          }
        catch (InterruptedException | NoSuchAlgorithmException | IOException e)
          {
            throw new RuntimeException(e);
          }
        finally
          {
            diskSemaphore.release();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Id createSha1 (final @Nonnull String string)
      {
        try
          {
            final MessageDigest digestComputer = MessageDigest.getInstance(ALGORITHM);
            digestComputer.update(string.getBytes(UTF_8));
            return new Id(toHexString(digestComputer.digest()));
          }
        catch (NoSuchAlgorithmException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
