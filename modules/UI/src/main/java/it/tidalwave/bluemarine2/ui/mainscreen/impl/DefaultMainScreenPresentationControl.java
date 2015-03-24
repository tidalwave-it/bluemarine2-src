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

import javax.inject.Inject;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Collection;
import javafx.application.Platform;
import org.springframework.beans.factory.ListableBeanFactory;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.ui.commons.PowerOnNotification;
import it.tidalwave.bluemarine2.ui.mainscreen.MainScreenPresentation;
import it.tidalwave.bluemarine2.ui.mainscreen.MainScreenPresentationControl;
import lombok.extern.slf4j.Slf4j;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMainScreenPresentationControl implements MainScreenPresentationControl
  {
    @Inject
    private MainScreenPresentation presentation;
    
    @Inject
    private ListableBeanFactory beanFactory;
    
    private final UserAction powerOffAction = new UserActionSupport() 
      {
        @Override
        public void actionPerformed() 
          {
            // TODO: fire a PowerOff event and wait for collaboration completion
            Platform.exit();
          }
      };
            
    @PostConstruct
    /* @VisibleForTesting */ void initialize() 
      {
        final Collection<UserAction> mainMenuActions =  beanFactory.getBeansOfType(MainMenuItem.class)
                                                                   .values()
                                                                   .stream()
                                                                   .sorted(comparing(MainMenuItem::getPriority))
                                                                   .map(menuItem -> menuItem.getAction())
                                                                   .collect(toList());
        presentation.bind(mainMenuActions, powerOffAction);
      }
    
    /* @VisibleForTesting */ void onPowerOnNotification (final @Nonnull @ListensTo PowerOnNotification notification)
      {
        presentation.showUp();
      }
  }
