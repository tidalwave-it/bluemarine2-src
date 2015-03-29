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
import it.tidalwave.role.ui.UserAction;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * Wraps a {@link Runnable} into a {@link UserAction}. It's useful with Java 8 for shorter code (using composition with
 * a lambda expression in place of subclassing an inner class.
 * 
 * FIXME: Merge to UserActionSupport.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public final class UserActionRunnable extends UserActionSupport
  {
    @Nonnull
    private final Runnable runnable;
    
    @Override
    public void actionPerformed() 
      {
        runnable.run();
      }
  }
