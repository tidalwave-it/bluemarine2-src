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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class AddStatementManager 
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
    public void requestAddStatement (final @Nonnull Resource subject, 
                                     final @Nonnull URI predicate,
                                     final @Nonnull Value literal) 
      {
        requestAddStatements(new AddStatementsRequest(subject, predicate, literal));
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void requestAddStatements (final @Nonnull List<Statement> statements) 
      {
        requestAddStatements(new AddStatementsRequest(statements));
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void requestAddStatements (final @Nonnull AddStatementsRequest request) 
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
        persistence.runInTransaction((RepositoryConnection connection) -> 
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
