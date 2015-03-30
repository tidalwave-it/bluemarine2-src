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
import java.util.concurrent.atomic.AtomicLong;
import java.io.IOException;
import it.tidalwave.util.ProcessExecutor;
import it.tidalwave.util.spi.DefaultProcessExecutor;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer;
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
    private MediaItem mediaItem;
    
    private ProcessExecutor executor;
    
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
            final ProcessExecutor.ConsoleOutput stderr = executor.getStderr();
            final AtomicLong latest = new AtomicLong(0);
            
            // TODO: use bound properties for duration, play time, etc...
            stderr.setListener((string) ->
              {
                final long now = System.currentTimeMillis();
                
                if (now - latest.get() > 1000)
                  {
                    log.debug("{}", string);
                // Frame#  3255 [ 8906], Time: 01:25.02 [03:52.66]
                // "^Frame# *([0-9]+) *\\[ *([0-9]+)\\], *Time: *([.:0-9]+) *\\[([.:0-9]+)\\].*$"
                    latest.set(now);
                  }
            });
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
          }
      }
  }
