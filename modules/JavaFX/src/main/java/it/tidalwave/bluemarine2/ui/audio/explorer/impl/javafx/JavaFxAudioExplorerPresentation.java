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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.NodeAndDelegate;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.createNodeAndDelegate;

/***********************************************************************************************************************
 *
 * The JavaFX implementation of {@link AudioExplorerPresentation}.
 * 
 * @stereotype  Presentation
 * 
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxAudioExplorerPresentation implements AudioExplorerPresentation
  {
    interface DelegateExclusions
      {
        public void showUp(Object control);
      }
    
    private static final String FXML_URL = "/it/tidalwave/bluemarine2/ui/impl/javafx/AudioExplorer.fxml";
    
    @Inject
    private FlowController flowController;
    
    private final NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);
    
    @Delegate(excludes = DelegateExclusions.class)
    private final AudioExplorerPresentation delegate = nad.getDelegate();
            
    @Override
    public void showUp (@Nonnull final Object control)
      {
        delegate.showUp(control);
        flowController.showPresentation(nad.getNode(), control);
      }
  }
