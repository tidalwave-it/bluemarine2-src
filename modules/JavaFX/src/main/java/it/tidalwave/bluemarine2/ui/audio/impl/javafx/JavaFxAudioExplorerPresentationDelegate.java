/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.ui.audio.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.audio.AudioExplorerPresentation;

/***********************************************************************************************************************
 *
 * The JavaFX Delegate for {@link AudioExplorerPresentation}.
 * 
 * @stereotype  JavaFXDelegate
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable
public class JavaFxAudioExplorerPresentationDelegate implements AudioExplorerPresentation
  {
    @FXML
    private ListView<PresentationModel> lvFiles;
    
    @FXML
    private Button btUp;
    
    @Inject
    private JavaFXBinder binder;
    
    @Override
    public void bind (final @Nonnull UserAction upAction)
      {
        binder.bind(btUp, upAction);
      }
    
    @Override
    public void showUp() 
      {
      }
    
    @Override
    public void populate (final @Nonnull PresentationModel pm)
      {
        binder.bind(lvFiles, pm);
      }
  }
