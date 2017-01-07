/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.rest;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import static java.util.Collections.emptyList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class CachingRestClientSupport
  {
    public enum CacheMode
      {
        /** Always use the network. */
        DONT_USE_CACHE
          {
            @Override @Nonnull
            public ResponseEntity<String> request (final @Nonnull CachingRestClientSupport api,
                                                   final @Nonnull String url)
              throws IOException
              {
                return api.requestFromNetwork(url);
              }
          },
        /** Never use the network. */
        ONLY_USE_CACHE
          {
            @Override @Nonnull
            public ResponseEntity<String> request (final @Nonnull CachingRestClientSupport api,
                                                   final @Nonnull String url)
              throws IOException
              {
                return api.requestFromCache(url).get();
              }
          },
        /** First try the cache, then the network. */
        USE_CACHE
          {
            @Override @Nonnull
            public ResponseEntity<String> request (final @Nonnull CachingRestClientSupport api,
                                                   final @Nonnull String url)
              throws IOException
              {
                return api.requestFromCacheAndThenNetwork(url);
              }
          };

        @Nonnull
        public abstract ResponseEntity<String> request (@Nonnull CachingRestClientSupport api,
                                                        @Nonnull String url)
          throws IOException;
      }

    private final RestTemplate restTemplate = new RestTemplate(); // FIXME: inject?

    @Getter @Setter
    private CacheMode cacheMode = CacheMode.USE_CACHE;

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
      }

    /*******************************************************************************************************************
     *
     * Performs a  web request.
     *
     * @return                  the response
     *
     ******************************************************************************************************************/
    @Nonnull
    protected ResponseEntity<String> request (final @Nonnull String url)
      throws IOException
      {
        log.debug("request({})", url);
        return cacheMode.request(this, url);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<ResponseEntity<String>> requestFromCache (final @Nonnull String url)
      throws IOException
      {
        log.debug("requestFromCache({})", url);
        return ResponseEntityIo.retrieve(cachePath.resolve(fixedPath(url)));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<String> requestFromNetwork (final @Nonnull String url)
      throws IOException
      {
        log.debug("requestFromNetwork({})", url);
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        log.trace(">>>> response: {}", response);
        return response;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<String> requestFromCacheAndThenNetwork (final @Nonnull String url)
      throws IOException
      {
        log.debug("requestFromCacheAndThenNetwork({})", url);

        return requestFromCache(url).orElseGet(() ->
          {
            try
              {
                final ResponseEntity<String> response = requestFromNetwork(url);
                ResponseEntityIo.store(cachePath.resolve(fixedPath(url)), response, emptyList());
                return response;
              }
            catch (IOException e)
              {
                throw new RestException(e); // FIXME
              }
          });
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    /* package */ static String fixedPath (final @Nonnull String url)
      {
        return url.replace("://", "/");
      }
  }
