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
package it.tidalwave.bluemarine2.ui.audio.renderer.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.ProcessExecutor;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import lombok.extern.slf4j.Slf4j;

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
@Configurable @Slf4j
public class JavaFxAudioRendererPresentationDelegate implements AudioRendererPresentation
  {
    @FXML
    private Button btRewind;
    
    @FXML
    private Button btStop;
    
    @FXML
    private Button btPause;
    
    @FXML
    private Button btPlay;
    
    @FXML
    private Button btFastForward;
    
    @FXML
    private ProgressBar pbPlayProgress;
    
    @FXML
    private Label lbTitle;
    
    @FXML
    private Label lbDuration;
    
    @FXML
    private Label lbPlayTime;
    
    @Inject
    private JavaFXBinder binder;
    
    private MediaItem mediaItem;
    
    private ProcessExecutor executor;
    
    @Override
    public void bind (final @Nonnull UserAction rewindAction,
                      final @Nonnull UserAction stopAction,
                      final @Nonnull UserAction pauseAction,
                      final @Nonnull UserAction playAction,
                      final @Nonnull UserAction fastForwardAction,
                      final @Nonnull Properties properties)
      {
        binder.bind(btRewind,      rewindAction);  
        binder.bind(btStop,        stopAction);  
        binder.bind(btPause,       pauseAction);  
        binder.bind(btPlay,        playAction);  
        binder.bind(btFastForward, fastForwardAction); 
        
        lbTitle.textProperty().bind(properties.getTitleProperty());
        lbDuration.textProperty().bind(properties.getDurationProperty());
        lbPlayTime.textProperty().bind(properties.getPlayTimeProperty());
        pbPlayProgress.progressProperty().bind(properties.getProgressProperty());
      }
    
    @Override
    public void showUp() 
      {
      }

    @Override
    public void setMediaItem (final @Nonnull MediaItem mediaItem) 
      {
        this.mediaItem = mediaItem;
      }
  }
