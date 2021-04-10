/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.rest;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Path;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
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
            public ResponseEntity<String> request (@Nonnull final CachingRestClientSupport api,
                                                   @Nonnull final String url)
              throws IOException, InterruptedException
              {
                return api.requestFromNetwork(url);
              }
          },
        /** Never use the network. */
        ONLY_USE_CACHE
          {
            @Override @Nonnull
            public ResponseEntity<String> request (@Nonnull final CachingRestClientSupport api,
                                                   @Nonnull final String url)
              throws IOException
              {
                return api.requestFromCache(url).get();
              }
          },
        /** First try the cache, then the network. */
        USE_CACHE
          {
            @Override @Nonnull
            public ResponseEntity<String> request (@Nonnull final CachingRestClientSupport api,
                                                   @Nonnull final String url)
              throws IOException, InterruptedException
              {
                return api.requestFromCacheAndThenNetwork(url);
              }
          };

        @Nonnull
        public abstract ResponseEntity<String> request (@Nonnull CachingRestClientSupport api,
                                                        @Nonnull String url)
          throws IOException, InterruptedException;
      }

    private final RestTemplate restTemplate = new RestTemplate(); // FIXME: inject?

    @Getter @Setter
    private CacheMode cacheMode = CacheMode.USE_CACHE;

    @Getter @Setter
    private Path cachePath;

    @Getter @Setter
    private String accept = "application/xml";

    @Getter @Setter
    private String userAgent = "blueMarine II (fabrizio.giudici@tidalwave.it)";

    @Getter @Setter
    private long throttleLimit;

    @Getter @Setter @Nonnegative
    private int maxRetry = 3;

    @Getter @Setter
    private List<Integer> retryStatusCodes = List.of(503);

    private long latestNetworkAccessTimestamp = 0;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private static final ResponseErrorHandler IGNORE_HTTP_ERRORS = new ResponseErrorHandler()
      {
        @Override
        public boolean hasError (@Nonnull final ClientHttpResponse response)
          throws IOException
          {
            return false;
          }

        @Override
        public void handleError (@Nonnull final ClientHttpResponse response)
          throws IOException
          {
          }
      };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private final ClientHttpRequestInterceptor interceptor = (request, body, execution) ->
      {
        final HttpHeaders headers = request.getHeaders();
        headers.add(USER_AGENT, userAgent);
        headers.add(ACCEPT, accept);
        return execution.execute(request, body);
      };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public CachingRestClientSupport()
      {
        restTemplate.setInterceptors(singletonList(interceptor));
        restTemplate.setErrorHandler(IGNORE_HTTP_ERRORS);
      }

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
    protected ResponseEntity<String> request (@Nonnull final String url)
      throws IOException, InterruptedException
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
    private Optional<ResponseEntity<String>> requestFromCache (@Nonnull final String url)
      throws IOException
      {
        log.debug("requestFromCache({})", url);
        return ResponseEntityIo.load(cachePath.resolve(fixedPath(url)));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private synchronized ResponseEntity<String> requestFromNetwork (@Nonnull final String url)
      throws IOException, InterruptedException
      {
        log.debug("requestFromNetwork({})", url);
        ResponseEntity<String> response = null;

        for (int retry = 0; retry < maxRetry; retry++)
          {
            final long now = System.currentTimeMillis();
            final long delta = now - latestNetworkAccessTimestamp;
            final long toWait = Math.max(throttleLimit - delta, 0);

            if (toWait > 0)
              {
                log.info(">>>> throttle limit: waiting for {} msec...", toWait);
                Thread.sleep(toWait);
              }

            latestNetworkAccessTimestamp = now;
            response = restTemplate.getForEntity(URI.create(url), String.class);
            final int httpStatusCode = response.getStatusCodeValue();
            log.debug(">>>> HTTP status code: {}", httpStatusCode);

            if (!retryStatusCodes.contains(httpStatusCode))
              {
                break;
              }

            log.warn("HTTP status code: {} - retry #{}", httpStatusCode, retry + 1);
          }

//        log.trace(">>>> response: {}", response);
        return response;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<String> requestFromCacheAndThenNetwork (@Nonnull final String url)
      throws IOException, InterruptedException
      {
        log.debug("requestFromCacheAndThenNetwork({})", url);

        return requestFromCache(url).orElseGet(() ->
          {
            try
              {
                final ResponseEntity<String> response = requestFromNetwork(url);
                final int httpStatusCode = response.getStatusCodeValue();

                if (!retryStatusCodes.contains(httpStatusCode))
                  {
                    ResponseEntityIo.store(cachePath.resolve(fixedPath(url)), response, emptyList());
                  }

                return response;
              }
            catch (IOException | InterruptedException e)
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
    /* package */ static String fixedPath (@Nonnull final String url)
      {
        String s = url.replace("://", "/");
        int i = s.lastIndexOf('/');

        if (i >= 0)
          {
            final String lastSegment = s.substring(i + 1);

            if (lastSegment.length() > 255) // FIXME: and Mac OS X
              {
                try
                  {
                    final MessageDigest digestComputer = MessageDigest.getInstance("SHA1");
                    s = s.substring(0, i) + "/" + toString(digestComputer.digest(lastSegment.getBytes(UTF_8)));
                  }
                catch (NoSuchAlgorithmException e)
                  {
                    throw new RuntimeException(e);
                  }
              }
          }

        return s;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String toString (@Nonnull final byte[] bytes)
      {
        final StringBuilder builder = new StringBuilder();

        for (final byte b : bytes)
          {
            final int value = b & 0xff;
            builder.append(Integer.toHexString(value >>> 4)).append(Integer.toHexString(value & 0x0f));
          }

        return builder.toString();
      }
  }
