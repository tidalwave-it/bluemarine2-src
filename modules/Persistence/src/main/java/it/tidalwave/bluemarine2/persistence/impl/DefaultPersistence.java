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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openrdf.model.Namespace;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.n3.N3Writer;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.PowerOnNotification;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.persistence.Persistence;
import it.tidalwave.bluemarine2.persistence.PropertyNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultPersistence implements Persistence
  {
    @Getter
    private Repository repository;
        
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPowerOnNotification (final @ListensTo @Nonnull PowerOnNotification notification) 
      throws RepositoryException, IOException, RDFParseException
      {
        log.info("onPowerOnNotification({})", notification);
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
        
        try
          {
            final Path repositoryPath = notification.getProperties().get(PropertyNames.REPOSITORY_PATH);
            final RepositoryConnection connection = repository.getConnection();
            
            if (Files.exists(repositoryPath))
              {
                log.info("Importing repository from {} ...", repositoryPath);
                connection.add(repositoryPath.toFile(), null, RDFFormat.N3);
                connection.commit();
                connection.close();
              }
          }
        catch (NotFoundException e) 
          {
            log.warn("No repository path: operating in memory");
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    public void dump (final @Nonnull Path path)
      throws RDFHandlerException, FileNotFoundException, RepositoryException 
      {
        log.info("dump({})", path);
        final File file = path.toFile();
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
      }
  }
