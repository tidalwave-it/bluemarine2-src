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
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import it.tidalwave.util.ProcessExecutor;
import it.tidalwave.util.spi.DefaultProcessExecutor;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer;
import it.tidalwave.util.ProcessExecutor.ConsoleOutput.Listener;
import javax.annotation.CheckForNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class Mpg123MediaPlayer implements MediaPlayer
  {
    // Frame#  3255 [ 8906], Time: 01:25.02 [03:52.66]
    private static final String FRAME_REGEX = "^Frame# *([0-9]+) *\\[ *([0-9]+)\\], *Time: *([.:0-9]+) *\\[([.:0-9]+)\\].*$";
    
    // 01:25.02
    private static final String PLAY_TIME_REGEX = "([0-9]{2}):([0-9]{2})\\.([0-9]{2})";

    // [4:05] Decoding of 06 The Moon Is A Harsh Mistress.mp3 finished.
    private static final String FINISHED_REGEX = "^.*Decoding.*finished\\.$";
    
    private static final Pattern FRAME_PATTERN = Pattern.compile(FRAME_REGEX);

    private static final Pattern PLAY_TIME_PATTERN = Pattern.compile(PLAY_TIME_REGEX);
    
    private static final Pattern FINISHED_PATTERN = Pattern.compile(FINISHED_REGEX);

    private MediaItem mediaItem;
    
    private ProcessExecutor executor;
    
    private long latestPlayTimeUpdateTime = 0;
    
    private Duration playTime = Duration.ZERO;
    
    @Getter
    private final Property<Duration> playTimeProperty = new SimpleObjectProperty<>(Duration.ZERO);
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private final Listener mpg123ConsoleListener = (string) ->
      {
        final long now = System.currentTimeMillis();
  
        if (FINISHED_PATTERN.matcher(string).matches()) 
          {
            log.debug("finished playing");
//                    executor.waitForCompletion();
            executor = null;
            playTimeProperty.setValue(playTime);
          }
        
        else 
          {
            playTime = parsePlayTime(string);

            if ((playTime != null) && (now - latestPlayTimeUpdateTime > 1000))
              {
                log.trace(">>>> play time: {}", playTime);
                latestPlayTimeUpdateTime = now;
                playTimeProperty.setValue(playTime);
              }
          }
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
        if (executor != null)
          {
            throw new Exception("Already playing");  
          }
        
        this.mediaItem = mediaItem;  
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
        if (executor != null)
          {
            throw new Exception("Already playing");  
          }
        
        try 
          {
            final String path = mediaItem.getPath().toAbsolutePath().toString();
            executor = DefaultProcessExecutor.forExecutable("/usr/bin/mpg123") // FIXME
                                             .withArguments("-v", path)
                                             .start();
            executor.getStderr().setListener(mpg123ConsoleListener);
          } 
        catch (IOException e)
          {
            throw new Exception(e.toString()); // FIXME:
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public synchronized void stop() 
      throws MediaPlayer.Exception 
      {
        if (executor != null)
          {
            executor.stop();
            executor = null;
          }
      }

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @CheckForNull
    private static Duration parsePlayTime (final @Nonnull String string)
      {
        final Matcher frameMatcher = FRAME_PATTERN.matcher(string);

        if (frameMatcher.matches())
          {
            final Matcher playTimeMatcher = PLAY_TIME_PATTERN.matcher(frameMatcher.group(3));

            if (playTimeMatcher.matches())
              {
                final int minutes = Integer.parseInt(playTimeMatcher.group(1));
                final int seconds = Integer.parseInt(playTimeMatcher.group(2));
                final int hundreds = Integer.parseInt(playTimeMatcher.group(3));
                return Duration.ofMillis(hundreds * 10 + seconds * 1000 + minutes * 60 * 1000);
              }
          }
        
        return null;
      }
  }
