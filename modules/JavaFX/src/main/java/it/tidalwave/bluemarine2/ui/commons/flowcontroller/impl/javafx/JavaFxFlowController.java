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
package it.tidalwave.bluemarine2.ui.commons.flowcontroller.impl.javafx;

import javax.annotation.Nonnull;
import java.util.Stack;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A JavaFX implementation of {@link FlowController}.
 * 
 * This implementation is very basic and suitable to simple applications without memory criticalities. It keeps a
 * stack of previous Presentations, without disposing their resources. A more efficient implementation should be similar
 * to the Android Activity life-cycle, which can jettison resources of past activities and recreate them on demand. But 
 * it would require life-cycle methods on the presentation interfaces. 
 * 
 * @stereotype  Controller
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxFlowController implements FlowController
  {
    @Setter
    private StackPane contentPane;
    
    // TODO: this implementation keeps all the history in the stack, thus wasting some memory.
    private final Stack<Node> presentationStack = new Stack<>();
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void showPresentation (final @Nonnull Object presentation)
      {
        log.info("showPresentation({})", presentation);

        // TODO: use an aspect - should be already done in some other project
        // FIXME: should not be needed, presentations should already run in JavaFX thread
        Platform.runLater(() ->
          {
            final Node newNode = (Node)presentation;
            
            if (presentationStack.isEmpty())
              {
                contentPane.getChildren().add(newNode);
              }
            else
              {
                final Node oldNode = presentationStack.peek();
                slide(newNode, oldNode, +1); 
              }
              
            presentationStack.push(newNode);
          });
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void dismissCurrentPresentation()
      {
        log.info("dismissCurrentPresentation()");
        
        final Node oldNode = presentationStack.pop();
        final Node newNode = presentationStack.peek();
        slide(newNode, oldNode, -1);              
      }

    /*******************************************************************************************************************
     *
     * Starts a "slide in/out" animation moving an old {@link Node} out and a new {@link Node} in, with a given 
     * direction.
     * 
     * @param   newNode     the {@code Node} to move in
     * @param   oldNode     the {@code Node} to move out
     * @param   direction   +1 for "forward" direction, -1 for "backward" direction
     *
     ******************************************************************************************************************/
    private void slide (final @Nonnull Node newNode, final @Nonnull Node oldNode, final int direction)
      {
        contentPane.getChildren().add(newNode);
        final double height = contentPane.getHeight();
        final KeyFrame start = new KeyFrame(Duration.ZERO,
                new KeyValue(newNode.translateYProperty(), height * direction),
                new KeyValue(oldNode.translateYProperty(), 0));
        final KeyFrame end = new KeyFrame(Duration.millis(200),
                new KeyValue(newNode.translateYProperty(), 0),
                new KeyValue(oldNode.translateYProperty(), -height * direction));
        final Timeline slideAnimation = new Timeline(start, end);
        slideAnimation.setOnFinished(event -> contentPane.getChildren().remove(oldNode));
        slideAnimation.play();
      }
  }
