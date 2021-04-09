/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.ui.mainscreen.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.messagebus.MessageBus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;

/***********************************************************************************************************************
 *
 * A prioritized container of {@link UserAction}s to be placed on a menu.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class MainMenuItem
  {
    @Getter
    private final int priority;

    @Inject
    private MessageBus messageBus;

    @Getter @Nonnull
    private final UserAction action;

    public MainMenuItem (final @Nonnull String displayNameKey,
                         final @Nonnull String requestClassName,
                         final int priority)
      throws ClassNotFoundException
      {
        this.priority = priority;
        final Class<?> requestClass = Thread.currentThread().getContextClassLoader().loadClass(requestClassName);
        // FIXME: use MessageSendingUserAction?
        this.action  = UserAction.of(() -> messageBus.publish(_s(requestClass::newInstance).get()),
                                     Displayable.fromBundle(getClass(), displayNameKey));
      }
  }
