/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.gracenote.api.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import it.tidalwave.bluemarine2.gracenote.api.GracenoteApi;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import static java.nio.charset.StandardCharsets.UTF_8;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultGracenoteApi implements GracenoteApi
  {
    private static final String SERVICE_URL_TEMPLATE = "https://c%s.web.cddbp.net/webapi/xml/1.0/";

    private final RestTemplate restTemplate = new RestTemplate(); // FIXME: inject?

    private String serviceUrl;

    @Getter @Setter
    private String userId;

    @Getter @Setter
    private String clientId;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    void initialize()
      {
        serviceUrl = String.format(SERVICE_URL_TEMPLATE, clientId);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public String queryAlbumToc()
      throws IOException
      {
        final String request = loadTemplate("query-album-toc.xml");
        log.trace(">>>> request: {}", request);
        final ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, request, String.class);
        log.trace("response: {}", response);
        return response.getBody();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public String queryAlbumFetch()
      throws IOException
      {
        final String request = loadTemplate("query-album-fetch.xml");
        log.trace(">>>> request: {}", request);
        final ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, request, String.class);
        log.trace("response: {}", response);
        return response.getBody();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private String loadTemplate (final @Nonnull String templateName)
      throws IOException
      {
        final ClassPathResource classPathResource = new ClassPathResource(templateName, DefaultGracenoteApi.class);

        try (final InputStream is = classPathResource.getInputStream())
          {
            final byte[] buffer  = new byte[(int)classPathResource.contentLength()];
            is.read(buffer);
            final String string = new String(buffer, UTF_8);
            return string.replace("@CLIENT_ID@", clientId)
                         .replace("@USER_ID@", userId);
          }
      }
  }
