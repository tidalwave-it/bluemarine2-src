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
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.messagebus.MessageBus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.ui.util.BundleUtilities.*;

/***********************************************************************************************************************
 *
 * A proritized container of {@link UserAction}s to be placed on a menu.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class MainMenuItem 
  {
    @Getter @Nonnull
    private final int priority;
    
    @Nonnull
    private final Class<?> requestClass;
    
    @Inject
    private MessageBus messageBus;
    
    @Getter @Nonnull
    private final UserAction action;
    
    
    public MainMenuItem (final @Nonnull String displayNameKey, 
                         final @Nonnull String requestClassName,
                         final @Nonnull int priority)
      throws ClassNotFoundException
      {
        this.priority = priority;
        this.requestClass = Thread.currentThread().getContextClassLoader().loadClass(requestClassName);
        // FIXME: use MessageSendingUserAction
        this.action  = new UserActionSupport(displayableFromBundle(getClass(), displayNameKey)) 
          {
            @Override
            public void actionPerformed() 
              {
                try
                  {
                    messageBus.publish(requestClass.newInstance());
                  } 
                catch (InstantiationException | IllegalAccessException e) 
                  {
                    log.error("", e);
                  }
              }
          };
      }
  }
