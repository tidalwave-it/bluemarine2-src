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
import static it.tidalwave.cec.CecEvent.EventType.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class JavaFxCecNavigationAdapter 
  {
    private final Map<CecEvent, Runnable> map = new HashMap<>();
    
    public JavaFxCecNavigationAdapter()
      {
        map.put(new CecEvent(SELECT, USER_CONTROL_PRESSED),  () -> emulateKey(KeyCode.SPACE,  USER_CONTROL_PRESSED)  );
        map.put(new CecEvent(SELECT, USER_CONTROL_RELEASED), () -> emulateKey(KeyCode.SPACE,  USER_CONTROL_RELEASED) );
        map.put(new CecEvent(LEFT,   USER_CONTROL_PRESSED),  () -> emulateKey(KeyCode.LEFT,   USER_CONTROL_PRESSED)  );
        map.put(new CecEvent(LEFT,   USER_CONTROL_RELEASED), () -> emulateKey(KeyCode.LEFT,   USER_CONTROL_RELEASED) );
        map.put(new CecEvent(RIGHT,  USER_CONTROL_PRESSED),  () -> emulateKey(KeyCode.RIGHT,  USER_CONTROL_PRESSED)  );
        map.put(new CecEvent(RIGHT,  USER_CONTROL_RELEASED), () -> emulateKey(KeyCode.RIGHT,  USER_CONTROL_RELEASED) );
        map.put(new CecEvent(UP,     USER_CONTROL_PRESSED),  () -> emulateKey(KeyCode.UP,     USER_CONTROL_PRESSED)  );
        map.put(new CecEvent(UP,     USER_CONTROL_RELEASED), () -> emulateKey(KeyCode.UP,     USER_CONTROL_RELEASED) );
        map.put(new CecEvent(DOWN,   USER_CONTROL_PRESSED),  () -> emulateKey(KeyCode.DOWN,   USER_CONTROL_PRESSED)  );
        map.put(new CecEvent(DOWN,   USER_CONTROL_RELEASED), () -> emulateKey(KeyCode.DOWN,   USER_CONTROL_RELEASED) );
        map.put(new CecEvent(EXIT,   USER_CONTROL_PRESSED),  () -> flowController.dismissCurrentPresentation()       );
      }
    
    @Inject
    private JavaFxFlowController flowController;
    
    /* VisibleForTesting */ void onCecEventReceived (final @Nonnull @ListensTo CecEvent event)
      {
        log.debug("onCecEventReceived({})", event);
        map.getOrDefault(event, () -> log.warn("unmapped event: {}", event)).run();
      }
    
    private void emulateKey (final @Nonnull KeyCode code, final @Nonnull CecEvent.EventType eventType)
      {
        Platform.runLater(() -> 
          {
            log.debug("emulateKeyPress({}, {})", code, eventType);
            final FXRobot robot = FXRobotFactory.createRobot(flowController.getContentPane().getScene());
            
            switch (eventType)
              {
                case USER_CONTROL_PRESSED:
                    robot.keyPress(code);                        
                    break;
                    
                case USER_CONTROL_RELEASED:
                    robot.keyRelease(code);                        
                    break;
              }
          });
      }
  }
