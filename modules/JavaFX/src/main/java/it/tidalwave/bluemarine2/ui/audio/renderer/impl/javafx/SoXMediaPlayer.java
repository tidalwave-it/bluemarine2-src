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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import it.tidalwave.util.ProcessExecutor;
import it.tidalwave.util.ProcessExecutor.ConsoleOutput.Listener;
import it.tidalwave.util.spi.DefaultProcessExecutor;
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
public class SoXMediaPlayer extends MediaPlayerSupport
  {
    // In:9.18% 00:00:24.71 [00:04:04.48] Out:1.09M [  ====|====  ]        Clip:0    
    private static final String FRAME_REGEX = "^In.* ([:.0-9]+) \\[([:.0-9]+)\\].*$";
    
    // 00:00:24.71
    private static final String PLAY_TIME_REGEX = "([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{2})";

    // Done.
    private static final String FINISHED_REGEX = "^Done\\..*$";
    
    private static final Pattern FRAME_PATTERN = Pattern.compile(FRAME_REGEX);

    private static final Pattern PLAY_TIME_PATTERN = Pattern.compile(PLAY_TIME_REGEX);
    
    private static final Pattern FINISHED_PATTERN = Pattern.compile(FINISHED_REGEX);

    private ProcessExecutor executor;
    
    private long latestPlayTimeUpdateTime = 0;
    
    private Duration playTime = Duration.ZERO;
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private final Listener soxConsoleListener = (string) ->
      {
        if (FINISHED_PATTERN.matcher(string).matches()) 
          {
            log.debug("finished playing");
//                    executor.waitForCompletion();
            executor = null;
            playTimeProperty.setValue(playTime);
            statusProperty.setValue(Status.STOPPED);
          }
        
        else 
          {
            final long now = System.currentTimeMillis();
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
        checkNotPlaying();
        this.mediaItem = mediaItem;  
        playTime = Duration.ZERO;
        playTimeProperty.set(playTime);
        statusProperty.setValue(Status.STOPPED);
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
        checkNotPlaying();

        try 
          {
            if (statusProperty.getValue().equals(Status.PAUSED))
              {
                executor.send(" ");
              }
            else
              {
                final String path = mediaItem.getPath().toAbsolutePath().toString();
                executor = DefaultProcessExecutor.forExecutable("/usr/bin/play") // FIXME
                                                 .withArguments(path)
                                                 .start();
                executor.getStderr().setListener(soxConsoleListener);
              }
          }
        catch (IOException e) 
          {
            throw new Exception(e);
          }
                  
        statusProperty.setValue(Status.PLAYING);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public synchronized void stop() 
      {
        if (executor != null)
          {
            executor.stop();
            executor = null;
            playTime = Duration.ZERO;
            statusProperty.setValue(Status.STOPPED);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public synchronized void pause() 
      throws Exception 
      {
        if (statusProperty.getValue().equals(Status.PLAYING))
          {
            try 
              {
                // FIXME: doesn't work
                executor.send(" ");
                statusProperty.setValue(Status.PAUSED);
              }
            catch (IOException e) 
              {
                throw new Exception(e);
              }
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void rewind() 
      throws Exception 
      {
        if (statusProperty.getValue().equals(Status.PLAYING))
          {
            try 
              {
                // FIXME: doesn't work
                executor.send(",");
              }
            catch (IOException e) 
              {
                throw new Exception(e);
              }
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void fastForward() 
      throws Exception
      {
        if (statusProperty.getValue().equals(Status.PLAYING))
          {
            try 
              {
                // FIXME: doesn't work
                executor.send(".");
              }
            catch (IOException e) 
              {
                throw new Exception(e);
              }
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
            final Matcher playTimeMatcher = PLAY_TIME_PATTERN.matcher(frameMatcher.group(1));

            if (playTimeMatcher.matches())
              {
                final int hours    = Integer.parseInt(playTimeMatcher.group(1));
                final int minutes  = Integer.parseInt(playTimeMatcher.group(2));
                final int seconds  = Integer.parseInt(playTimeMatcher.group(3));
                final int hundreds = Integer.parseInt(playTimeMatcher.group(4));
                return Duration.ofMillis(hundreds * 10 + seconds * 1000 + minutes * 60 * 1000 + hours * 60 * 60 * 1000);
              }
          }
        
        return null;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private void checkNotPlaying()
      throws Exception
      {
        if (statusProperty.getValue().equals(Status.PLAYING))
          {
            throw new Exception("Already playing");  
          }
      }
  }
