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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static java.util.stream.Collectors.toList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class StatementManager
  {
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
    public void requestAdd (final @Nonnull Resource subject, final @Nonnull IRI predicate, final @Nonnull Value literal)
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
    public void requestAdd (final @Nonnull Model model)
      {
        requestAdd(new AddStatementsRequest(model.stream().collect(toList())))  ;
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
        log.info("onAddStatementsRequest({})", request);
        progress.incrementCompletedInsertions();
        persistence.runInTransaction(connection ->
          {
            request.getStatements().stream().forEach(_c(s -> connection.add(s)));
          });
      }
  }
