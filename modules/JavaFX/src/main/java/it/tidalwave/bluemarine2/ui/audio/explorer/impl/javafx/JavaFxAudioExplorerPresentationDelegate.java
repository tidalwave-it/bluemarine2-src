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
import javax.inject.Provider;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.AsException;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Styleable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Displayable.*;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;
import static it.tidalwave.role.ui.UserActionProvider.*;

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
    private HBox hbBrowserButtons;
    
    @Inject
    private Provider<JavaFXBinder> binder;
    
    @Override
    public void bind (final @Nonnull AudioExplorerPresentation.Properties properties, final @Nonnull UserAction upAction)
      {
        binder.get().bind(btUp, upAction);
        lbFolderName.textProperty().bind(properties.folderNameProperty());
      }
    
    @Override
    public void showUp (final @Nonnull Object control) 
      {
      }
    
    @Override
    public void populateBrowsers (final @Nonnull PresentationModel pm)
      {
        bindToggleButtons(hbBrowserButtons, pm);
      }
      
    @Override
    public void populateItems (final @Nonnull PresentationModel pm, final @Nonnull Optional<Object> optionalMemento)
      {
        binder.get().bind(lvFiles, pm, () -> 
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
    
    /*
     * FIXME: move to SteelBlue
     * The pane must be populated with at least one button, which will be queried for the CSS style.
    */
    private void bindToggleButtons (final @Nonnull Pane pane, final @Nonnull PresentationModel pm)
      {
        final ToggleGroup group = new ToggleGroup();
        final ObservableList<Node> children = pane.getChildren();
        final ObservableList<String> styleClass = children.get(0).getStyleClass();
        children.clear();
        final SimpleComposite<PresentationModel> pmc = pm.as(SimpleComposite);
        pmc.findChildren().results().stream().forEach(cpm -> 
          {
            try 
              {
                final ToggleButton button = new ToggleButton();
                
                try // can't use asOptional() since PresentationModel is constrained to Java 7
                  {
                    button.setText((cpm.as(Displayable).getDisplayName()));
                  } 
                catch (AsException e2) 
                  {
//                    e2.printStackTrace();
                  }
                
                button.getStyleClass().addAll(styleClass);
                button.setToggleGroup(group);
                binder.get().bind(button, cpm.as(UserActionProvider).getDefaultAction());
                children.add(button);
                
                try // can't use asOptional() since PresentationModel is constrained to Java 7
                  {
                    button.getStyleClass().addAll(cpm.as(Styleable.Styleable).getStyles());
                  } 
                catch (AsException e2) 
                  {
//                    e2.printStackTrace();
                  }
              } 
            catch (NotFoundException e) 
              {
                e.printStackTrace(); // FIXME
              }
          });
      }
  }
