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
import java.util.function.Function;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URI;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.message.PersistenceInitializedNotification;
import it.tidalwave.bluemarine2.message.PowerOffNotification;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.model.ModelPropertyNames;
import it.tidalwave.bluemarine2.persistence.Persistence;
import it.tidalwave.bluemarine2.rest.ResponseEntityIo;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.EventBarrier;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.test.FileComparisonUtils8.assertSameContents;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;
import static it.tidalwave.bluemarine2.commons.test.TestUtilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class MusicResourcesControllerTest extends SpringTestSupport
  {
    private static final Path PATH_TEST_SETS = Paths.get("target/test-classes/test-sets");

    private ResourceServer server;

    private MessageBus messageBus;

    private String baseUrl;

    private EventBarrier<PersistenceInitializedNotification> barrier;

    private Function<String, String> postProcessor;

    private static final ResponseErrorHandler IGNORE_HTTP_ERRORS = new ResponseErrorHandler()
      {
        @Override
        public boolean hasError (final ClientHttpResponse response)
          throws IOException
          {
            return false;
          }

        @Override
        public void handleError (final ClientHttpResponse response)
          throws IOException
          {
          }
      };

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public MusicResourcesControllerTest()
      {
        super(LifeCycle.AROUND_CLASS,
              "classpath:META-INF/DciAutoBeans.xml",
              "classpath:META-INF/CommonsAutoBeans.xml",
              "classpath:META-INF/ModelAutoBeans.xml",
              "classpath:META-INF/PersistenceAutoBeans.xml",
              "classpath:META-INF/RestAutoBeans.xml",
              "classpath:META-INF/CatalogAutoBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeClass
    public void setup()
      throws Exception
      {
        server = context.getBean(ResourceServer.class);
        messageBus = context.getBean(MessageBus.class);
        final Persistence persistence = context.getBean(Persistence.class);

        barrier = new EventBarrier<>(PersistenceInitializedNotification.class, messageBus);

        final Map<Key<?>, Object> properties = new HashMap<>();
        final Path testSetPath = Paths.get(System.getProperty(PROPERTY_MUSIC_TEST_SETS_PATH));
        properties.put(ModelPropertyNames.ROOT_PATH, testSetPath.resolve("iTunes-aac-fg-20170131-1"));
        messageBus.publish(new PowerOnNotification(properties));
        Thread.sleep(2000);
        barrier.await();
        final Repository repository = persistence.getRepository();
        loadRepository(repository, PATH_TEST_SETS.resolve("model-iTunes-fg-20161210-1.n3"));
        loadRepository(repository, PATH_TEST_SETS.resolve("model-iTunes-aac-fg-20170131-1.n3"));
        loadRepository(repository, PATH_TEST_SETS.resolve("musicbrainz-iTunes-fg-20161210-1.n3"));

        baseUrl = server.absoluteUrl("");
        log.info(">>>> baseUrl: {}", baseUrl);
        postProcessor = s -> s.replaceAll(baseUrl, "http://<server>/");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @AfterClass
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
        restTemplate.setErrorHandler(IGNORE_HTTP_ERRORS);
        final Class<?> responseType = (url.contains("content") || url.contains("coverart")) ? byte[].class : String.class;
        // when
        final ResponseEntity<?> response = restTemplate.getForEntity(URI.create(baseUrl + url), responseType);
        // then
        final Path actualPath = PATH_TEST_RESULTS.resolve(expected);
        final Path expectedPath = PATH_EXPECTED_TEST_RESULTS.resolve(expected);
        ResponseEntityIo.store(actualPath, response, Arrays.asList("Last-Modified"), postProcessor);
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
    @DataProvider
    private static Object[][] resources()
      {
        return new Object[][]
          {
            // static resources
            { "dummy.txt",
              "dummy.txt.txt" },

            // dynamic resources
            { "rest/record",
              "records.json.txt" },

            { "rest/record/urn:bluemarine:record:eLWktOMBbcOWysVn6AW6kksBS7Q=",
              "record-eLWktOMBbcOWysVn6AW6kksBS7Q=.json.txt" },

            { "rest/record/urn:bluemarine:record:not_existing",
              "record-not_existing.json.txt" },

            { "rest/record/urn:bluemarine:record:eLWktOMBbcOWysVn6AW6kksBS7Q=/track",
              "record-eLWktOMBbcOWysVn6AW6kksBS7Q=-tracks.json.txt" },

            // FIXME: this is wrong, returning 200 + empty JSON while it should be 404
            { "rest/record/urn:bluemarine:record:not_existing/track",
              "record-not_existing-tracks.json.txt" },

            { "rest/record/urn:bluemarine:record:XoqvktWLs6mu64qGOxQ3NyPzXVY=/coverart",
              "record-XoqvktWLs6mu64qGOxQ3NyPzXVY=-coverart.jpg.txt" },

            { "rest/track",
              "tracks.json.txt" },

            { "rest/track/urn:bluemarine:track:Q9KPhUq1xN6VJRpV5gWsDkRYHUc=",
              "track-Q9KPhUq1xN6VJRpV5gWsDkRYHUc=.json.txt" },

            { "rest/track/urn:bluemarine:track:not_existing",
              "track-not_existing.json.txt" },

            { "rest/audiofile",
              "audiofiles.json.txt" },

            { "rest/audiofile/urn:bluemarine:audiofile:5lCKAUoE3IfmgttCE3a5U23gxQg=",
              "audiofile-5lCKAUoE3IfmgttCE3a5U23gxQg=.json.txt" },

            { "rest/audiofile/urn:bluemarine:audiofile:not_existing",
              "audiofile-not_existing.json.txt" },

            { "rest/audiofile/urn:bluemarine:audiofile:Nmd7Bm3DQ922WhPkJn5YD_i_eK4=/content",
              "audiofile-Nmd7Bm3DQ922WhPkJn5YD_i_eK4=-content.mp3.txt" },

            { "rest/audiofile/urn:bluemarine:audiofile:Nmd7Bm3DQ922WhPkJn5YD_i_eK4=/coverart",
              "audiofile-Nmd7Bm3DQ922WhPkJn5YD_i_eK4=-coverart.jpg.txt" },

            // missing coverart
            { "rest/audiofile/urn:bluemarine:audiofile:5lCKAUoE3IfmgttCE3a5U23gxQg=/coverart",
              "audiofile-5lCKAUoE3IfmgttCE3a5U23gxQg=-coverart.jpg.txt" },
          };
      }
  }
