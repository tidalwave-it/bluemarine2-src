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
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Platform;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserAction8;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultPresentable;
import it.tidalwave.role.ui.spi.UserActionLambda;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.role.EntityBrowser;
import it.tidalwave.bluemarine2.ui.commons.OpenAudioExplorerRequest;
import it.tidalwave.bluemarine2.ui.commons.OnDeactivate;
import it.tidalwave.bluemarine2.ui.commons.RenderAudioFileRequest;
import it.tidalwave.bluemarine2.ui.commons.OnActivate;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static it.tidalwave.role.ui.Presentable.Presentable;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.toCompositePresentationModel;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier.AudioFileSupplier;
import static it.tidalwave.bluemarine2.model.role.Parentable.Parentable;

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
    private static class FolderAndMemento
      {
        @Nonnull
        private final Entity folder;

        @Nonnull
        private final Optional<Object> memento;
      }
    
    @Inject
    private AudioExplorerPresentation presentation;
    
    @Inject
    private MessageBus messageBus;
    
    @Inject
    private List<EntityBrowser> browsers;
    
    private Entity currentFolder;
    
    private final Stack<FolderAndMemento> navigationStack = new Stack<>();
    
    private final AudioExplorerPresentation.Properties properties = new AudioExplorerPresentation.Properties();
    
    private final UserAction8 navigateUpAction = new UserActionLambda(() -> navigateUp()); 
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct 
    /* VisibleForTesting */ void initialize()
      {
        presentation.bind(properties, navigateUpAction);  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onOpenAudioExplorerRequest (final @ListensTo @Nonnull OpenAudioExplorerRequest request)
      {
        log.info("onOpenAudioExplorerRequest({})", request);
        presentation.showUp(this);
        populateBrowsers();
        selectBrowser(browsers.get(0));
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
     * Deactivation is disabled (and acts as navigateUpAction) when the stack is not empty.
     *
     ******************************************************************************************************************/
    @OnDeactivate
    /* VisibleForTesting */ OnDeactivate.Result onDeactivate()
      {
        log.debug("onDeactivate()");
        
        if (navigationStack.isEmpty())
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
     * Selects a browser.
     * 
     * @param   browser     the browser
     *
     ******************************************************************************************************************/
    private void selectBrowser (final @Nonnull EntityBrowser browser)
      {
        log.info("selectBrowser({})", browser);
        navigationStack.clear();
        populateItems(new FolderAndMemento(browser.getRoot(), Optional.empty()));
      }
    
    /*******************************************************************************************************************
     *
     * Navigates to a new folder, saving the current folder to the stack.
     * 
     * @param   newMediaFolder  the new folder
     *
     ******************************************************************************************************************/
    private void navigateTo (final @Nonnull Entity newMediaFolder)
      {
        log.debug("navigateTo({})", newMediaFolder);
        navigationStack.push(new FolderAndMemento(currentFolder, Optional.of(presentation.getMemento())));
        populateItems(new FolderAndMemento(newMediaFolder, Optional.empty()));
      }
    
    /*******************************************************************************************************************
     *
     * Navigates up to the parent folder.
     *
     ******************************************************************************************************************/
    private void navigateUp() 
      {
        // TODO: assert not UI thread
        log.debug("navigateUp()");
        populateItems(navigationStack.pop());
      }
    
    /*******************************************************************************************************************
     *
     * Publishes a request to render an audio file.
     * 
     * @param   entity      the {@code Entity} referencing the audio file
     *
     ******************************************************************************************************************/
    private void requestRenderAudioFileFile (final @Nonnull Entity entity)
      {
        log.debug("requestRenderAudioFileFile({})", entity);
        messageBus.publish(new RenderAudioFileRequest(entity.as(AudioFileSupplier).getAudioFile()));    
      }

    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    private void populateBrowsers()
      {
        log.debug("populateBrowsers()");
        
        final PresentationModel pm = browsers.stream()
                                              .map(object -> new DefaultPresentable(object)
                                                                .createPresentationModel(rolesFor(object)))
                                              .collect(toCompositePresentationModel());
        presentation.populateBrowsers(pm);
      }
    
    /*******************************************************************************************************************
     *
     * Populates the presentation with the contents of a folder and selects an item.
     * 
     * @param   folderAndMemento    the folder and the presentation memento
     *
     ******************************************************************************************************************/
    private void populateItems (final @Nonnull FolderAndMemento folderAndMemento)
      {
        log.debug("populateItems({})", folderAndMemento);
        this.currentFolder = folderAndMemento.getFolder();
        // FIXME: shouldn't deal with JavaFX threads here
        Platform.runLater(() -> navigateUpAction.enabledProperty().setValue(!navigationStack.isEmpty()));
        Platform.runLater(() -> properties.folderNameProperty().setValue(getCurrentPathLabel()));
        // FIXME: waiting signal while loading
        final SimpleComposite8<Entity> composite = currentFolder.as(SimpleComposite8);
        // Uses native ordering provided by the Composite.
        final PresentationModel pm = composite.findChildren()
                                              .stream()
                                              .map(object -> object.asOptional(Presentable)
                                                                   .orElse(new DefaultPresentable(object))
                                                                   .createPresentationModel(rolesFor(object)))
                                              .collect(toCompositePresentationModel());
        presentation.populateItems(pm, folderAndMemento.getMemento());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    // FIXME: inject with @DciRole and @DciContext?
    @Nonnull
    private UserActionProvider rolesFor (final @Nonnull Entity entity)
      {
        final UserAction action = isComposite(entity) 
            ? new UserActionLambda(() -> navigateTo(entity)) 
            : new UserActionLambda(() -> requestRenderAudioFileFile(entity));
        
        return new DefaultUserActionProvider() // FIXME: new DefaultUserActionProvider(action)
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
    // FIXME: inject with @DciRole and @DciContext?
    @Nonnull
    private UserActionProvider rolesFor (final @Nonnull EntityBrowser entitySupplier)
      {
        final UserAction8 selectBrowser = new UserActionLambda(()-> selectBrowser(entitySupplier));
    
        return new DefaultUserActionProvider() // FIXME: new DefaultUserActionProvider(action)
          {
            @Override @Nonnull
            public UserAction getDefaultAction()
              {
                return selectBrowser;
              }
          };
      }
    
    /*******************************************************************************************************************
     *
     * Computes the label describing the current navigation path.
     * 
     ******************************************************************************************************************/
    @Nonnull
    private String getCurrentPathLabel()
      {
        return concat(navigationStack.stream().map(i -> i.getFolder()), of(currentFolder))
                .filter(i -> i.asOptional(Parentable).map(p -> p.hasParent()).orElse(true))
                .filter(i -> i.asOptional(Displayable).map(d -> true).orElse(false))
                .map(i -> i.asOptional(Displayable).map(o -> o.getDisplayName()).orElse("???"))
                .collect(joining(" / "));
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private static boolean isComposite (final @Nonnull Entity entity)
      {
        // FIXME: Composite doesn't work. Introduce Composite8?
        return entity.asOptional(SimpleComposite8).map(c -> true).orElse(false);
      }
  }
