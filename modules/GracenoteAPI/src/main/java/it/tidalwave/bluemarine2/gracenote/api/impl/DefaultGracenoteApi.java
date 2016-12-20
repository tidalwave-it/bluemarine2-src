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

import it.tidalwave.bluemarine2.gracenote.api.Album;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import it.tidalwave.bluemarine2.gracenote.api.GracenoteApi;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultGracenoteApi implements GracenoteApi
  {
    public enum CacheMode
      {
        /** Always tries the network. */
        DONT_USE_CACHE,
        /** Never goes to the network. */
        ALWAYS_USE_CACHE,
        /** First try the cache, then the network. */
        POSSIBLY_USE_CACHE
      }

    private static final String SERVICE_URL_TEMPLATE = "https://c%s.web.cddbp.net/webapi/xml/1.0/";

    private final RestTemplate restTemplate = new RestTemplate(); // FIXME: inject?

    private String serviceUrl;

    @Getter @Setter
    private String userId;

    @Getter @Setter
    private String clientId;

    @Getter @Setter
    private CacheMode cacheMode = CacheMode.POSSIBLY_USE_CACHE;

    @Getter @Setter
    private Path cachePath;

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
    @Override @Nonnull
    public Optional<Album> findAlbumByToc (@Nonnull String offsets)
      throws IOException
      {
        return Optional.of(Album.of(queryAlbumToc(offsets)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Optional<Album> findAlbumByGnId (@Nonnull String gnId)
      throws IOException
      {
        return Optional.of(Album.of(queryAlbumFetch(gnId)));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    /* package */ ResponseEntity<String> queryAlbumToc (final @Nonnull String offsets)
      throws IOException
      {
        log.info("queryAlbumToc({})", offsets);
        final String cacheKey = "albumToc/" + offsets.replace(' ', '/');
        return request("query-album-toc.xml", cacheKey, "@OFFSETS@", offsets);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ResponseEntity<String> queryAlbumFetch (final @Nonnull String gnId)
      throws IOException
      {
        log.info("queryAlbumFetch({})", gnId);
        final String cacheKey = "albumFetch/" + gnId;
        return request("query-album-fetch.xml", cacheKey, "@GN_ID@", gnId);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<String> request (final @Nonnull String templateName,
                                            final @Nonnull String cacheKey,
                                            final @Nonnull String ... args)
      throws IOException
      {
        switch (cacheMode)
          {
            case ALWAYS_USE_CACHE:
                return requestFromCache(cacheKey).get();

            case DONT_USE_CACHE:
                return requestFromNetwork(templateName, args);

            case POSSIBLY_USE_CACHE:
                return requestFromCache(cacheKey).orElseGet(() ->
                  {
                    try
                      {
                        final ResponseEntity<String> response = requestFromNetwork(templateName, args);
                        ResponseEntityIo.store(cachePath.resolve(cacheKey), response, emptyList());
                        return response;
                      }
                    catch (IOException e)
                      {
                        throw new RuntimeException(e); // FIXME
                      }
                  });

            default:
                throw new IllegalStateException("Unexpected cache mode: " + cacheMode);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<ResponseEntity<String>> requestFromCache (final @Nonnull String cacheKey)
      throws IOException
      {
        return ResponseEntityIo.retrieve(cachePath.resolve(cacheKey).resolve("response.txt"));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<String> requestFromNetwork (final @Nonnull String templateName,
                                                       final @Nonnull String ... args)
      throws IOException
      {
        String template = loadTemplate(templateName).replace("@CLIENT_ID@", clientId)
                                                    .replace("@USER_ID@", userId);

        for (int i = 0; i < args.length; i += 2)
          {
            template = template.replace(args[i], args[i + 1]);
          }

        final String request = template;
        log.trace(">>>> request: {}", request);
        final ResponseEntity<String> response = restTemplate.postForEntity(serviceUrl, request, String.class);
        log.trace(">>>> response: {}", response);
        return response;
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
            return new String(buffer, UTF_8);
          }
      }
  }
