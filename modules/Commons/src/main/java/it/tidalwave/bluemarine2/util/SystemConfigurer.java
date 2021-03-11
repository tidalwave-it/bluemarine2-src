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
package it.tidalwave.bluemarine2.util;

import org.slf4j.bridge.SLF4JBridgeHandler;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class SystemConfigurer
  {
    public static void setupSlf4jBridgeHandler()
      {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
      }

    public static void setSystemProperties()
      {
        final String home = System.getProperty("user.home", "/tmp");
        final String osName = System.getProperty("os.name").toLowerCase();

        switch (osName)
          {
            case "linux":
                // on Linux we define paths in the launcher shell
                break;

            case "mac os x":
                final String workspace = System.getProperty("blueMarine2.workspace", home + "/Library/Application Support/blueMarine2");
                System.setProperty("blueMarine2.workspace", workspace);
                System.setProperty("blueMarine2.logFolder", workspace + "/logs");
                System.setProperty("blueMarine2.logConfigOverride", workspace + "/config/logback-override.xml");
                break;

            case "windows":
                // FIXME todo
                break;

            default:
                throw new ExceptionInInitializerError("Unknown o.s.: " + osName);
          }
      }
  }
