/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.Nonnull;
import java.util.Arrays;
import it.tidalwave.util.spi.PriorityAsSupport;
import it.tidalwave.bluemarine2.model.role.Entity;
import lombok.experimental.Delegate;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class EntityWithRoles implements Entity
  {
    @Delegate @Nonnull
    private final PriorityAsSupport asSupport;

    @Nonnull // for toString() only
    private final Object[] roles;

    public EntityWithRoles (final @Nonnull Object ... roles)
      {
        this.asSupport = new PriorityAsSupport(this, roles);
        this.roles = roles;
      }

    protected EntityWithRoles (final @Nonnull PriorityAsSupport.RoleProvider additionalRoleProvider,
                               final @Nonnull Object ... roles)
      {
        this.asSupport = new PriorityAsSupport(this, additionalRoleProvider, roles);
        this.roles = roles;
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("%s(%s)", getClass().getSimpleName(), Arrays.toString(roles));
      }
  }
