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
package it.tidalwave.bluemarine2.ui.mainscreen.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.ui.commons.Intent;
import it.tidalwave.bluemarine2.ui.mainscreen.MainMenuItem;
import lombok.Delegate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A default implementation of {@link MainMenuItem}.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMainMenuItem implements MainMenuItem
  {
    @Getter @Nonnull
    private final int priority;
    
    @Nonnull
    private final String activityName;
    
    @Delegate
    private final AsSupport asSupport;
    
    private final Displayable displayable;
    
    @Inject
    private MessageBus messageBus;
    
    // FIXME: use MessageSendingUserAction
    private final UserAction userAction = new UserActionSupport() 
      {
        @Override
        public void actionPerformed() 
          {
            messageBus.publish(new Intent(activityName));
          }
      };
    
    private final UserActionProvider userActionProvider = new DefaultUserActionProvider()
      {
        @Override
        public UserAction getDefaultAction()
          {
            return userAction;
          }
      }; 
    
    public DefaultMainMenuItem (final @Nonnull String displayName, 
                                final @Nonnull String activityName,
                                final @Nonnull int priority)
      {
        this.priority = priority;
        this.activityName = activityName;
        displayable = new DefaultDisplayable(displayName);
        asSupport = new AsSupport(this, new Object[] { displayable, userActionProvider });
      }
  }
