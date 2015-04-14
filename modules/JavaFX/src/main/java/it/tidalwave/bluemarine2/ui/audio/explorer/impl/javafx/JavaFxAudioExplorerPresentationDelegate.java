/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The JavaFX Delegate for {@link AudioRendererPresentation}.
 * 
 * @stereotype  JavaFXDelegate
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
public class JavaFxAudioExplorerPresentationDelegate implements AudioExplorerPresentation
  {
    @Getter @ToString
    static class Memento
      {
        private final int selectedIndex;  
        
        public Memento()
          {
            selectedIndex = 0;
          }
        
        public Memento (final @Nonnull ListView lvFiles) 
          {
        // TODO: add further properties, such as the precise scroller position
            selectedIndex = lvFiles.getSelectionModel().getSelectedIndex();  
          }
        
        public void applyTo (final @Nonnull ListView lvFiles)
          {
            lvFiles.getSelectionModel().select(selectedIndex);
            lvFiles.scrollTo(selectedIndex);
          }
      }
    
    @FXML
    private ListView<PresentationModel> lvFiles;
    
    @FXML
    private Button btUp;
    
    @FXML
    private Label lbFolderName;
    
    @FXML
    private ComboBox<PresentationModel> cbBrowsers;
    
    @Inject
    private JavaFXBinder binder;
    
    @Override
    public void bind (final @Nonnull Properties properties, final @Nonnull UserAction upAction)
      {
        binder.bind(btUp, upAction);
        lbFolderName.textProperty().bind(properties.folderNameProperty());
      }
    
    @Override
    public void showUp (final @Nonnull Object control) 
      {
      }
    
    @Override
    public void populateBrowsers (final @Nonnull PresentationModel pm)
      {
        binder.bind(cbBrowsers, pm);
      }
      
    @Override
    public void populateItems (final @Nonnull PresentationModel pm, final @Nonnull Optional<Object> optionalMemento)
      {
        binder.bind(lvFiles, pm, () -> 
          {
            if (!lvFiles.getItems().isEmpty())
              {
                ((Memento)optionalMemento.orElse(new Memento())).applyTo(lvFiles);
              }
          });
      }

    @Override
    public void focusOnMediaItems() 
      {
        lvFiles.requestFocus();
      }

    @Override
    public Object getMemento()
      {
        return new Memento(lvFiles);
      }
  }
