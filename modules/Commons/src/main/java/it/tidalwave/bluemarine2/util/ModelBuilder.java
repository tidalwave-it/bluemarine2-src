/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.TreeModel;

/***********************************************************************************************************************
 *
 * Unlike the similar class in RDF4J, this is thread-safe and can merge to similar objects.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@ThreadSafe
public class ModelBuilder
  {
    private final Model model = new TreeModel();

    @Nonnull private final Resource[] contexts;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ModelBuilder (@Nonnull final Resource ... contexts)
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
    public synchronized ModelBuilder with (@Nonnull final Resource subject,
                                           @Nonnull final IRI predicate,
                                           @Nonnull final Value object,
                                           @Nonnull final Resource... contexts)
      {
        model.add(subject, predicate, object, contexts);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final Resource subjext,
                                           @Nonnull final IRI predicate,
                                           @Nonnull final Optional<Value> optionalObject,
                                           @Nonnull final Resource... contexts)
      {
        return optionalObject.map(object -> ModelBuilder.this.with(subjext, predicate, object, contexts)).orElse(this);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder withOptional (@Nonnull final Optional<? extends Resource> optionalSubject,
                                                   @Nonnull final IRI predicate,
                                                   @Nonnull final Value object)
      {
        return optionalSubject.map(subject -> ModelBuilder.this.with(subject, predicate, object)).orElse(this);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder withOptional (@Nonnull final Resource subject,
                                                   @Nonnull final IRI predicate,
                                                   @Nonnull final Optional<? extends Value> optionalObject)
      {
        return optionalObject.map(object -> ModelBuilder.this.with(subject, predicate, object)).orElse(this);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder withOptional (@Nonnull final Optional<? extends Resource> optionalSubject,
                                                   @Nonnull final IRI predicate,
                                                   @Nonnull final Optional<? extends Value> optionalObject)
      {
        return optionalObject.map(object -> withOptional(optionalSubject, predicate, object)).orElse(this);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final List<? extends Resource> subjects,
                                           @Nonnull final IRI predicate,
                                           @Nonnull final Value object)
      {
        subjects.forEach(subject -> ModelBuilder.this.with(subject, predicate, object)); // FIXME ?? this = withOptional(...)
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final List<? extends Resource> subjects,
                                           @Nonnull final IRI predicate,
                                           @Nonnull final List<? extends Value> objects)
      {
        assert subjects.size() == objects.size();

        for (int i = 0; i < subjects.size(); i++)
          {
            ModelBuilder.this.with(subjects.get(i), predicate, objects.get(i)); // FIXME ?? this = withOptional(...)
          }

        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final Resource subject,
                                           @Nonnull final IRI predicate,
                                           @Nonnull final Stream<? extends Value> objects)
      {
        objects.forEach(object -> ModelBuilder.this.with(subject, predicate, object)); // FIXME ?? this = withOptional(...)
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder withOptional (@Nonnull final Optional<? extends Resource> subject,
                                                   @Nonnull final IRI predicate,
                                                   @Nonnull final Stream<? extends Value> objects)
      {
        if (subject.isPresent())
          {
            objects.forEach(object -> withOptional(subject, predicate, object)); // FIXME ?? this = withOptional(...)
          }

        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final Statement statement)
      {
        model.add(statement);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final Optional<ModelBuilder> optionalBuiilder)
      {
        optionalBuiilder.ifPresent(ModelBuilder.this::with);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final ModelBuilder other)
      {
        return ModelBuilder.this.with(other.toModel());
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final Model other)
      {
        other.forEach(model::add);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized ModelBuilder with (@Nonnull final List<ModelBuilder> others)
      {
        others.stream().map(ModelBuilder::toModel).forEach(m -> m.forEach(model::add));
        return this;
      }
  }
