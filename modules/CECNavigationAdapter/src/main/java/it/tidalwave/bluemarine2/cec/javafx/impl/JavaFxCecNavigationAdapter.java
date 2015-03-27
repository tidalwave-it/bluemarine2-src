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
package it.tidalwave.bluemarine2.cec.javafx.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import it.tidalwave.cec.CecEvent;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.impl.javafx.JavaFxFlowController;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.cec.CecEvent.KeyCode.*;
import static it.tidalwave.cec.CecEvent.KeyDirection.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class JavaFxCecNavigationAdapter 
  {
    private final Map<CecEvent, Runnable> actionMap = new HashMap<>();
    
    public JavaFxCecNavigationAdapter()
      {
        actionMap.put(new CecEvent(CENTER, KEY_PRESSED),  () -> { emulateKeyPress(KeyCode.SPACE,  KEY_PRESSED);  });
        actionMap.put(new CecEvent(CENTER, KEY_RELEASED), () -> { emulateKeyPress(KeyCode.SPACE,  KEY_RELEASED); });
        actionMap.put(new CecEvent(LEFT,   KEY_PRESSED),  () -> { emulateKeyPress(KeyCode.LEFT,   KEY_PRESSED);  });
        actionMap.put(new CecEvent(LEFT,   KEY_RELEASED), () -> { emulateKeyPress(KeyCode.LEFT,   KEY_RELEASED); });
        actionMap.put(new CecEvent(RIGHT,  KEY_PRESSED),  () -> { emulateKeyPress(KeyCode.RIGHT,  KEY_PRESSED);  });
        actionMap.put(new CecEvent(RIGHT,  KEY_RELEASED), () -> { emulateKeyPress(KeyCode.RIGHT,  KEY_RELEASED); });
        actionMap.put(new CecEvent(UP,     KEY_PRESSED),  () -> { emulateKeyPress(KeyCode.UP,     KEY_PRESSED);  });
        actionMap.put(new CecEvent(UP,     KEY_RELEASED), () -> { emulateKeyPress(KeyCode.UP,     KEY_RELEASED); });
        actionMap.put(new CecEvent(DOWN,   KEY_PRESSED),  () -> { emulateKeyPress(KeyCode.DOWN,   KEY_PRESSED);  });
        actionMap.put(new CecEvent(DOWN,   KEY_RELEASED), () -> { emulateKeyPress(KeyCode.DOWN,   KEY_RELEASED); });
        actionMap.put(new CecEvent(EXIT,   KEY_PRESSED),  () -> { flowController.dismissCurrentPresentation();   });
      }
    
    @Inject
    private JavaFxFlowController flowController;
    
    /* VisibleForTesting */ void onCecEventReceived (final @Nonnull @ListensTo CecEvent event)
      {
        log.debug("onCecEventReceived({})", event);
        actionMap.getOrDefault(event, () -> { log.warn("unmapped event: {}", event); }).run();
      }
    
    private void emulateKeyPress (final @Nonnull KeyCode code, final @Nonnull CecEvent.KeyDirection direction)
      {
        Platform.runLater(() -> 
          {
            log.debug("emulateKeyPress({}, {})", code, direction);
            final FXRobot robot = FXRobotFactory.createRobot(flowController.getContentPane().getScene());
            
            switch (direction)
              {
                case KEY_PRESSED:
                    robot.keyPress(code);                        
                    break;
                    
                case KEY_RELEASED:
                    robot.keyRelease(code);                        
                    break;
              }
          });
      }
  }
