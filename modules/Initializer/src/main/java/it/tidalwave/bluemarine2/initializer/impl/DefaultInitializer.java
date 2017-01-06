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
package it.tidalwave.bluemarine2.initializer.impl;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import it.tidalwave.bluemarine2.persistence.PersistencePropertyNames;
import it.tidalwave.bluemarine2.downloader.DownloaderPropertyNames;
import it.tidalwave.bluemarine2.initializer.Initializer;
import it.tidalwave.bluemarine2.util.PowerOffNotification;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultInitializer implements Initializer
  {
    @Inject
    private MessageBus messageBus;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void boot()
      {
        final Map<Key<?>, Object> properties = new HashMap<>();
        final Path configPath = getConfigurationPath();
        log.info("configPath is {}", configPath);
        properties.put(ModelPropertyNames.ROOT_PATH, configPath);
        properties.put(PersistencePropertyNames.STORAGE_FOLDER, configPath.resolve("storage"));
        properties.put(PersistencePropertyNames.IMPORT_FILE, configPath.resolve("import").resolve("repository.n3"));
        properties.put(PersistencePropertyNames.RENAME_IMPORT_FILE, true);
        properties.put(DownloaderPropertyNames.CACHE_FOLDER_PATH, configPath.resolve("cache"));
        messageBus.publish(new PowerOnNotification(properties));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PreDestroy
    private void shutdown()
      {
        messageBus.publish(new PowerOffNotification());
      }

    /*******************************************************************************************************************
     *
     * FIXME: this is duplicated in JavaFXSpringApplication
     *
     ******************************************************************************************************************/
    @Nonnull
    private Path getConfigurationPath()
      {
        String s = System.getProperty("blueMarine2.workspace");

        if (s != null)
          {
            return Paths.get(s);
          }

        s = System.getProperty("user.home", "/");
        final String osName = System.getProperty("os.name").toLowerCase();

        switch (osName)
          {
            case "linux":
                s += "/.blueMarine2";
                break;

            case "mac os x":
                s += "/Library/Application Support/blueMarine2";
                break;

            case "windows":
                s += "/.blueMarine2";
                break;
          }

        return Paths.get(s);
      }
  }
