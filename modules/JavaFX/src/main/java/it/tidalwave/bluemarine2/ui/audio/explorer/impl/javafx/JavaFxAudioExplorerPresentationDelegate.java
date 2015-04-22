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
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.ui.Styleable.Styleable;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;
import static it.tidalwave.role.ui.UserActionProvider.*;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import static java.util.Collections.*;

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
    
    @FXML
    private Pane pnCoverArt;
    
    @FXML
    private ImageView ivCoverArt;
    
    @FXML
    private VBox vbDetails;
    
    @Inject
    private Provider<JavaFXBinder> binder;
    
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
    public void setCoverImage (final @Nonnull Optional<URI> imageUri)
      {
        ivCoverArt.setImage(imageUri.map(uri -> new Image(uri.toString())).orElse(null));
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
    
    /*
     * FIXME: move to SteelBlue
     * The pane must be pre-populated with at least one button, which will be queried for the CSS style.
    */
    private void bindToggleButtons (final @Nonnull Pane pane, final @Nonnull PresentationModel pm)
      {
        final ToggleGroup group = new ToggleGroup();
        final ObservableList<Node> children = pane.getChildren();
        final ObservableList<String> prototypeStyleClass = children.get(0).getStyleClass();
        final SimpleComposite<PresentationModel> pmc = pm.as(SimpleComposite);
        children.setAll(pmc.findChildren().results().stream()
                                                    .map(cpm -> createButton(cpm, prototypeStyleClass, group))
                                                    .collect(toList()));
      }
    
    @Nonnull
    private ToggleButton createButton (final @Nonnull PresentationModel pm,
                                       final @Nonnull ObservableList<String> baseStyleClass,
                                       final @Nonnull ToggleGroup group)
      {
        final ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setText(asOptional(pm, Displayable).map(d -> d.getDisplayName()).orElse(""));
        button.getStyleClass().addAll(baseStyleClass);
        button.getStyleClass().addAll(asOptional(pm, Styleable).map(s -> s.getStyles()).orElse(emptyList()));
        
        try 
          {
            binder.get().bind(button, pm.as(UserActionProvider).getDefaultAction());
          }
        catch (NotFoundException e)
          {
            // ok, no UserActionProvider
          }

        if (group.getSelectedToggle() == null)
          {
            group.selectToggle(button);
          }
        
        return button;
      }
    
    @Nonnull
    private static <T> Optional<T> asOptional (final @Nonnull As asObject, final Class<T> roleClass)
      {
        // can't use asOptional() since PresentationModel is constrained to Java 7
        // FIXME The shortest implementation doesn't work - see DefaultPresentationModel implementation of as()
        // It doesn't call as() with NotFoundBehaviour - it's probably a bug
//        return Optional.ofNullable(asObject.as(roleClass, throwable -> null));
          try
            {
              return Optional.of(asObject.as(roleClass));  
            }
          catch (AsException e)
            {
              return Optional.empty();
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
