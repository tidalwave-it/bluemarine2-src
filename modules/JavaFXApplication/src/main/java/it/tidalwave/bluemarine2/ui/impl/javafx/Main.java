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
package it.tidalwave.bluemarine2.ui.impl.javafx;

import javax.annotation.Nonnull;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import it.tidalwave.ui.javafx.JavaFXSpringApplication;
import it.tidalwave.bluemarine2.util.SystemConfigurer;
import it.tidalwave.bluemarine2.initializer.Initializer;
import it.tidalwave.util.PreferencesHandler;

/***********************************************************************************************************************
 *
 * The main class initializes the logging facility and starts the JavaFX application.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class Main extends JavaFXSpringApplication
  {
    public Main()
      {
//        setMaximized(true);

        if ("arm".equals(System.getProperty("os.arch")))
          {
            setFullScreen(true);
            setFullScreenLocked(true);
          }
      }

    public static void main (final @Nonnull String ... args)
      {
        try
          {
            System.setProperty(PreferencesHandler.PROP_APP_NAME, "blueMarine2");
            SystemConfigurer.setupSlf4jBridgeHandler();
            SystemConfigurer.setSystemProperties();
            Platform.setImplicitExit(true);
            launch(args);
          }
        catch (Throwable t)
          {
            // Don't use logging facilities here, they could be not initialized
            t.printStackTrace();
            System.exit(-1);
          }
      }

    @Override
    protected void onStageCreated (final @Nonnull ApplicationContext applicationContext)
      {
        applicationContext.getBean(Initializer.class).boot();
      }
  }