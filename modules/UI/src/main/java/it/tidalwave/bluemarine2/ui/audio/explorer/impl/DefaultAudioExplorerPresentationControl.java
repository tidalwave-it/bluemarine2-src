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
import java.util.Stack;
import java.io.File;
import java.nio.file.Path;
import it.tidalwave.util.As;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.role.ui.spi.UserActionRunnable;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.impl.DefaultMediaFolder;
import it.tidalwave.bluemarine2.ui.commons.OpenAudioExplorerRequest;
import it.tidalwave.bluemarine2.ui.commons.RenderMediaFileRequest;
import it.tidalwave.bluemarine2.ui.audio.explorer.AudioExplorerPresentation;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static it.tidalwave.role.ui.Presentable.Presentable;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.toCompositePresentationModel;

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
    @Inject
    private AudioExplorerPresentation presentation;
    
    @Inject
    private MessageBus messageBus;
    
    private final Stack<MediaFolder> stack = new Stack<>();
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private final UserAction upAction = new UserActionSupport(new DefaultDisplayable("Up")) // FIXME: bundle
      {
        @Override
        public void actionPerformed() 
          {
            // TODO: assert not UI thread
            log.info("upAction.actionPerformed()");
            
            if (stack.size() > 1)
              {
                stack.pop();
                populateWith(stack.peek());
              }
          }
      };
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct 
    /* VisibleForTesting */ void initialize()
      {
        presentation.bind(upAction);  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onOpenAudioExplorerRequest (final @ListensTo @Nonnull OpenAudioExplorerRequest request)
      {
        log.info("onOpenAudioExplorerRequest({})", request);
        presentation.showUp();
        String s = "/Users/fritz/Personal/Music/iTunes/iTunes Music/Music"; // FIXME;
        
        if ("arm".equals(System.getProperty("os.arch")))
          {
            s = "/";
          }
        
        final Path path = new File(s).toPath();
        final MediaFolder mediaFolder = new DefaultMediaFolder(path);
        navigateTo(mediaFolder);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void navigateTo (final @Nonnull MediaFolder mediaFolder)
      {
        log.debug("navigateTo({})", mediaFolder);
        stack.push(mediaFolder);
        populateWith(mediaFolder);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void populateWith (final @Nonnull MediaFolder mediaFolder)
      {
        log.debug("populateWith({})", mediaFolder);
        // FIXME: waiting signal while loading
        final SimpleComposite8<As> composite = mediaFolder.as(SimpleComposite8);
        final PresentationModel pm = composite.findChildren()
                                              .stream()
                                              .map(object -> object.as(Presentable).createPresentationModel(actionProviderFor(object)))
                                              .collect(toCompositePresentationModel());
        presentation.populate(pm);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    // FIXME: inject with @DciRole and @DciContext
    @Nonnull
    private UserActionProvider actionProviderFor (final @Nonnull As object)
      {
        final UserAction action = (object instanceof MediaFolder) 
            ? new UserActionRunnable(() -> navigateTo(((MediaFolder)object))) 
            : new UserActionRunnable(() -> messageBus.publish(new RenderMediaFileRequest((MediaItem)object)));
        
        return new DefaultUserActionProvider()
          {
            @Override @Nonnull
            public UserAction getDefaultAction()
              {
                return action;
              }
          };
      }
  }
