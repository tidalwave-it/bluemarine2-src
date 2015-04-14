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
package it.tidalwave.bluemarine2.ui.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Key;
import it.tidalwave.util.PowerOnNotification;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.impl.javafx.JavaFxFlowController;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The JavaFX Delegate for the main application screen.
 * 
 * @stereotype  JavaFXDelegate
 * 
 * @author Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
public class JavaFXApplicationPresentationDelegate
  {
    @Inject @Nonnull
    private JavaFxFlowController flowController;
    
    @Inject @Nonnull
    private MessageBus messageBus;
    
    @FXML
    private GridPane gpGridPane;
    
    @FXML
    private StackPane spContent;
    
    private final DoubleProperty leftOverscan = new SimpleDoubleProperty(0);
    
    private final DoubleProperty rightOverscan = new SimpleDoubleProperty(0);
    
    private final DoubleProperty topOverscan = new SimpleDoubleProperty(0);
    
    private final DoubleProperty bottomOverscan = new SimpleDoubleProperty(0);

    /*******************************************************************************************************************
     *
     * When initialization is completed, binds the screen estate to the {@link FlowController} and fires a 
     * {@link PowerOnNotification}, so other actors can start working.
     *
     ******************************************************************************************************************/
    @FXML
    public void initialize()
      {
        log.info("initialize()");

        final ObservableList<ColumnConstraints> columns = gpGridPane.getColumnConstraints();
        final ObservableList<RowConstraints> rows = gpGridPane.getRowConstraints();
        columns.get(0).minWidthProperty().bind(leftOverscan);
        columns.get(0).maxWidthProperty().bind(leftOverscan);
        columns.get(2).minWidthProperty().bind(rightOverscan);
        columns.get(2).maxWidthProperty().bind(rightOverscan);
        rows.get(0).minHeightProperty().bind(topOverscan);
        rows.get(0).maxHeightProperty().bind(topOverscan);
        rows.get(2).minHeightProperty().bind(bottomOverscan);
        rows.get(2).maxHeightProperty().bind(bottomOverscan);
        
        // FIXME: this is hardwired for my TV set - must be configurable by JavaFXApplicationWithSplash
        // Or FlowController, repurposed as a UIController
        
        if ("arm".equals(System.getProperty("os.arch")))
          {
            leftOverscan.set(38);
            rightOverscan.set(38);
            topOverscan.set(22);
            bottomOverscan.set(22);
          }
        // END FIXME
        
        flowController.setContentPane(spContent);
        final Map<Key<?>, Object> properties = new HashMap<>();
        final Path configPath = getConfiguratonPath();
        log.info("configPath is {}", configPath);
        final Path repositoryPath = configPath.resolve("repository.n3");
        properties.put(it.tidalwave.bluemarine2.persistence.PropertyNames.REPOSITORY_PATH, repositoryPath);
        properties.put(it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH, configPath);
        messageBus.publish(new PowerOnNotification(properties));        
      }    
    
    /*******************************************************************************************************************
     *
     * Binds the BACK_SPACE key to backward navigation.
     * 
     * @param   event   the key event
     *
     ******************************************************************************************************************/
    @FXML
    public void onKeyReleased (final @Nonnull KeyEvent event)
      {
        if (event.getCode().equals(KeyCode.BACK_SPACE))
          {
            log.debug("onKeyReleased({})", event);
            flowController.tryToDismissCurrentPresentation();
          }
      }
    
    /*******************************************************************************************************************
     * 
     * 
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private Path getConfiguratonPath()
      {
        String s = System.getProperty("user.home", "/");
        final String osName = System.getProperty("os.name").toLowerCase();
        
        switch (osName)
          {
            case "linux":
                s += "/.blueMarine2";
                break;
                
            case "mac os x":
                s += "/Library/Application Support/blueMarine2";
                break;
                
            case "windows":
                s += "/.blueMarine2";
                break;
          }
        
        return Paths.get(s);
      }
  }
