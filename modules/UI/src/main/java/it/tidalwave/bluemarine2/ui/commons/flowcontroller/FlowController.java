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
package it.tidalwave.bluemarine2.ui.commons.flowcontroller;

import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * The controller of the flow of presentations that appear on the screen.
 *  
 * @stereotype  Controller
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface FlowController 
  {
    /*******************************************************************************************************************
     *
     * Shows the given Presentation on the screen.
     * 
     * @param   presentation    the Presentation to show
     *
     ******************************************************************************************************************/
    public void showPresentation (@Nonnull Object presentation);
    
    /*******************************************************************************************************************
     *
     * Dismisses the Presentation currently rendered on the screen.
     *
     ******************************************************************************************************************/
    public void dismissCurrentPresentation();
  }
