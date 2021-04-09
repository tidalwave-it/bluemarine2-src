/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.ui.audio.renderer;

import javax.annotation.Nonnull;
import java.time.Duration;
import javafx.beans.property.ObjectProperty;
import it.tidalwave.bluemarine2.model.MediaItem;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface MediaPlayer 
  {
    enum Status
      {
        STOPPED,
        PLAYING,
        PAUSED
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static class Exception extends java.lang.Exception
      {
        public Exception (@Nonnull final String message)
          {
            super(message);
          }

        public Exception (@Nonnull final Throwable cause)
          {
            super(cause);
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setMediaItem (@Nonnull MediaItem mediaItem)
      throws Exception;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void play()
      throws Exception;
            
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void stop()
      throws Exception;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void pause()
      throws Exception;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void rewind()
      throws Exception;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void fastForward()
      throws Exception;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public ObjectProperty<Duration> playTimeProperty();
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public ObjectProperty<Status> statusProperty();
  }
