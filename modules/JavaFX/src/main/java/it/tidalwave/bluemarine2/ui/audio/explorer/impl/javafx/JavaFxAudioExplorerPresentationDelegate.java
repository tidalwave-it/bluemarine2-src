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
import java.util.stream.Stream;
import java.net.URI;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import it.tidalwave.bluemarine2.util.JavaFXBinderSupplements;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;

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
        
        public Memento (final @Nonnull ListView<PresentationModel> lvFiles) 
          {
        // TODO: add further properties, such as the precise scroller position
            selectedIndex = lvFiles.getSelectionModel().getSelectedIndex();  
          }
        
        public void applyTo (final @Nonnull ListView<PresentationModel> lvFiles)
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
    
    @FXML
    private Pane pnCoverArt;
    
    @FXML
    private ImageView ivCoverArt;
    
    @FXML
    private VBox vbDetails;
    
    @Inject
    private Provider<JavaFXBinder> binder;
    
    @Inject
    private Provider<JavaFXBinderSupplements> binderSupplements;
    
    @FXML
    private void initialize()
      {
        pnCoverArt.prefHeightProperty().bind(pnCoverArt.widthProperty());
        ivCoverArt.fitWidthProperty().bind(pnCoverArt.widthProperty().multiply(0.9));
        ivCoverArt.fitHeightProperty().bind(pnCoverArt.heightProperty().multiply(0.9));
      }
    
    @Override
    public void bind (final @Nonnull Properties properties, final @Nonnull UserAction upAction)
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
        binderSupplements.get().bindToggleButtons(hbBrowserButtons, pm);
      }
      
    @Override
    public void populateItems (final @Nonnull PresentationModel pm, final @Nonnull Optional<Object> optionalMemento)
      {
        binder.get().bind(lvFiles, pm, () -> 
          {
            if (!lvFiles.getItems().isEmpty())
              {
                ((Memento)optionalMemento.orElse(new Memento())).applyTo(lvFiles);
                lvFiles.requestFocus();
              }
          });
      }

    @Override
    public void renderDetails (final @Nonnull String entityDetails)
      {
        vbDetails.getChildren().setAll(Stream.of(entityDetails.split("\n")).map(s -> createLabel(s)).collect(toList()));
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
    
    @Override
    public void setCoverArt (final @Nonnull Optional<URI> optionalCoverArtUri)
      {
        ivCoverArt.setImage(optionalCoverArtUri.map(uri -> new Image(uri.toString())).orElse(null));
      }
    
    /*******************************************************************************************************************
     *
     * With a remote there's no TAB key, so we must emulate some tab control with left and right arrows.
     * 
     * FIXME: try to generalise (e.g. cyclefocus?) and move to JavaFxApplicationPresentationDelegate.
     * 
     * @param   event   the key event
     *
     ******************************************************************************************************************/
    @FXML
    public void onKeyPressed (final @Nonnull KeyEvent event)
      {
        log.debug("onKeyPressed({})", event);
        
        if (lvFiles.isFocused())
          {
            if (event.getCode().equals(KeyCode.LEFT))
              {
                btUp.requestFocus();
              }
            else if (event.getCode().equals(KeyCode.RIGHT))
              {
                hbBrowserButtons.getChildren().get(0).requestFocus();
              }
          }
      }
    
    @Nonnull
    private static Label createLabel (final @Nonnull String s)
      {
        final Label label = new Label(s);
        label.getStyleClass().setAll("label", "track-details");
        return label;
      }
  }
