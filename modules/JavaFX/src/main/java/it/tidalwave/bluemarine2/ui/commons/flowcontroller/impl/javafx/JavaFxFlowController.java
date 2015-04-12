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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import it.tidalwave.bluemarine2.ui.commons.OnActivate;
import it.tidalwave.bluemarine2.ui.commons.OnDeactivate;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A JavaFX implementation of {@link FlowController}.
 * 
 * This implementation is very basic and suitable to simple applications without memory criticalities. It keeps a
 stack of previous Presentations, without disposing their resources. A more efficient implementation should be similar
 to the Android Activity life-cycle, which can jettison resources of past activities and recreate them on demand. But 
 it would require life-cycle methods on the node interfaces. 
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
    @RequiredArgsConstructor @Getter @ToString
    static class NodeAndControl
      {
        @Nonnull
        private final Node node;
        
        @CheckForNull
        private final Object control;
      }
    
    @Getter @Setter
    private StackPane contentPane;
    
    // TODO: this implementation keeps all the history in the stack, thus wasting some memory.
    private final Stack<NodeAndControl> presentationStack = new Stack<>();
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void showPresentation (final @Nonnull Object presentation)
      {
        showPresentationImpl(presentation, null);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void showPresentation (final @Nonnull Object presentation, final @Nonnull Object control)
      {
        showPresentationImpl(presentation, control);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    private void showPresentationImpl (final @Nonnull Object presentation, final @Nullable Object control)
      {
        log.info("showPresentationImpl({}, {})", presentation, control);
        
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
                final Node oldNode = presentationStack.peek().getNode();
                slide(newNode, oldNode, +1); 
              }
              
            presentationStack.push(new NodeAndControl(newNode, control));
            notifyActivated(control);
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
        
        if (presentationStack.size() < 2)
          {
            // TODO: should ask for user confirmation
            powerOff();
          }
        else
          {
            Platform.runLater(() ->
              {
                log.debug(">>>> presentationStack: {}", presentationStack);
                final Node oldNode = presentationStack.pop().getNode();
                final Node newNode = presentationStack.peek().getNode();
                slide(newNode, oldNode, -1);              
                notifyActivated(presentationStack.peek().getControl());
              });
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void tryToDismissCurrentPresentation() 
      {
        log.info("tryToDismissCurrentPresentation()");
        
        try 
          {
             canDeactivate(presentationStack.peek().getControl(), () -> dismissCurrentPresentation());
          } 
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
          {
            log.error("", e);
          }
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void powerOff() 
      {
        log.info("Shutting down...");
        // TODO: fire a PowerOff event and wait for collaboration completion
        // TODO: in this case, the responsibility to fire PowerOn should be moved here
//            Platform.exit();
        System.exit(0); // needed, otherwise Spring won't necessarily shut down 
      }
    
    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    private void notifyActivated (final @CheckForNull Object control)
      {
        try 
          {
            log.debug("notifyActivated({})", control);
            final Method method = findAnnotatedMethod(control, OnActivate.class);
            
            if (method != null)
              {
                // FIXME: should run in a background process
                method.invoke(control);
              }
          } 
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }
      }
    
    /*******************************************************************************************************************
     *
     * If a Presentation Control is passed, it is inspected for a method annotated with {@link OnDeactivate}. If found, 
     * it is called. If it returns {@code false}, this method returns {@code false}.
     * 
     * @param   control     the Presentation Control
     *
     ******************************************************************************************************************/
    @Nonnull
    private void canDeactivate (final @CheckForNull Object control, final @Nonnull Runnable runnable)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
      {
        log.debug("canDeactivate({})", control);
        final Method method = findAnnotatedMethod(control, OnDeactivate.class);
        
        if (method == null)
          {
            runnable.run();
          }
        else
          {
            // FIXME: should run in a background process
            if (((OnDeactivate.Result)method.invoke(control)).equals(OnDeactivate.Result.PROCEED))
              {
                runnable.run();
              }
          }
      }

    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    @CheckForNull
    private static Method findAnnotatedMethod (final @CheckForNull Object object, 
                                               final @Nonnull Class<? extends Annotation> annotationClass)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
      {
        log.debug("findAnnotatedMethod({})", object, annotationClass);
        
        if (object != null)
          {
            for (final Method method : object.getClass().getDeclaredMethods())
              {
                if (method.getAnnotation(annotationClass) != null)
                  {
                    log.debug(">>>> found {} annotated method on {}", annotationClass, object);
                    method.setAccessible(true);
                    return method; 
                  }
              }
          }
          
        return null;
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
