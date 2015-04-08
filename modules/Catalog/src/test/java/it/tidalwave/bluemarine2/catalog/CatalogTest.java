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
package it.tidalwave.bluemarine2.catalog;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.bluemarine2.catalog.impl.RepositoryCatalog;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;
import static java.nio.file.Files.*;
import static it.tidalwave.util.test.FileComparisonUtils.*;
import javax.annotation.Nonnull;
import org.testng.annotations.DataProvider;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class CatalogTest 
  {
    private static final Path MODELS = Paths.get("src/test/resources");
            
    private static final Path TEST_RESULTS = Paths.get("target/test-results");
            
    private static final Path EXPECTED_TEST_RESULTS = Paths.get("src/test/resources/expected-results");
            
    private Repository repository;
    
    @Test(dataProvider = "p")
    public void dumpCatalog (final @Nonnull String catalogName, final @Nonnull String dumpName) 
      throws RepositoryException, IOException, RDFParseException, MalformedQueryException, QueryEvaluationException
      {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
        final File file = MODELS.resolve(catalogName).toFile();
//        final File file = new File("../MediaScanner/src/test/resources/expected-results/model.n3");
        final RepositoryConnection connection = repository.getConnection();
        
    // https://bitbucket.org/openrdf/alibaba/src/master/object-repository/
//        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
//        ObjectRepository oRepository = factory.createRepository(repository);        
               
        connection.add(file, null, RDFFormat.N3);
        connection.commit();
        connection.close();
        
        final Catalog catalog = new RepositoryCatalog(repository);
        final List<? extends MusicArtist> artists = catalog.findArtists().results();
        
        final Path actualResult = TEST_RESULTS.resolve(dumpName);
        final Path expectedResult = EXPECTED_TEST_RESULTS.resolve(dumpName);
        createDirectories(TEST_RESULTS);

        final PrintWriter pw = new PrintWriter(actualResult.toFile());
        pw.println("ARTISTS:\n");
        artists.forEach(artist -> pw.printf("%s\n", artist));
        
        // FIXME: missing those in collaborations 
        // FIXME: not correctly sorted in many cases
        
//        artists.forEach(artist -> 
        final MusicArtist artist = artists.get(0);
//          {
            pw.printf("\nTRACKS OF %s:\n", artist);
            artist.findTracks().stream().forEach(track -> pw.printf("  %s\n", track));
//          });
        
        pw.close();
        
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }
    
    @DataProvider(name = "p")
    private static Object[][] p()
      {
        return new Object[][]
          {
              { "tiny-model.n3",  "tiny-dump.txt"  },
              { "small-model.n3", "small-dump.txt" },
              { "large-model.n3", "large-dump.txt" }
          };
      }
  }
