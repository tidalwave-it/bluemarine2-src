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
import java.util.Collection;
import it.tidalwave.bluemarine2.ui.mainscreen.MainMenuItem;
import it.tidalwave.bluemarine2.ui.mainscreen.MainMenuItemProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DefaultMainMenuItemProvider implements MainMenuItemProvider
  {
    @Inject
    private ListableBeanFactory beanFactory;
    
    @Override @Nonnull
    public Collection<MainMenuItem> findMainMenuItems() 
      {
        // FIXME: sort by @Order
        return beanFactory.getBeansOfType(MainMenuItem.class).values().stream()
                .sorted(comparing(MainMenuItem::getPriority)).collect(toList());
      }
  }
