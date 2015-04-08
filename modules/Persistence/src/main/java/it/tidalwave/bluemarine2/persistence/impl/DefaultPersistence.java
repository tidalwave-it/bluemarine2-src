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
package it.tidalwave.bluemarine2.persistence.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.openrdf.model.Namespace;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.n3.N3Writer;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.persistence.AddStatementsRequest;
import it.tidalwave.bluemarine2.persistence.DumpCompleted;
import it.tidalwave.bluemarine2.persistence.DumpRequest;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultPersistence 
  {
    private Repository repository;
    
    @Inject
    private MessageBus messageBus;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct // FIXME: use onPowerOn
    /* VisibleForTesting */ void initialize() 
      throws RepositoryException
      {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onAddStatementsRequest (final @ListensTo @Nonnull AddStatementsRequest request) 
      throws RepositoryException
      {
        log.info("onAddStatementsRequest({})", request);
        final long baseTime = System.nanoTime();
        final RepositoryConnection connection = repository.getConnection();
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
        connection.commit();
        connection.close();
        log.debug(">>>> done in {} ns", System.nanoTime() - baseTime);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onDumpRequest (final @ListensTo @Nonnull DumpRequest request)
      throws RDFHandlerException, FileNotFoundException, RepositoryException 
      {
        log.info("onDumpRequest({})", request);
        final File file = request.getPath().toFile();
        file.getParentFile().mkdirs();
        final PrintWriter pw = new PrintWriter(file);
        final RDFHandler writer = new SortingRDFHandler(new N3Writer(pw));
        final RepositoryConnection connection = repository.getConnection();
        
        for (final Namespace namespace : connection.getNamespaces().asList())
          {
            writer.handleNamespace(namespace.getPrefix(), namespace.getName());
          }

        writer.handleNamespace("bio",   "http://purl.org/vocab/bio/0.1/");
        writer.handleNamespace("bmmo",  "http://bluemarine.tidalwave.it/2015/04/mo/");
        writer.handleNamespace("dc",    "http://purl.org/dc/elements/1.1/");
        writer.handleNamespace("foaf",  "http://xmlns.com/foaf/0.1/");
        writer.handleNamespace("owl",   "http://www.w3.org/2002/07/owl#");
        writer.handleNamespace("mo",    "http://purl.org/ontology/mo/");
        writer.handleNamespace("rdfs",  "http://www.w3.org/2000/01/rdf-schema#");
        writer.handleNamespace("rel",   "http://purl.org/vocab/relationship/");
        writer.handleNamespace("vocab", "http://dbtune.org/musicbrainz/resource/vocab/");
        writer.handleNamespace("xs",    "http://www.w3.org/2001/XMLSchema#");

        connection.export(writer);
        connection.close();
        
        messageBus.publish(new DumpCompleted(request));
      }
  }
