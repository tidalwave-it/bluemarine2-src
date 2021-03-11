/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
import javafx.scene.robot.Robot;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.cec.CecEvent;
import it.tidalwave.cec.CecUserControlEvent;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.impl.javafx.JavaFxFlowController;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.cec.CecEvent.EventType.*;
import static it.tidalwave.cec.CecUserControlEvent.UserControlCode.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class JavaFxCecNavigationAdapter 
  {
    private final Map<CecEvent, Runnable> actionMap = new HashMap<>();
    
    public JavaFxCecNavigationAdapter()
      {
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  SELECT),       () -> keyPress(KeyCode.SPACE)     );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, SELECT),       () -> keyRelease(KeyCode.SPACE)   );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  LEFT),         () -> keyPress(KeyCode.LEFT)      );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, LEFT),         () -> keyRelease(KeyCode.LEFT)    );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  RIGHT),        () -> keyPress(KeyCode.RIGHT)     );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, RIGHT),        () -> keyRelease(KeyCode.RIGHT)   );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  UP),           () -> keyPress(KeyCode.UP)        );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, UP),           () -> keyRelease(KeyCode.UP)      );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  DOWN),         () -> keyPress(KeyCode.DOWN)      );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, DOWN),         () -> keyRelease(KeyCode.DOWN)    );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  PLAY),         () -> keyPress(KeyCode.PLAY)      );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, PLAY),         () -> keyRelease(KeyCode.PLAY)    );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  STOP),         () -> keyPress(KeyCode.STOP)      );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, STOP),         () -> keyRelease(KeyCode.STOP)    );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  PAUSE),        () -> keyPress(KeyCode.PAUSE)     );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, PAUSE),        () -> keyRelease(KeyCode.PAUSE)   );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  REWIND),       () -> keyPress(KeyCode.REWIND)    );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, REWIND),       () -> keyRelease(KeyCode.REWIND)  );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  FAST_FORWARD), () -> keyPress(KeyCode.FAST_FWD)  );
        actionMap.put(new CecUserControlEvent(USER_CONTROL_RELEASED, FAST_FORWARD), () -> keyRelease(KeyCode.FAST_FWD));
        
        actionMap.put(new CecUserControlEvent(USER_CONTROL_PRESSED,  EXIT),         
                                                                () -> flowController.tryToDismissCurrentPresentation());
      }
    
    @Inject
    private JavaFxFlowController flowController;
    
    /* VisibleForTesting */ void onCecUserControlEventReceived (final @Nonnull @ListensTo CecUserControlEvent event)
      {
        log.debug("onCecUserControlEventReceived({})", event);
        Platform.runLater(() -> 
          {
            actionMap.getOrDefault(event, () -> log.warn("unmapped event: {}", event)).run();
          });
      }
    
    private void keyPress (final @Nonnull KeyCode code)
      {
        log.debug("keyPress({})", code);
        final Robot robot = new Robot();
        robot.keyPress(code);              
      }
    
    private void keyRelease (final @Nonnull KeyCode code)
      {
        log.debug("keyRelease({})", code);
        final Robot robot = new Robot();
        robot.keyRelease(code);              
      }
  }
