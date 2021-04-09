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
package it.tidalwave.util.spi;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.dci.annotation.DciRole;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A specialisation of {@link AsSupport} that deals with multiple roles of the same type by prioritising them; they
 * are ordered from most relevant to least relevant (where relevance is associated to specialisation, that is most
 * specialised roles, or roles associated via {@code @DciRole} to most specialised datum classes, are most relevant).
 *
 * FIXME: could be seen as a replacement to {@code AsSupport}?
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class PriorityAsSupport extends AsSupport implements As
  {
    @FunctionalInterface
    public static interface RoleProvider
      {
        @Nonnull
        public <T> Collection<T> findRoles (final @Nonnull Class<T> type);
      }

    @Nonnull
    private final Object owner; // for logging only

    @Nonnull
    private final Optional<RoleProvider> additionalRoleProvider;

    public PriorityAsSupport (final Object owner)
      {
        this(owner, Collections.emptyList());
      }

    public PriorityAsSupport (@Nonnull final Object owner, final @Nonnull Collection<Object> rolesOrFactories)
      {
        super(owner, rolesOrFactories);
        this.owner = owner;
        this.additionalRoleProvider = Optional.empty();
      }

    public PriorityAsSupport (@Nonnull final Object owner,
                              final @Nonnull RoleProvider additionalRoleProvider,
                              final @Nonnull Collection<Object> rolesOrFactories)
      {
        super(owner, rolesOrFactories);
        this.owner = owner;
        this.additionalRoleProvider = Optional.of(additionalRoleProvider);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     * Returned roles can be associated both to this type and to the delegate; the one with the higher priority is
     * returned. See {@link #asMany(java.lang.Class)} for further details.
     *
     * @see #asMany(java.lang.Class)
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
     * Returned roles can be associated both to this type and to the delegate; the one with the higher priority is
     * returned. See {@link #asMany(java.lang.Class)} for further details.
     *
     * @see #asMany(java.lang.Class)
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type, final @Nonnull NotFoundBehaviour<T> notFoundBehaviour)
      {
        return maybeAs(type).orElseGet(() -> notFoundBehaviour.run(new AsException(type)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     * Returned roles can be associated both to this type and to the delegate; the one with the higher priority is
     * returned. See {@link #asMany(java.lang.Class)} for further details.
     *
     * @see #asMany(java.lang.Class)
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Optional<T> maybeAs (final @Nonnull Class<T> type)
      {
        return asMany(type).stream().findFirst();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     * Returned roles can be associated both to this type and to the delegate; the one with the higher priority is
     * returned. The ones associated to this type come with higher priority (this makes sense, being this class a
     * decorator, specific roles could be associated to it). But given that the default implementation of asMany()
     * doesn't guarantee ant order (see TFT-192) there's something to take care of. Currently this method contains
     * some hardwired priority logics.
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> Collection<T> asMany (final @Nonnull Class<T> type)
      {
        log.trace("asMany({}) - {}", type, owner);
        final List<T> unordered = new ArrayList<>(super.asMany(type));
        additionalRoleProvider.ifPresent(r -> unordered.addAll(r.findRoles(type)));
        //
        // Need a kind of bubble sort, because:
        // a) the original sequence might have a meaning; for instance, additional roles added by
        //    additionalRoleProvider are appended and, generally, they should stay low in priority.
        // b) there is not always a well-defined way to define a relation order between the elements.
        //
        final List<T> result = new ArrayList<>();
        unordered.forEach(item -> addInOrder(result, item));
        log.trace(">>>> returning {}", result);

        return result;
      }

    /*******************************************************************************************************************
     *
     * Adds an item to the list, just before the first existing item which whose datum class is an instance of a
     * subclass of its datum class.
     *
     ******************************************************************************************************************/
    private static <T> void addInOrder (final @Nonnull List<T> list, final @Nonnull T item)
      {
        log.trace(">>>> add in order {} into {}", item, list);
        final Optional<T> firstAncestor = list.stream().filter(i -> isDatumAncestor(i, item)).findFirst();
        final int index = firstAncestor.map(list::indexOf).orElse(list.size());
        list.add(index, item);
        log.trace(">>>>>>>> add in order {} ", list);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private static <T> boolean isDatumAncestor (final @Nonnull T a, final @Nonnull T b)
      {
        final DciRole aBoundDatumClass = a.getClass().getAnnotation(DciRole.class);
        final DciRole bBoundDatumClass = b.getClass().getAnnotation(DciRole.class);

        if ((aBoundDatumClass != null) && (bBoundDatumClass != null))
          {
            return aBoundDatumClass.datumType()[0].isAssignableFrom(bBoundDatumClass.datumType()[0]); // FIXME: multiple classes?
          }

        return a.getClass().isAssignableFrom(b.getClass());
      }
  }
