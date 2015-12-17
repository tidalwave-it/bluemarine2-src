/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import lombok.extern.slf4j.Slf4j;
import static javafx.scene.input.KeyCode.*;

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
    private Button btPrev;
    
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
    private Button btNext;
    
    @FXML
    private ProgressBar pbPlayProgress;
    
    @FXML
    private Label lbTitle;
    
    @FXML
    private Label lbFolderName;
    
    @FXML
    private Label lbArtist;
    
    @FXML
    private Label lbComposer;
    
    @FXML
    private Label lbDuration;
    
    @FXML
    private Label lbPlayTime;
    
    @FXML
    private Label lbNextTrack;
    
    @Inject
    private Provider<JavaFXBinder> binder;

    private final Map<KeyCombination, Runnable> accelerators = new HashMap<>();
    
    @FXML
    private void initialize()
      {
//        final ObservableMap<KeyCombination, Runnable> accelerators = btPlay.getScene().getAccelerators();
        accelerators.put(new KeyCodeCombination(PLAY),     () -> btPlay.fire());
        accelerators.put(new KeyCodeCombination(STOP),     () -> btStop.fire());
        accelerators.put(new KeyCodeCombination(PAUSE),    () -> btPause.fire());
        accelerators.put(new KeyCodeCombination(REWIND),   () -> btRewind.fire());
        accelerators.put(new KeyCodeCombination(FAST_FWD), () -> btFastForward.fire());
      }
    
    @FXML // TODO: should be useless, but getScene().getAccelerators() doesn't work
    public void onKeyReleased (final @Nonnull KeyEvent event)
      {
        accelerators.getOrDefault(new KeyCodeCombination(event.getCode()), () -> {}).run();
      }
    
    @Override
    public void bind (final @Nonnull Properties properties,
                      final @Nonnull UserAction prevAction,
                      final @Nonnull UserAction rewindAction,
                      final @Nonnull UserAction stopAction,
                      final @Nonnull UserAction pauseAction,
                      final @Nonnull UserAction playAction,
                      final @Nonnull UserAction fastForwardAction,
                      final @Nonnull UserAction nextAction)
      {
        binder.get().bind(btPrev,        prevAction);  
        binder.get().bind(btRewind,      rewindAction);  
        binder.get().bind(btStop,        stopAction);  
        binder.get().bind(btPause,       pauseAction);  
        binder.get().bind(btPlay,        playAction);  
        binder.get().bind(btFastForward, fastForwardAction); 
        binder.get().bind(btNext,        nextAction);  
        
        lbTitle.textProperty().bind(properties.titleProperty());
        lbFolderName.textProperty().bind(properties.folderNameProperty());
        lbArtist.textProperty().bind(properties.artistProperty());
        lbComposer.textProperty().bind(properties.composerProperty());
        lbDuration.textProperty().bind(properties.durationProperty());
        lbPlayTime.textProperty().bind(properties.playTimeProperty());
        lbNextTrack.textProperty().bind(properties.nextTrackProperty());
        pbPlayProgress.progressProperty().bind(properties.progressProperty());
      }
    
    @Override
    public void showUp (final @Nonnull Object control) 
      {
      }

    @Override
    public void focusOnPlayButton() 
      {
        btPlay.requestFocus();
      }

    @Override
    public void focusOnStopButton() 
      {
        btStop.requestFocus();
      }
  }
