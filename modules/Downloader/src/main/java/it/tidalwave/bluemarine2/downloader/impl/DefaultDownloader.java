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
package it.tidalwave.bluemarine2.downloader.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.client.cache.HeapResource;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadComplete.Origin;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponseInterceptor;
import static it.tidalwave.bluemarine2.downloader.DownloaderPropertyNames.CACHE_FOLDER_PATH;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultDownloader
  {
    @Inject
    private MessageBus messageBus;

    @Inject
    private SimpleHttpCacheStorage cacheStorage;

    private PoolingHttpClientConnectionManager connectionManager;

    private CacheConfig cacheConfig;

    private CloseableHttpClient httpClient;

private final HttpResponseInterceptor killCacheHeaders = (HttpResponse
 response, HttpContext context) ->
 {
 response.removeHeaders("Expires");
 response.removeHeaders("Pragma");
 response.removeHeaders("Cache-Control");
 response.addHeader("Expires", "Mon, 31 Dec 2099 00:00:00 GMT");
 };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    // FIXME: this is because there's a fix, and we explicitly save stuff in the cache - see below
    private final RedirectStrategy dontFollowRedirect = new RedirectStrategy()
      {
        @Override
        public boolean isRedirected (HttpRequest request, HttpResponse response, HttpContext context)
          throws ProtocolException
          {
            return false;
          }

        @Override
        public HttpUriRequest getRedirect (HttpRequest request, HttpResponse response, HttpContext context)
          throws ProtocolException
          {
            return null;
          }
      };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    /* VisibleForTesting */ void initialize()
      {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);

        cacheConfig = CacheConfig.custom()
                .setAllow303Caching(true)
                .setMaxCacheEntries(Integer.MAX_VALUE)
                .setMaxObjectSize(Integer.MAX_VALUE)
                .setSharedCache(false)
                .setHeuristicCachingEnabled(true)
                .build();
        httpClient = CachingHttpClients.custom()
                .setHttpCacheStorage(cacheStorage)
                .setCacheConfig(cacheConfig)
                .setRedirectStrategy(dontFollowRedirect)
                .setUserAgent("blueMarine (fabrizio.giudici@tidalwave.it)")
                .setDefaultHeaders(List.of(new BasicHeader("Accept", "application/n3")))
                .setConnectionManager(connectionManager)
                .addInterceptorFirst(killCacheHeaders) // FIXME: only if  explicitly configured
         .build();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPowerOnNotification (final @ListensTo @Nonnull PowerOnNotification notification)
      throws NotFoundException
      {
        log.info("onPowerOnNotification({})", notification);
        cacheStorage.setFolderPath(notification.getProperties().get(CACHE_FOLDER_PATH));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onDownloadRequest (final @ListensTo @Nonnull DownloadRequest request)
      throws URISyntaxException
      {
        try
          {
            log.info("onDownloadRequest({})", request);

            URL url = request.getUrl();

            for (;;)
              {
                final HttpCacheContext context = HttpCacheContext.create();
                @Cleanup final CloseableHttpResponse response = httpClient.execute(new HttpGet(url.toURI()), context);
                final byte[] bytes = bytesFrom(response);
                final CacheResponseStatus cacheResponseStatus = context.getCacheResponseStatus();
                log.debug(">>>> cacheResponseStatus: {}", cacheResponseStatus);

                final Origin origin = cacheResponseStatus.equals(CacheResponseStatus.CACHE_HIT) ? Origin.CACHE
                                                                                                : Origin.NETWORK;

                // FIXME: shouldn't do this by myself
                // FIXME: upon configuration, everything should be cached (needed for supporting integration tests)
                if (!origin.equals(Origin.CACHE) && List.of(200, 303).contains(response.getStatusLine().getStatusCode()))
                  {
                    final Date date = new Date();
                    final Resource resource = new HeapResource(bytes);
                    cacheStorage.putEntry(url.toExternalForm(),
                            new HttpCacheEntry(date, date, response.getStatusLine(), response.getAllHeaders(), resource));
                  }

                // FIXME: if the redirect were enabled, we could drop this check
                if (request.isOptionPresent(DownloadRequest.Option.FOLLOW_REDIRECT)
                    && response.getStatusLine().getStatusCode() == 303) // SEE_OTHER FIXME
                  {
                    url = new URL(response.getFirstHeader("Location").getValue());
                    log.info(">>>> following 'see also' to {} ...", url);
                  }
                else
                  {
                    messageBus.publish(new DownloadComplete(request.getUrl(),
                                                            response.getStatusLine().getStatusCode(),
                                                            bytes,
                                                            origin));
                    return;
                  }
              }
          }
        catch (IOException e)
          {
            log.error("{}: {}", request.getUrl(), e.toString());
            messageBus.publish(new DownloadComplete(request.getUrl(), -1, new byte[0], Origin.NETWORK));
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private byte[] bytesFrom (final @Nonnull HttpResponse response)
      throws IOException
      {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (response.getEntity() != null)
          {
            response.getEntity().writeTo(baos);
          }

        return baos.toByteArray();
      }
  }
