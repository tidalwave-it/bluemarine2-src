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
package it.tidalwave.bluemarine2.ui.video.impl.javafx;

import javax.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import it.tidalwave.bluemarine2.ui.video.VideoExplorerPresentation;

/***********************************************************************************************************************
 *
 * The JavaFX Delegate for {@link VideoExplorerPresentation}.
 * 
 * @stereotype  JavaFXDelegate
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable
public class JavaFxVideoExplorerPresentationDelegate implements VideoExplorerPresentation
  {
    @FXML
    private Button btBack;
    
    @Inject
    private FlowController flowController;
    
    @FXML
    private void initialize()
      {
        // FIXME: temporary - should go call back the controller instead
        btBack.setOnAction(event -> { flowController.dismissCurrentPresentation(); });
      }

    @Override
    public void showUp() 
      {
      }
  }
