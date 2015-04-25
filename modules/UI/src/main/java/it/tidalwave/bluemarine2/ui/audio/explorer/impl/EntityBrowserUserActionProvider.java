/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.role.ui.spi.UserActionLambda;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.role.EntityBrowser;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
//@DciRole(datumType = EntityBrowser.class, context = DefaultAudioExplorerPresentationControl.class)
@Configurable @RequiredArgsConstructor
public class EntityBrowserUserActionProvider extends DefaultUserActionProvider
  {
    @Nonnull
    private final EntityBrowser entityBrowser;

    @Inject
    private AudioExplorerPresentationControlSpi control;
    
    @Override @Nonnull
    public UserAction getDefaultAction() 
      {
        return new UserActionLambda(()-> control.selectBrowser(entityBrowser));
      }
  }