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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.client.cache.FileResource;
import org.apache.http.impl.client.cache.HeapResource;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.DefaultHttpResponseWriter;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import java.nio.file.StandardCopyOption;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.cache.CacheResponseStatus;

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
       
    private final HttpCacheStorage cacheStorage = new HttpCacheStorage() 
      {
        @Override
        public void putEntry (final @Nonnull String key, final @Nonnull HttpCacheEntry entry)
          throws IOException 
          {
            try 
              {
                log.debug("putEntry({}, {})", key, entry);
                final Path cachePath = getCacheItemPath(new URL(key));
                Files.createDirectories(cachePath);
                
                final Path cacheHeadersPath = cachePath.resolve("headers");
                final Path cacheContentPath = cachePath.resolve("content");
                
                @Cleanup final OutputStream os = Files.newOutputStream(cacheHeadersPath, StandardOpenOption.CREATE);
                final HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
                final SessionOutputBufferImpl sob = new SessionOutputBufferImpl(metrics, 100);
                sob.bind(os);
                final DefaultHttpResponseWriter writer = new DefaultHttpResponseWriter(sob);

                final BasicHttpResponse response = new BasicHttpResponse(entry.getStatusLine());
                response.setHeaders(entry.getAllHeaders());
                writer.write(response);
                
                if (entry.getResource().length() > 0)
                  {
                    Files.copy(entry.getResource().getInputStream(), cacheContentPath, StandardCopyOption.REPLACE_EXISTING);
                  }
              }
            catch (HttpException e)
              {
                throw new IOException(e);
              }
          }

        @Override
        public HttpCacheEntry getEntry (final @Nonnull String key) 
          throws IOException 
          {
            log.debug("getEntry({})", key);
            final Path cachePath = getCacheItemPath(new URL(key));
            final Path cacheHeadersPath = cachePath.resolve("headers");
            final Path cacheContentPath = cachePath.resolve("content");
            
            if (!Files.exists(cacheHeadersPath))
              {
                log.trace(">>>> cache miss: {}", cacheHeadersPath);
                return null;  
              }
            
            try
              {
                @Cleanup final InputStream is = Files.newInputStream(cacheHeadersPath);
                final HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
                final SessionInputBufferImpl sib = new SessionInputBufferImpl(metrics, 100);
                sib.bind(is);
                final DefaultHttpResponseParser parser = new DefaultHttpResponseParser(sib);
                final HttpResponse response = parser.parse();
                final Date date = new Date(); // FIXME: force hit?
//                        new Date(Files.getLastModifiedTime(cacheHeadersPath).toMillis());
                final Resource resource = 
                        Files.exists(cacheContentPath) ? new FileResource(cacheContentPath.toFile()) : null;
                return new HttpCacheEntry(date, date, response.getStatusLine(), response.getAllHeaders(), resource);
              }
            catch (HttpException e)
              {
                throw new IOException(e);
              }
          }

        @Override
        public void removeEntry (final @Nonnull String key) 
          throws IOException
          {
            log.debug("removeEntry({})");
          }

        @Override
        public void updateEntry (final @Nonnull String key, final @Nonnull HttpCacheUpdateCallback callback)
          throws IOException, HttpCacheUpdateException 
          {
            log.debug("updateEntry({}, {})", key, callback);
          }
        
        @Nonnull
        private Path getCacheItemPath (final @Nonnull URL url)
          throws MalformedURLException 
          {
            final int port = url.getPort();
            final URL url2 = new URL(url.getProtocol(), url.getHost(), (port == 80) ? -1 : port, url.getFile());
            final Path cachePath = Paths.get(url2.toString().replaceAll(":", ""));
            return Paths.get("target/test-classes/download-cache").resolve(cachePath); // FIXME
          } 
      };
    
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

    private PoolingHttpClientConnectionManager connectionManager;
    
    private CacheConfig cacheConfig;
    
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
                .setUserAgent("blueMarine")
                .setDefaultHeaders(Arrays.asList(new BasicHeader("Accept", "application/n3")))
                .setConnectionManager(connectionManager)
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

                cacheStorage.putEntry(url.toExternalForm(), new HttpCacheEntry(
                        date, date, response.getStatusLine(), response.getAllHeaders(), resource));
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
