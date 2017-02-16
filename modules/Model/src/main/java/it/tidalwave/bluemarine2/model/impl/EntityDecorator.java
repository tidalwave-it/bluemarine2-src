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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import lombok.Getter;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * This class decorates an existing {@link Entity} with additional roles.
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
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
        super(additionalRoles);
        this.delegate = delegate;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type)
      {
        return as(type, As.Defaults.throwAsException(type));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Collection<T> asMany (Class<T> type)
      {
        final List<T> result = new ArrayList<>(); // FIXME
        asOptional(type).ifPresent(e -> result.add(e));
        return result;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Optional<T> asOptional (final @Nonnull Class<T> type)
      {
        return Optional.ofNullable(as(type, throwable -> null));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type, final @Nonnull NotFoundBehaviour<T> notFoundBehaviour)
      {
        try
          {
            return super.as(type);
          }
        catch (AsException e1)
          {
            try
              {
                return delegate.as(type);
              }
            catch (AsException e2)
              {
                return notFoundBehaviour.run(e2);
              }
          }
      }
  }
