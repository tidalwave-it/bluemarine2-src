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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import lombok.Getter;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * This class decorates an existing {@link Entity} with additional roles. Furthermore, roles annotated with
 * {@code @DciRole} can be implicitly associated with the {@code EntityDecorator} itself, or its subclasses.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@ToString
public class EntityDecorator extends EntityWithRoles
  {
    @Getter @Nonnull
    protected final Entity delegate;

    /*******************************************************************************************************************
     *
     * Creates a new instance given a delegate and additional roles.
     *
     * @param   delegate            the delegate
     * @param   additionalRoles     the additional roles
     *
     ******************************************************************************************************************/
    public EntityDecorator (final @Nonnull Entity delegate, final @Nonnull Object... additionalRoles)
      {
        super(delegate::asMany, additionalRoles);
        this.delegate = delegate;
      }
  }
