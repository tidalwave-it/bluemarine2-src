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

import javax.inject.Inject;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.NodeAndDelegate;
import it.tidalwave.bluemarine2.ui.audio.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.createNodeAndDelegate;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxAudioExplorerPresentation implements AudioExplorerPresentation
  {
    private static final String FXML_URL = "/it/tidalwave/bluemarine2/ui/impl/javafx/AudioExplorer.fxml";
    
    @Inject
    private FlowController flowController;
    
    private final NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);
    
    private final AudioExplorerPresentation delegate = nad.getDelegate();
            
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void showUp()  
      {
        delegate.showUp();
        flowController.showPresentation(nad.getNode());
      }
  }
