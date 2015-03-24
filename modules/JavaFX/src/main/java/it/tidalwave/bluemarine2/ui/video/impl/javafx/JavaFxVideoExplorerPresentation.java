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
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import it.tidalwave.bluemarine2.ui.video.VideoExplorerPresentation;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxVideoExplorerPresentation implements VideoExplorerPresentation
  {
    @Inject
    private FlowController flowController;
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void showUp()  
      {
        try
          {
            final String url = "/it/tidalwave/bluemarine2/ui/impl/javafx/VideoExplorer.fxml";
            flowController.showPresentation(FXMLLoader.load(getClass().getResource(url)));
          } 
        catch (IOException e) 
          {
            log.error("", e);   
          }
      }
  }
