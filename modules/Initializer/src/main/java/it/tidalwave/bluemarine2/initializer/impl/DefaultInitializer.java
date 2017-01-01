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
package it.tidalwave.bluemarine2.initializer.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.initializer.Initializer;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
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
    private Provider<MessageBus> messageBus;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void boot ()
      {
        final Map<Key<?>, Object> properties = new HashMap<>();
        final Path configPath = getConfigurationPath();
        log.info("configPath is {}", configPath);
        final Path repositoryPath = configPath.resolve("repository.n3");
        final Path cachePath = configPath.resolve("cache");
        properties.put(it.tidalwave.bluemarine2.persistence.PropertyNames.REPOSITORY_PATH, repositoryPath);
        properties.put(it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH, configPath);
        properties.put(it.tidalwave.bluemarine2.downloader.PropertyNames.CACHE_FOLDER_PATH, cachePath);
        messageBus.get().publish(new PowerOnNotification(properties));
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
