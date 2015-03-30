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
package it.tidalwave.bluemarine2.ui.audio.renderer.impl;

import it.tidalwave.bluemarine2.model.MediaItem;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.UserActionRunnable;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.ui.commons.RenderMediaFileRequest;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import java.time.Duration;
import javafx.application.Platform;

/***********************************************************************************************************************
 *
 * The Control of the {@link AudioRendererPresentation}.
 * 
 * @stereotype  Control
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultAudioRendererPresentationControl 
  {
    @Inject
    private AudioRendererPresentation presentation;
    
    @Inject
    private MediaPlayer mediaPlayer;
    
    private final AudioRendererPresentation.Properties properties = new AudioRendererPresentation.Properties();
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private final UserAction rewindAction = new UserActionRunnable(() -> { });
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private final UserAction stopAction = new UserActionRunnable(() -> 
      {
        try
          {
            mediaPlayer.stop();
          }
        catch (MediaPlayer.Exception e) 
          {
            log.error("", e);
          }
      });
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private final UserAction playAction = new UserActionRunnable(() -> 
      { 
        try
          {
            mediaPlayer.play();
          }
        catch (MediaPlayer.Exception e) 
          {
            log.error("", e);
          }
      });
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private final UserAction fastForwardAction = new UserActionRunnable(() -> { });
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    /* VisibleForTesting */ void initialize()
      {
        presentation.bind(rewindAction, stopAction, playAction, fastForwardAction, properties);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onOpenAudioRendererRequest (final @ListensTo @Nonnull RenderMediaFileRequest request) 
      throws MediaPlayer.Exception
      {
        log.info("onOpenAudioRendererRequest({})", request);
        presentation.showUp();
        
        final MediaItem mediaItem = request.getMediaItem();
        final Metadata metadata = mediaItem.getMedatada();

        // FIXME: the control shouldn't mess with JavaFX stuff
        Platform.runLater(() ->
          {
            properties.getTitleProperty().setValue(metadata.get(TITLE).orElse(""));
            properties.getDurationProperty().setValue(metadata.get(DURATION).orElse(Duration.ZERO).toString());
            properties.getPlayTimeProperty().setValue("00:00");
            properties.getProgressProperty().setValue(0);
          });
        
        presentation.setMediaItem(mediaItem);
        mediaPlayer.setMediaItem(mediaItem);
      }
  }
