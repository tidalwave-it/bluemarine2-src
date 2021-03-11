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
package it.tidalwave.bluemarine2.model.impl.catalog.factory;

import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.Repository;

/***********************************************************************************************************************
 *
 * A factory for entities that populates their attributes out of a {@link BindingSet}.
 *
 * @stereotype  Factory
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface RepositoryEntityFactory
  {
    static interface EntityFactoryFunction<ENTITY> extends BiFunction<Repository, BindingSet, ENTITY>
      {
      }

    /*******************************************************************************************************************
     *
     * Instantiates an entity populating attributes from the given {@link BindingSet}.
     *
     * @param   <E>             the static type of the entity to instantiate
     * @param   repository      the repository we're querying
     * @param   entityClass     the dynamic type of the entity to instantiate
     * @param   bindingSet      the {@code BindingSet}
     * @return                  the instantiated entity
     *
     ******************************************************************************************************************/
    @Nonnull
    public <E> E createEntity (@Nonnull Repository repository,
                               @Nonnull Class<E> entityClass,
                               @Nonnull BindingSet bindingSet);
  }
