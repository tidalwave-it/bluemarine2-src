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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Stack;
import javafx.application.Platform;
import it.tidalwave.util.As;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserAction8;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.UserActionLambda;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.EntitySupplier;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.Parentable;
import it.tidalwave.bluemarine2.ui.commons.OpenAudioExplorerRequest;
import it.tidalwave.bluemarine2.ui.commons.OnDeactivate;
import it.tidalwave.bluemarine2.ui.commons.RenderMediaFileRequest;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import it.tidalwave.bluemarine2.ui.commons.OnActivate;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static it.tidalwave.role.ui.Presentable.Presentable;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.toCompositePresentationModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The Control of the {@link AudioExplorerPresentation}.
 * 
 * @stereotype  Control
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultAudioExplorerPresentationControl 
  {
    @AllArgsConstructor @Getter @ToString
    private static class FolderAndSelection
      {
        @Nonnull
        private final Entity folder;

        @Nullable
        private final Integer selectedIndex;
      }
    
    @Inject
    private AudioExplorerPresentation presentation;
    
    @Inject
    private MessageBus messageBus;
    
    @Inject
    private EntitySupplier rootEntitySupplier;
    
    private Entity folder;
    
    private final Stack<FolderAndSelection> stack = new Stack<>();
    
    private final AudioExplorerPresentation.Properties properties = new AudioExplorerPresentation.Properties();
    
    private final UserAction8 upAction = new UserActionLambda(() -> navigateUp()); 
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct 
    /* VisibleForTesting */ void initialize()
      {
        presentation.bind(properties, upAction);  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onOpenAudioExplorerRequest (final @ListensTo @Nonnull OpenAudioExplorerRequest request)
      {
        log.info("onOpenAudioExplorerRequest({})", request);
        presentation.showUp(this);
        populateAndSelect(rootEntitySupplier.get(), 0);
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @OnActivate
    /* VisibleForTesting */ void onActivate()
      {
        presentation.focusOnMediaItems();
      }
    
    /*******************************************************************************************************************
     *
     * The default action to go back should be disabled if the stack is not empty.
     *
     ******************************************************************************************************************/
    @OnDeactivate
    /* VisibleForTesting */ OnDeactivate.Result onDeactivate()
      {
        log.debug("onDeactivate()");
        
        if (stack.isEmpty())
          {  
            return OnDeactivate.Result.PROCEED;  
          }  
        else
          {
            navigateUp();
            return OnDeactivate.Result.IGNORE;
          }
      }
    
    /*******************************************************************************************************************
     *
     * Navigates to a new {@link MediaFolder}, saving the current folder to the stack.
     * 
     * @param   newMediaFolder  the new {@code MediaFolder}
     *
     ******************************************************************************************************************/
    private void navigateTo (final @Nonnull MediaFolder newMediaFolder)
      {
        log.debug("navigateTo({})", newMediaFolder);
        stack.push(new FolderAndSelection(folder, properties.selectedIndexProperty().get()));
        populateAndSelect(newMediaFolder, 0);
      }
    
    /*******************************************************************************************************************
     *
     * Navigates up to the parent {@link MediaFolder}.
     *
     ******************************************************************************************************************/
    private void navigateUp() 
      {
        // TODO: assert not UI thread
        log.debug("navigateUp()");
        final FolderAndSelection folderAndSelection = stack.pop();
        populateAndSelect(folderAndSelection.getFolder(), folderAndSelection.getSelectedIndex());
      }
    
    /*******************************************************************************************************************
     *
     * Populates the presentation with the contents of a {@link MediaFolder} and selects an item.
     * 
     * @param   folder     the {@code MediaFolder}
     * @param   selectedIndex   the index of the item to select
     *
     ******************************************************************************************************************/
    private void populateAndSelect (final @Nonnull Entity folder, final int selectedIndex)
      {
        log.debug("populateAndSelect({}, {})", folder, selectedIndex);
        this.folder = folder;
        // FIXME: shouldn't deal with JavaFX threads here
        Platform.runLater(() -> upAction.enabledProperty().setValue(!stack.isEmpty()));
        Platform.runLater(() -> properties.folderNameProperty().setValue(getCurrentPathLabel()));
        // FIXME: waiting signal while loading
        final SimpleComposite8<Entity> composite = folder.as(SimpleComposite8);
        final PresentationModel pm = composite.findChildren()
                                              .stream()
                                              .map(object -> object.as(Presentable)
                                                                   .createPresentationModel(rolesFor(object)))
                                              .sorted(new AudioComparator())
                                              .collect(toCompositePresentationModel());
        presentation.populateAndSelect(pm, selectedIndex);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    // FIXME: inject with @DciRole and @DciContext
    @Nonnull
    private UserActionProvider rolesFor (final @Nonnull As object)
      {
        final UserAction action = (object instanceof MediaFolder) 
            ? new UserActionLambda(() -> navigateTo(((MediaFolder)object))) 
            : new UserActionLambda(() -> messageBus.publish(new RenderMediaFileRequest((MediaItem)object)));
        
        return new DefaultUserActionProvider()
          {
            @Override @Nonnull
            public UserAction getDefaultAction()
              {
                return action;
              }
          };
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private String getCurrentPathLabel()
      {
//        return stack.peek().as(Displayable).getDisplayName();
        return concat(stack.stream().map(i -> i.getFolder()), of(folder))
                           .filter(i -> (i instanceof Parentable) ? ((Parentable)i).getParent() != null : false)
                           .map(folder -> folder.as(Displayable).getDisplayName())
                           .collect(joining(" / "));
      }
  }
