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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.nio.file.Path;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.model.MediaFileSystem;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.PropertyNames;
import lombok.Delegate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static java.util.concurrent.TimeUnit.SECONDS;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMediaFileSystem implements MediaFileSystem
  {
    private CountDownLatch initialized = new CountDownLatch(1);

    @Getter
    private Path rootPath;

    @Delegate
    private final AsSupport asSupport = new AsSupport(this);

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
    /* VisibleForTesting */ void onPowerOnNotification (final @ListensTo @Nonnull PowerOnNotification notification)
      throws NotFoundException
      {
        log.info("onPowerOnNotification({})", notification);
        rootPath = notification.getProperties().get(PropertyNames.ROOT_PATH).resolve("Music");
        log.info("rootPath: {}", rootPath);
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
