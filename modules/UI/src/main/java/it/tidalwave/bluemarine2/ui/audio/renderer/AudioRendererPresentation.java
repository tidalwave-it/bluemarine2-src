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
package it.tidalwave.bluemarine2.ui.audio.renderer;

import javax.annotation.Nonnull;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.bluemarine2.model.MediaItem;

/***********************************************************************************************************************
 *
 * The Presentation of the renderer of audio media files.
 * 
 * @stereotype  Presentation
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface AudioRendererPresentation 
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull UserAction rewindAction,
                      @Nonnull UserAction stopAction,
                      @Nonnull UserAction playAction,
                      @Nonnull UserAction fastForwardAction);
    
    /*******************************************************************************************************************
     *
     * Shows this presentation on the screen.
     *
     ******************************************************************************************************************/
    public void showUp();

    /*******************************************************************************************************************
     *
     * Sets the {@link MediaItem} to render.
     * 
     * @param   mediaItem   the {@code MediaItem}
     *
     ******************************************************************************************************************/
    public void setMediaItem (@Nonnull MediaItem mediaItem);
  }
