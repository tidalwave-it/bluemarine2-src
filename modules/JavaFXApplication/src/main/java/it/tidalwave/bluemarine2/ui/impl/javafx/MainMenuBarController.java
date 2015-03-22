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
        final String[] labels = { "Immagini", "Musica", "Filmati" };
        
        for (int i = 0; i < labels.length; i++) 
          {
            final ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / labels.length);
            columnConstraints.add(column);
            final Button button = createButton(labels[i]);
            GridPane.setConstraints(button, i, 0); 

            button.setOnAction(event -> 
              {
                log.info("pressed {}", button.getText());
                // TODO: invoked controller
              });
            
            children.add(button);
          }
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
