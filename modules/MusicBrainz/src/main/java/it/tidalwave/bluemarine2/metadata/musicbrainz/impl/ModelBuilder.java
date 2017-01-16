/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Optional;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.TreeModel;

/***********************************************************************************************************************
 *
 * Unlike the similar class in RDF4J, this is thread-safe and can merge to similar objects.
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@ThreadSafe
public class ModelBuilder
  {
    private final Model model = new TreeModel();

    private final @Nonnull Resource[] contexts;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ModelBuilder (final @Nonnull Resource ... contexts)
      {
        this.contexts = contexts;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized Model toModel()
      {
        return new TreeModel(model);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (final @Nonnull Resource subjext,
                                           final @Nonnull IRI predicate,
                                           final @Nonnull Value object,
                                           final @Nonnull Resource... contexts)
      {
        model.add(subjext, predicate, object, contexts);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (final @Nonnull Resource subjext,
                                           final @Nonnull IRI predicate,
                                           final @Nonnull Optional<Value> optionalObject,
                                           final @Nonnull Resource... contexts)
      {
        return optionalObject.map(object -> with(subjext, predicate, object, contexts)).orElse(this);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder merge (final @Nonnull ModelBuilder other)
      {
        return merge(other.toModel());
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder merge (final @Nonnull Model other)
      {
        other.forEach(statement -> model.add(statement));
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder merge (final @Nonnull List<ModelBuilder> others)
      {
        others.stream().map(other -> other.toModel()).forEach(m -> m.forEach(statement -> model.add(statement)));
        return this;
      }
  }
