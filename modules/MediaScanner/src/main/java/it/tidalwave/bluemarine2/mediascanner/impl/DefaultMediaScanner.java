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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import lombok.extern.slf4j.Slf4j;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static it.tidalwave.bluemarine2.util.Miscellaneous.toFileBMT46;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMediaScanner
  {
    private static final String ALGORITHM = "SHA1";

    @Inject
    private ProgressHandler progress;

    @Inject
    private MessageBus messageBus;

    // With magnetic disks it's better to access files one at a time
    // TODO: with SSD, this is not true
    private final Semaphore diskSemaphore = new Semaphore(1);

    /*******************************************************************************************************************
     *
     * Processes a folder of {@link MediaItem}s.
     *
     * @param   folder      the folder
     *
     ******************************************************************************************************************/
    public void process (final @Nonnull MediaFolder folder)
      {
        log.info("process({})", folder);
//        shared.reset();
        progress.reset();
        progress.incrementTotalFolders();
        messageBus.publish(new InternalMediaFolderScanRequest(folder));
      }

    /*******************************************************************************************************************
     *
     * Scans a folder of {@link MediaItem}s.
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onInternalMediaFolderScanRequest
                                    (final @ListensTo @Nonnull InternalMediaFolderScanRequest request)
      {
        try
          {
            log.info("onInternalMediaFolderScanRequest({})", request);

            request.getFolder().findChildren().stream().forEach(item ->
              {
                if (item instanceof MediaItem)
                  {
                    progress.incrementTotalMediaItems();
                    messageBus.publish(new MediaItemImportRequest((MediaItem)item, Optional.empty()));
                  }

                else if (item instanceof MediaFolder)
                  {
                    progress.incrementTotalFolders();
                    messageBus.publish(new InternalMediaFolderScanRequest((MediaFolder)item));
                  }
              });
          }
        catch (Exception e)
          {
            log.error("", e);
          }
        finally
          {
            progress.incrementScannedFolders();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onMediaItemImportRequest (final @ListensTo @Nonnull MediaItemImportRequest request)
      throws InterruptedException, NoSuchAlgorithmException, IOException
      {
        if (!request.getSha1().isPresent())
          {
            final byte[] sha1 = sha1Of(request.getMediaItem().getPath());
            messageBus.publish(new MediaItemImportRequest(request.getMediaItem(), Optional.of(sha1)));
            progress.incrementDoneFingerprints();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public byte[] sha1Of (final @Nonnull Path path)
      throws InterruptedException, NoSuchAlgorithmException, IOException
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
                return digestComputer.digest();
              }
          }
        finally
          {
            diskSemaphore.release();
          }
      }
  }
