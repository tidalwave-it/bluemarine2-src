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
package it.tidalwave.bluemarine2.ui.audio.renderer.impl.javafx;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import javafx.scene.media.Media;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer;
import java.time.Duration;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxMediaPlayer implements MediaPlayer
  {
    private MediaItem mediaItem;
    
    private Media media;
    
    private javafx.scene.media.MediaPlayer mediaPlayer;
    
    @Getter
    private final Property<Duration> playTimeProperty = new SimpleObjectProperty<>(Duration.ZERO);
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void setMediaItem (final @Nonnull MediaItem mediaItem) 
      throws Exception 
      {
        this.mediaItem = mediaItem;
        final Path path = mediaItem.getPath().toAbsolutePath();
        log.info("path:     {}", path);
        log.info("metadata: {}", mediaItem.getMedatada());
        media = new Media(path.toUri().toString());
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void play() 
      throws Exception 
      {
        if (mediaPlayer != null)
          {
            // FIXME: remove all listeners
          }
        
        mediaPlayer = new javafx.scene.media.MediaPlayer(media);
        
        mediaPlayer.currentTimeProperty().addListener(
                (ObservableValue<? extends javafx.util.Duration> observable, 
                 javafx.util.Duration oldValue, 
                 javafx.util.Duration newValue) -> 
          {
            playTimeProperty.setValue(Duration.ofMillis((long)newValue.toMillis()));
          });
        
        mediaPlayer.play();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void stop() 
      throws Exception 
      {
        mediaPlayer.stop();
      }
  }
