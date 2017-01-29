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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.fasterxml.jackson.annotation.JsonView;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.finder.SourceAwareFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryMediaCatalog;
import it.tidalwave.util.Finder8;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.springframework.http.MediaType.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
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
        loadInMemoryCatalog(repository, Paths.get("target/test-classes/test-sets/musicbrainz-iTunes-fg-20161210-1.n3"));
//        System.setProperty("blueMarine2.source", "musicbrainz");
//        System.setProperty("blueMarine2.fallback", "embedded");
        catalog = new RepositoryMediaCatalog(repository);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @ResponseBody
    @JsonView(Profile.Master.class)
    @RequestMapping(value = "/record", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public RecordsJson getRecords (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                   final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        return new RecordsJson(finalized(catalog.findRecords(), source, fallback, RecordJson::new));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @ResponseBody
    @JsonView(Profile.Detail.class)
    @RequestMapping(value = "/record/{id}", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public RecordsJson getRecord (final @PathVariable String id,
                                  final @RequestParam(required = false, defaultValue = "embedded") String source,
                                  final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        return new RecordsJson(finalized(catalog.findRecords().withId(new Id(id)), source, fallback, RecordJson::new));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @ResponseBody
    @JsonView(Profile.Detail.class)
    @RequestMapping(value = "/record/{id}/track", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public TracksJson getRecordTracks (final @PathVariable String id,
                                       final @RequestParam(required = false, defaultValue = "embedded") String source,
                                       final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        return new TracksJson(finalized(catalog.findTracks().inRecord(new Id(id)), source, fallback, TrackJson::new));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @ResponseBody
    @JsonView(Profile.Master.class)
    @RequestMapping(value = "/track", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public TracksJson getTracks (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                 final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        return new TracksJson(finalized(catalog.findTracks(), source, fallback, TrackJson::new));
      }

    static interface Streamable<ENTITY, FINDER extends SourceAwareFinder<FINDER, ENTITY>> extends SourceAwareFinder<ENTITY, FINDER>
      {
        public Stream<ENTITY> stream();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>, JSON>
        List<JSON> finalized (final @Nonnull FINDER finder,
                              final @Nonnull String source,
                              final @Nonnull String fallback,
                              final @Nonnull Function<ENTITY, JSON> mapper)
      {
        final FINDER f = finder.importedFrom(new Id(source))
                                .withFallback(new Id(fallback));
        return ((Finder8<ENTITY>)f) // FIXME: hacky, because SourceAwareFinder does not extends Finder8
                     .stream()
                     .map(mapper)
                     .collect(toList());
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
