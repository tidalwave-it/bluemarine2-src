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
package it.tidalwave.bluemarine2.ui.impl.javafx;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import it.tidalwave.bluemarine2.ui.mainscreen.MainMenuItem;
import it.tidalwave.bluemarine2.ui.mainscreen.impl.DefaultMainMenuItem;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class MainMenuBarController 
  {
    @Nonnull
    private final GridPane gpMainMenuBar;
    
    /*******************************************************************************************************************
     *
     * Discover the main menu items and properly populate the menu bar.
     *
     ******************************************************************************************************************/
    public void populate()
      {
        final ObservableList<ColumnConstraints> columnConstraints = gpMainMenuBar.getColumnConstraints();
        final ObservableList<Node> children = gpMainMenuBar.getChildren();
        
        columnConstraints.clear();
        children.clear();
        // TODO: read from DCI
        
        final Collection<MainMenuItem> mainMenuItems = new ArrayList<>();
        mainMenuItems.add(new DefaultMainMenuItem("Immagini"));
        mainMenuItems.add(new DefaultMainMenuItem("Musica"));
        mainMenuItems.add(new DefaultMainMenuItem("Filmati"));
        
        final AtomicInteger columnIndex = new AtomicInteger(0);
        
        mainMenuItems.forEach(menuItem ->
          {
            final ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / mainMenuItems.size());
            columnConstraints.add(column);
            final Button button = createButton(menuItem.as(Displayable).getDisplayName());
            GridPane.setConstraints(button, columnIndex.getAndIncrement(), 0); 

            button.setOnAction(event -> 
              {
                log.info("pressed {}", button.getText());
                // TODO: invoked controller
              });
            
            children.add(button);
          });
      }
    
    /*******************************************************************************************************************
     *
     * Create a {@code Button} for the menu bar.
     * 
     * @param   text    the label of the button
     * @return          the button
     *
     ******************************************************************************************************************/
    @Nonnull
    private Button createButton (final @Nonnull String text)
      {
        final Button button = new Button(text);
        GridPane.setHgrow(button, Priority.ALWAYS);
        GridPane.setVgrow(button, Priority.ALWAYS);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setPrefSize(999, 999); // fill
        button.getStyleClass().add("mainMenuButton");
        
        return button;
      }
  }
