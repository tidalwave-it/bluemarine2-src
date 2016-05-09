/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.openrdf.model.Statement;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class StatementManager
  {
    public class Builder
      {
        private final List<Statement> statements = new ArrayList<>();

        private final ValueFactory factory = ValueFactoryImpl.getInstance();

        @Nonnull
        public Builder with (final @Nonnull Resource subject,
                             final @Nonnull URI predicate,
                             final @Nonnull Value object)
          {
            return with(factory.createStatement(subject, predicate, object));
          }

        @Nonnull
        public Builder withOptional (final @Nonnull Optional<? extends Resource> optionalSubject,
                                     final @Nonnull URI predicate,
                                     final @Nonnull Value object)
          {
            return optionalSubject.map(subject -> with(subject, predicate, object)).orElse(this);
          }

        @Nonnull
        public Builder withOptional (final @Nonnull Resource subject,
                                     final @Nonnull URI predicate,
                                     final @Nonnull Optional<? extends Value> optionalObject)
          {
            return optionalObject.map(object -> with(subject, predicate, object)).orElse(this);
          }

        @Nonnull
        public Builder withOptional (final @Nonnull Optional<? extends Resource> optionalSubject,
                                     final @Nonnull URI predicate,
                                     final @Nonnull Optional<? extends Value> optionalObject)
          {
            return optionalObject.map(object -> withOptional(optionalSubject, predicate, object)).orElse(this);
          }

        @Nonnull
        public Builder with (final @Nonnull List<? extends Resource> subjects,
                             final @Nonnull URI predicate,
                             final @Nonnull Value object)
          {
            subjects.stream().forEach(subject -> with(subject, predicate, object)); // FIXME ?? this = withOptional(...)
            return this;
          }

        @Nonnull
        public Builder with (final @Nonnull List<? extends Resource> subjects,
                             final @Nonnull URI predicate,
                             final @Nonnull List<? extends Value> objects)
          {
            assert subjects.size() == objects.size();

            for (int i = 0; i < subjects.size(); i++)
              {
                with(subjects.get(i), predicate, objects.get(i)); // FIXME ?? this = withOptional(...)
              }

            return this;
          }

        @Nonnull
        public Builder with (final @Nonnull Resource subject,
                             final @Nonnull URI predicate,
                             final @Nonnull Stream<? extends Value> objects)
          {
            objects.forEach(object -> with(subject, predicate, object)); // FIXME ?? this = withOptional(...)
            return this;
          }

        @Nonnull
        public Builder withOptional (final @Nonnull Optional<? extends Resource> subject,
                                     final @Nonnull URI predicate,
                                     final @Nonnull Stream<? extends Value> objects)
          {
            if (subject.isPresent())
              {
                objects.forEach(object -> withOptional(subject, predicate, object)); // FIXME ?? this = withOptional(...)
              }

            return this;
          }

        @Nonnull
        public Builder with (final @Nonnull Statement statement)
          {
            statements.add(statement);
            return this;
          }

        @Nonnull
        public Builder with (final @Nonnull Optional<Builder> optionalBuilder)
          {
            optionalBuilder.ifPresent(builder -> statements.addAll(builder.statements));
            return this;
          }

        @Nonnull
        public void publish()
          {
            progress.incrementTotalInsertions();
            messageBus.publish(new AddStatementsRequest(Collections.unmodifiableList(statements)));
          }
      }

    @Inject
    private MessageBus messageBus;

    @Inject
    private Persistence persistence;

    @Inject
    private ProgressHandler progress;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void requestAdd (final @Nonnull Resource subject, final @Nonnull URI predicate, final @Nonnull Value literal)
      {
        requestAdd(new AddStatementsRequest(subject, predicate, literal));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void requestAdd (final @Nonnull List<Statement> statements)
      {
        requestAdd(new AddStatementsRequest(statements));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Builder requestAddStatements()
      {
        return new Builder();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void requestAdd (final @Nonnull AddStatementsRequest request)
      {
        progress.incrementTotalInsertions();
        messageBus.publish(request);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onAddStatementsRequest (final @ListensTo @Nonnull AddStatementsRequest request)
      throws RepositoryException
      {
        log.trace("onAddStatementsRequest({})", request);
        progress.incrementCompletedInsertions();
        persistence.runInTransaction(connection ->
          {
            request.getStatements().stream().forEach(s ->
              {
                try
                  {
                    connection.add(s);
                  }
                catch (RepositoryException e)
                  {
                    throw new RuntimeException(e); // FIXME
                  }
              });
          });
      }
  }
