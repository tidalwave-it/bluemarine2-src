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
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.Composite;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.role.ui.spi.UserActionLambda;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.Entity;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;

/***********************************************************************************************************************
 *
 * A default role for {@link Entity} instances in the context of {@link DefaultAudioExplorerPresentationControl} and
 * containing a {@link Composite} role that provides a default action for that navigates inside the composite contents.
 * 
 * FIXME: it should be only injected in entities with the Composite role, but the DCI Role library is not capable of
 * navigating inside data; in other words, @DciRole(datumType = ...) can only refer to a datum class.
 * 
 * As a workaround, since this role is a factory of {@link UserAction}s, it just refuses to create them in case there's
 * no Composite.
 * 
 * @stereotype  Role
 *  
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = Entity.class, context = DefaultAudioExplorerPresentationControl.class)
@RequiredArgsConstructor
public class CompositeEntityUserActionProvider extends DefaultUserActionProvider
  {
    @Nonnull
    private final Entity mediaFolder;
    
    @Nonnull
    private final AudioExplorerPresentationControlSpi control;
    
    @Override @Nonnull
    public UserAction getDefaultAction() 
      throws NotFoundException 
      {
        // test for the Composite role
        // FIXME: Composite doesn't work. Introduce Composite8?
        mediaFolder.asOptional(SimpleComposite8).orElseThrow(() -> new NotFoundException());
        return new UserActionLambda(() -> control.navigateTo(mediaFolder));
      }
  }
