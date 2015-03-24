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
import javax.inject.Inject;
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
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.util.NotFoundException;
import org.springframework.beans.factory.annotation.Configurable;
import static it.tidalwave.role.ui.UserActionProvider.UserActionProvider;
import javafx.application.Platform;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @RequiredArgsConstructor @Slf4j
public class MainMenuBarBinder 
  {
    @Nonnull
    private final GridPane gpMainMenuBar;

    @Inject
    private JavaFXBinder binder;
    
    /*******************************************************************************************************************
     *
     * Populates the menu bar.
     * 
     * @param   mainMenuItems   the menu items
     *
     ******************************************************************************************************************/
    public void bind (final @Nonnull Collection<MainMenuItem> mainMenuItems)
      {
        assert Platform.isFxApplicationThread();
        
        final ObservableList<ColumnConstraints> columnConstraints = gpMainMenuBar.getColumnConstraints();
        final ObservableList<Node> children = gpMainMenuBar.getChildren();

        columnConstraints.clear();
        children.clear();
        final AtomicInteger columnIndex = new AtomicInteger(0);

        mainMenuItems.forEach(menuItem ->
          {
            final ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / mainMenuItems.size());
            columnConstraints.add(column);
            final Button button = createButton();
            GridPane.setConstraints(button, columnIndex.getAndIncrement(), 0); 

            try 
              {
                binder.bind(button, menuItem.as(UserActionProvider).getDefaultAction());
              } 
            catch (NotFoundException e)   
              {
                log.warn("Can't bind UserAction: {}", e.toString());
                // no action
              }

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
    private Button createButton()
      {
        final Button button = new Button();
        GridPane.setHgrow(button, Priority.ALWAYS);
        GridPane.setVgrow(button, Priority.ALWAYS);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setPrefSize(999, 999); // fill
        button.getStyleClass().add("mainMenuButton");
        
        return button;
      }
  }
