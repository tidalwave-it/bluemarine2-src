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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.message.PersistenceInitializedNotification;
import it.tidalwave.bluemarine2.message.PowerOffNotification;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import it.tidalwave.bluemarine2.persistence.Persistence;
import it.tidalwave.bluemarine2.rest.ResponseEntityIo;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.EventBarrier;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class MusicResourcesControllerTest extends SpringTestSupport
  {
    private ResourceServer server;

    private MessageBus messageBus;

    private String baseUrl;

    private EventBarrier<PersistenceInitializedNotification> barrier;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public MusicResourcesControllerTest()
      {
        super("classpath:META-INF/DciAutoBeans.xml",
              "classpath:META-INF/CommonsAutoBeans.xml",
              "classpath:META-INF/ModelAutoBeans.xml",
              "classpath:META-INF/PersistenceAutoBeans.xml",
              "classpath:META-INF/RestAutoBeans.xml",
              "classpath:META-INF/CatalogAutoBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
//    @BeforeClass
    @BeforeMethod
    public void setup()
      throws Exception
      {
        server = context.getBean(ResourceServer.class);
        messageBus = context.getBean(MessageBus.class);
        final Persistence persistence = context.getBean(Persistence.class);

        barrier = new EventBarrier<>(PersistenceInitializedNotification.class, messageBus);

        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(ModelPropertyNames.ROOT_PATH, Paths.get("/tmp")); // FIXME
        messageBus.publish(new PowerOnNotification(properties));
        Thread.sleep(2000);
        barrier.await();
        final Repository repository = persistence.getRepository();
        loadInMemoryCatalog(repository, Paths.get("target/test-classes/test-sets/model-iTunes-fg-20161210-1.n3"));
        loadInMemoryCatalog(repository, Paths.get("target/test-classes/test-sets/musicbrainz-iTunes-fg-20161210-1.n3"));

        baseUrl = String.format("http://%s:%d", server.getIpAddress(), server.getPort());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
//    @AfterClass
    @AfterMethod
    public void shutdown()
      throws Exception
      {
        messageBus.publish(new PowerOffNotification());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "resources")
    public void must_serve_REST_resource (final @Nonnull String url, final @Nonnull String expected)
      throws IOException
      {
        // given
        final RestTemplate restTemplate = new RestTemplate();
        // when
        final ResponseEntity<String> response = restTemplate.getForEntity(URI.create(baseUrl + url), String.class);
        // then
        final Path actualPath = PATH_TEST_RESULTS.resolve(expected);
        final Path expectedPath = PATH_EXPECTED_TEST_RESULTS.resolve(expected);
        ResponseEntityIo.store(actualPath, response, Arrays.asList("Last-Modified"));
        assertSameContents(expectedPath, actualPath);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void test_service_publishing()
      throws InterruptedException
      {
        Thread.sleep(Integer.getInteger("delay", 0));
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

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] resources()
      {
        return new Object[][]
          {
            { "/rest/record",                                                          "records.json.txt"     },
            { "/rest/track",                                                           "tracks.json.txt"      },
            { "/rest/audiofile",                                                       "audiofiles.json.txt"  },
            { "/rest/audiofile/urn:bluemarine:audiofile:5lCKAUoE3IfmgttCE3a5U23gxQg=", "audiofile-urn:bluemarine:audiofile:5lCKAUoE3IfmgttCE3a5U23gxQg=.json.txt"  },
            { "/rest/record/urn:bluemarine:record:eLWktOMBbcOWysVn6AW6kksBS7Q=",       "record-eLWktOMBbcOWysVn6AW6kksBS7Q=.json.txt"          },
            { "/rest/record/urn:bluemarine:record:eLWktOMBbcOWysVn6AW6kksBS7Q=/track", "record-eLWktOMBbcOWysVn6AW6kksBS7Q=-tracks.json.txt"   },
            { "/index.xhtml",                                                          "index.xhtml.txt"      }
          };
      }
  }
