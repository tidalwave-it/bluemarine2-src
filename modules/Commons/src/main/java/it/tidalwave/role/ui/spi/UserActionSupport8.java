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
package it.tidalwave.role.ui.spi;

import javax.annotation.Nonnull;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import it.tidalwave.role.ui.UserAction8;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public abstract class UserActionSupport8 extends UserActionSupport implements UserAction8 
  {
    @Getter @Accessors(fluent = true)
    private final BooleanProperty enabledProperty = new SimpleBooleanProperty(true);
    
    private final InvalidationListener invalidationListener = (Observable observable) -> 
      {
        log.info("invalidated {}", observable);
      };
    
    private final ChangeListener<Boolean> changeListener =
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> 
      {
        log.info("changed: {} {} ->{}", observable, oldValue, newValue);
        this.enabled().set(newValue);
      };
    
    public UserActionSupport8 (final @Nonnull Object... rolesOrFactories) 
      {
        super(rolesOrFactories);
        
        enabledProperty.addListener(changeListener);
        enabledProperty.addListener(invalidationListener);
    }
  }