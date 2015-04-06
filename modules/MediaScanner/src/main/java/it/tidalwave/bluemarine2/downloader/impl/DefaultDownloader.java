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
package it.tidalwave.bluemarine2.downloader.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
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
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultDownloader 
  {
    @Inject
    private MessageBus messageBus;
       
    // FIXME: @Inject
    private final HttpCacheStorage cacheStorage = new SimpleHttpCacheStorage();
            
    private PoolingHttpClientConnectionManager connectionManager;
    
    private CacheConfig cacheConfig;
    
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
    private final HttpResponseInterceptor killCacheHeaders = (HttpResponse response, HttpContext context) ->
      {
        response.removeHeaders("Expires");
        response.removeHeaders("Pragma");
        response.removeHeaders("Cache-Control");
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

        // FIXME: still downloads lots of things because it finds cache is too old
        cacheConfig = CacheConfig.custom()
                .setAllow303Caching(true)
                .setMaxCacheEntries(Integer.MAX_VALUE)
                .setMaxObjectSize(Integer.MAX_VALUE)
                .setSharedCache(false)
                .setHeuristicCachingEnabled(true)
                .build();
      }
    
    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onDownloadRequest (final @ListensTo @Nonnull DownloadRequest request)
      throws IOException, URISyntaxException
      {
        log.info("onDownloadRequest({})", request);    
                
        URL url = request.getUrl();
        
//        @Cleanup FIXME
        final CloseableHttpClient httpClient = CachingHttpClients.custom()
                .setHttpCacheStorage(cacheStorage)
                .setCacheConfig(cacheConfig)
                .setRedirectStrategy(dontFollowRedirect)
                .setUserAgent("blueMarine (fabrizio.giudici@tidalwave.it)")
                .setDefaultHeaders(Arrays.asList(new BasicHeader("Accept", "application/n3")))
                .setConnectionManager(connectionManager)
                .addInterceptorFirst(killCacheHeaders) // FIXME: only if explicitly configured
                .build();

        boolean done = false;
        
        while (!done)
          {
            final HttpCacheContext context = HttpCacheContext.create();
            @Cleanup final CloseableHttpResponse response = httpClient.execute(new HttpGet(url.toURI()), context);
            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            response.getEntity().writeTo(baos);
            final byte[] bytes = baos.toByteArray();
            final CacheResponseStatus cacheResponseStatus = context.getCacheResponseStatus();
            log.debug(">>>> cacheResponseStatus: {}", cacheResponseStatus);
            
            // FIXME: shouldn't do this by myself
            if (!cacheResponseStatus.equals(CacheResponseStatus.CACHE_HIT))
              {
                final Date date = new Date();
                final Resource resource = new HeapResource(bytes);
                cacheStorage.putEntry(url.toExternalForm(), 
                        new HttpCacheEntry(date, date, response.getStatusLine(), response.getAllHeaders(), resource));
              }

            // FIXME: if the redirect were enabled, we could drop this check
            if (response.getStatusLine().getStatusCode() == 303) // SEE_OTHER FIXME
              {
                url = new URL(response.getFirstHeader("Location").getValue());
                log.info(">>>> following 'see also' to {} ...", url);
              }
            else
              {
                done = true;  
                messageBus.publish(new DownloadComplete(url, response.getStatusLine().getStatusCode(), bytes));
              }
          }
                
        // FIXME: be sure to fire a message even in case of Exception
      }
  }
