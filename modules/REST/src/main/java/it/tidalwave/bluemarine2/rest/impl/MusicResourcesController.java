/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryMediaCatalog;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
@RestController @Slf4j
public class MusicResourcesController
  {
    private MediaCatalog catalog;

    @PostConstruct
    private void initialize()
      throws IOException
      {
        log.info("loading triples...");
        final Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        loadInMemoryCatalog(repository, Paths.get("target/test-classes/test-sets/model-iTunes-fg-20161210-1.n3"));
        catalog = new RepositoryMediaCatalog(repository);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/record", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RecordsJson getRecords()
      {
        return new RecordsJson(catalog.findRecords().stream().map(RecordJson::new).collect(toList()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/track", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TracksJson getTracks()
      {
        return new TracksJson(catalog.findTracks().stream().map(TrackJson::new).collect(toList()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull // FIXME: duplicated code
    private static void loadInMemoryCatalog (final @Nonnull Repository repository, final @Nonnull Path path)
      throws RDFParseException, IOException, RepositoryException
      {
        log.info("loadInMemoryCatalog(..., {})", path);

        try (final RepositoryConnection connection = repository.getConnection())
          {
            connection.add(path.toFile(), null, RDFFormat.N3);
            connection.commit();
          }
      }
  }
