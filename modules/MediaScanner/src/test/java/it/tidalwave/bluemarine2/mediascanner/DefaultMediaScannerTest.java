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
package it.tidalwave.bluemarine2.mediascanner;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.n3.N3Writer;
import it.tidalwave.bluemarine2.model.impl.DefaultMediaFileSystem;
import it.tidalwave.util.test.FileComparisonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;
import org.openrdf.model.Namespace;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author fritz
 */
@Slf4j
public class DefaultMediaScannerTest
  {
    private ClassPathXmlApplicationContext context;
    
    private Repository repository;
    
    private RepositoryConnection connection;
    
    @BeforeMethod
    private void prepareTest() 
      throws RepositoryException
      {
        final String s = "classpath:/META-INF/DefaultMediaScannerTestBeans.xml";
        context = new ClassPathXmlApplicationContext(s);
        
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
        
        connection = repository.getConnection();
      }

    @Test
    public void testScan() 
      throws RDFHandlerException, IOException, RepositoryException
      {
        final DefaultMediaScanner underTest = new DefaultMediaScanner(connection);
        final DefaultMediaFileSystem mediaFileSystem = new DefaultMediaFileSystem();

        underTest.process(mediaFileSystem.getRoot());
        connection.commit();
        
        final File actualFile = new File("target/test-results/model.n3");
        final File expectedFile = new File("src/test/resources/expected-results/model.n3");
        dumpModel(actualFile);
        // FIXME: OOM
        FileComparisonUtils.assertSameContents(expectedFile, actualFile);
      }
    
    private void dumpModel (final @Nonnull File file)
      throws RDFHandlerException, FileNotFoundException, RepositoryException 
      {
        file.getParentFile().mkdirs();
        final PrintWriter pw = new PrintWriter(file);
        final RDFHandler writer = new SortingRDFHandler(new N3Writer(pw));
        
        for (final Namespace namespace : connection.getNamespaces().asList())
          {
            writer.handleNamespace(namespace.getPrefix(), namespace.getName());
          }

        writer.handleNamespace("bmmo", "http://bluemarine.tidalwave.it/2015/04/mo/");
        writer.handleNamespace("dc",   "http://purl.org/dc/elements/1.1/");
        writer.handleNamespace("foaf", "http://xmlns.com/foaf/0.1/");
        writer.handleNamespace("mo",   "http://purl.org/ontology/mo/");
        writer.handleNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        writer.handleNamespace("xs",   "http://www.w3.org/2001/XMLSchema#");

        connection.export(writer);
        connection.close();
      }
  }
