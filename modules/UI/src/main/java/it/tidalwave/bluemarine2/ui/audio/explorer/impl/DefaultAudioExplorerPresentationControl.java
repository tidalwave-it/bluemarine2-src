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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.net.URL;
import javafx.application.Platform;
import it.tidalwave.dci.annotation.DciContext;
import it.tidalwave.util.Finder;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.role.EntityBrowser;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import it.tidalwave.bluemarine2.ui.commons.OpenAudioExplorerRequest;
import it.tidalwave.bluemarine2.ui.commons.OnDeactivate;
import it.tidalwave.bluemarine2.ui.commons.OnActivate;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.*;
import static it.tidalwave.bluemarine2.model.spi.PathAwareEntity._PathAwareEntity_;

/***********************************************************************************************************************
 *
 * The Control of the {@link AudioExplorerPresentation}.
 *
 * @stereotype  Control
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @DciContext @Slf4j
public class DefaultAudioExplorerPresentationControl implements AudioExplorerPresentationControlSpi
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

    private final UserAction navigateUpAction = UserAction.of(() -> navigateUp());

    private final AtomicReference<Optional<URL>> currentCoverArtUrl = new AtomicReference<>(Optional.empty());

    @Getter
    private final List<Entity> mediaItems = new ArrayList<>();

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
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */  void onDownloadComplete (final @ListensTo @Nonnull DownloadComplete notification)
      {
        log.info("onDownloadComplete({})", notification);

        if (currentCoverArtUrl.get().map(url -> url.equals(notification.getUrl())).orElse(false))
          {
            if (notification.getStatusCode() == 200) // FIXME
              {
                presentation.setCoverArt(Optional.of(notification.getCachedUri()));
              }
          }
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
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void selectBrowser (final @Nonnull EntityBrowser browser)
      {
        log.info("selectBrowser({})", browser);
        navigationStack.clear();
        populateItems(new FolderAndMemento(browser.getRoot(), Optional.empty()));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void navigateTo (final @Nonnull Entity newMediaFolder)
      {
        log.debug("navigateTo({})", newMediaFolder);
        navigationStack.push(new FolderAndMemento(currentFolder, Optional.of(presentation.getMemento())));
        populateItems(new FolderAndMemento(newMediaFolder, Optional.empty()));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void renderDetails (final @Nonnull String details)
      {
        presentation.renderDetails(details);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void clearDetails()
      {
        presentation.setCoverArt(Optional.empty());
        presentation.renderDetails("");
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void requestCoverArt (final @Nonnull Optional<URL> optionalCoverArtUrl)
      {
        log.debug("requestCoverArt({})", optionalCoverArtUrl);
        currentCoverArtUrl.set(optionalCoverArtUrl);
        optionalCoverArtUrl.ifPresent(url -> messageBus.publish(new DownloadRequest(url)));
      }

    /*******************************************************************************************************************
     *
     * Navigates up to the parent folder.
     *
     ******************************************************************************************************************/
    private void navigateUp()
      {
        log.debug("navigateUp()");
        populateItems(navigationStack.pop());
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void populateBrowsers()
      {
        log.debug("populateBrowsers()");

        // FIXME: in this case role injection doesn't work because browsers are pre-instantiated by Spring and not
        // in this context.
//        contextManager.runWithContext(this, new SimpleTask()
//          {
//            @Override
//            public Void run()
//              {
//                final PresentationModel pm = browsers.stream() // natively sorted by @OrderBy
//                                                     .map(o -> o.as(_Presentable_).createPresentationModel())
//                                                     .collect(toCompositePresentationModel());
//                presentation.populateBrowsers(pm);
//                selectBrowser(browsers.get(0));
//                return null;
//              }
//           });

        final PresentationModel pm = toCompositePresentationModel(browsers, o -> new EntityBrowserUserActionProvider(o));
        presentation.populateBrowsers(pm);
        selectBrowser(browsers.get(0));
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
        Platform.runLater(() -> navigateUpAction.enabled().set(!navigationStack.isEmpty()));
        Platform.runLater(() -> properties.folderNameProperty().setValue(getCurrentPathLabel()));
        final Finder<? extends Entity> finder = currentFolder.as(_SimpleComposite_).findChildren().withContext(this);
        mediaItems.clear();
        // mediaItems.addAll(finder.stream().filter(i -> i instanceof MediaItem).map(i -> (MediaItem)i).collect(toList
        // ()));
        mediaItems.addAll(finder.results());
        // Needs the cast for overloading ambiguity in the method signature
        final PresentationModel pm = toCompositePresentationModel((Iterable<Entity>)mediaItems);
        presentation.populateItems(pm, folderAndMemento.getMemento());
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
                .filter(i -> i.maybeAs(_PathAwareEntity_).map(p -> p.getParent().isPresent()).orElse(true))
                .filter(i -> i.maybeAs(_Displayable_).map(d -> true).orElse(false))
                .map(i -> i.maybeAs(_Displayable_).map(o -> o.getDisplayName()).orElse("???"))
                .collect(joining(" / "));
      }
  }
