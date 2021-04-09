/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.nio.file.Path;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.spi.PriorityAsSupport;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import lombok.experimental.Delegate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static java.util.concurrent.TimeUnit.SECONDS;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMediaFileSystem implements MediaFileSystem
  {
    private final CountDownLatch initialized = new CountDownLatch(1);

    @Getter
    private Path rootPath;

    @Delegate
    private final PriorityAsSupport asSupport = new PriorityAsSupport(this);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public MediaFolder getRoot()
      {
        waitForPowerOn();
        return new FileSystemMediaFolder(rootPath, null, rootPath);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting FIXME */ public void onPowerOnNotification (@ListensTo @Nonnull final PowerOnNotification notification)
      throws NotFoundException
      {
        log.info("onPowerOnNotification({})", notification);
        rootPath = notification.getProperties().get(ModelPropertyNames.ROOT_PATH).resolve("Music");
        log.info(">>>> rootPath: {}", rootPath);
        initialized.countDown();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void waitForPowerOn()
      {
        try
          {
            if (!initialized.await(10, SECONDS))
              {
                throw new IllegalStateException("rooPath null: did not receive PowerOnNotification");
              }
          }
        catch (InterruptedException ex)
          {
            throw new IllegalStateException("Interrupted while waiting for PowerOnNotification");
          }
      }
  }
