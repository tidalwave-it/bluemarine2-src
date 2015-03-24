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
package it.tidalwave.bluemarine2.ui.mainscreen;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.role.ui.UserAction;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface MainScreenPresentation 
  {
    /*******************************************************************************************************************
     *
     * Binds the presentation with callbacks.
     * 
     * @param  mainMenuItems    the main menu items
     * @param  powerOffAction   the action that powers off the system
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull Collection<MainMenuItem> mainMenuItems, @Nonnull UserAction powerOffAction);
    
    /*******************************************************************************************************************
     *
     * Shows this presentation on the screen.
     *
     ******************************************************************************************************************/
    public void showUp();
  }
