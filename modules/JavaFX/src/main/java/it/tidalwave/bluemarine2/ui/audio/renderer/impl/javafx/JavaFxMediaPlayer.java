/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.ui.audio.renderer.impl.javafx;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.time.Duration;
import java.nio.file.Path;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.ui.audio.renderer.spi.MediaPlayerSupport;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxMediaPlayer extends MediaPlayerSupport
  {
    private static final javafx.util.Duration SKIP_DURATION = javafx.util.Duration.seconds(1);
    
    @CheckForNull
    private Media media;

    @CheckForNull
    private javafx.scene.media.MediaPlayer mediaPlayer;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private final Runnable cleanup = () ->
      {
        log.debug(">>>> media reproduction finished");
        // FIXME: remove listener from currentTimeProperty
        mediaPlayer = null;
        statusProperty.setValue(Status.STOPPED);
      };

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void setMediaItem (final @Nonnull MediaItem mediaItem)
      throws Exception
      {
        log.info("setMediaItem({})", mediaItem);
        checkNotPlaying();
        this.mediaItem = mediaItem;
        final Path path = mediaItem.getPath().toAbsolutePath();
        log.debug("path:     {}", path);
        log.debug("metadata: {}", mediaItem.getMetadata());
        media = new Media(path.toUri().toString());
        statusProperty.set(Status.STOPPED);
        playTimeProperty.set(Duration.ZERO);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public synchronized void play()
      throws Exception
      {
        log.info("play()");
        checkNotPlaying();

        if ((mediaPlayer != null) && mediaPlayer.getStatus().equals(javafx.scene.media.MediaPlayer.Status.PAUSED))
          {
            mediaPlayer.play();
          }
        else
          {
            if (mediaPlayer != null)
              {
                mediaPlayer.dispose();
              }

            mediaPlayer = new javafx.scene.media.MediaPlayer(media);
            // FIXME: bidirectional bind to an expression?
            mediaPlayer.currentTimeProperty().addListener(
                    (ObservableValue<? extends javafx.util.Duration> observable,
                    javafx.util.Duration oldValue,
                    javafx.util.Duration newValue) ->
              {
                playTimeProperty.setValue(Duration.ofMillis((long)newValue.toMillis()));
              });

            mediaPlayer.play();
            mediaPlayer.setOnEndOfMedia(cleanup);
            mediaPlayer.setOnError(cleanup);
            mediaPlayer.setOnHalted(cleanup);
          }

        statusProperty.setValue(Status.PLAYING);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void stop()
      {
        log.info("stop()");
        
        if (mediaPlayer != null)
          {
            mediaPlayer.stop();
            statusProperty.setValue(Status.STOPPED);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void pause()
      {
        log.info("pause()");
        
        if (mediaPlayer != null)
          {
            mediaPlayer.pause();
            statusProperty.setValue(Status.PAUSED);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void rewind()
      {
        log.info("rewind()");
        
        if (mediaPlayer != null)
          {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(SKIP_DURATION));
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void fastForward()
      {
        log.info("fastForward()");
        
        if (mediaPlayer != null)
          {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(SKIP_DURATION));
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void checkNotPlaying()
      throws Exception
      {
        if ((mediaPlayer != null) && mediaPlayer.getStatus().equals(javafx.scene.media.MediaPlayer.Status.PLAYING))
          {
            throw new Exception("Already playing");
          }
      }
  }
