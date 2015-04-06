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
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.client.cache.Resource;
import org.apache.http.impl.client.cache.FileResource;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.DefaultHttpResponseWriter;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.message.BasicHttpResponse;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.StandardOpenOption.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class SimpleHttpCacheStorage implements HttpCacheStorage
  {
    private static final String PATH_CONTENT = "content";
    private static final String PATH_HEADERS = "headers";
    
    @Getter @Setter
    private Path folderPath = Paths.get(System.getProperty("java.io.tmpdir"));
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void putEntry (final @Nonnull String key, final @Nonnull HttpCacheEntry entry)
      throws IOException 
      {
        try 
          {
            log.debug("putEntry({}, {})", key, entry);
            final Path cachePath = getCacheItemPath(new URL(key));
            createDirectories(cachePath);
            final Path cacheHeadersPath = cachePath.resolve(PATH_HEADERS);
            final Path cacheContentPath = cachePath.resolve(PATH_CONTENT);

            @Cleanup final OutputStream os = newOutputStream(cacheHeadersPath, CREATE);
            final SessionOutputBufferImpl sob = sessionOutputBufferFrom(os);
            final DefaultHttpResponseWriter writer = new DefaultHttpResponseWriter(sob);
            writer.write(responseFrom(entry));
            sob.flush();

            if (entry.getResource().length() > 0)
              {
                copy(entry.getResource().getInputStream(), cacheContentPath, REPLACE_EXISTING);
              }
          }
        catch (HttpException e)
          {
            throw new IOException(e);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public HttpCacheEntry getEntry (final @Nonnull String key) 
      throws IOException 
      {
        log.debug("getEntry({})", key);
        final Path cachePath = getCacheItemPath(new URL(key));
        final Path cacheHeadersPath = cachePath.resolve(PATH_HEADERS);
        final Path cacheContentPath = cachePath.resolve(PATH_CONTENT);

        if (!exists(cacheHeadersPath))
          {
            log.trace(">>>> cache miss: {}", cacheHeadersPath);
            return null;  
          }

        try
          {
            @Cleanup final InputStream is = newInputStream(cacheHeadersPath);
            final SessionInputBufferImpl sib = sessionInputBufferFrom(is);
            final DefaultHttpResponseParser parser = new DefaultHttpResponseParser(sib);
            return entryFrom(cacheContentPath, parser.parse());
          }
        catch (HttpException e)
          {
            throw new IOException(e);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void removeEntry (final @Nonnull String key) 
      throws IOException
      {
        log.debug("removeEntry({})");
        // FIXME
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void updateEntry (final @Nonnull String key, final @Nonnull HttpCacheUpdateCallback callback)
      throws IOException, HttpCacheUpdateException 
      {
        log.debug("updateEntry({}, {})", key, callback);
        // FIXME
      }

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private Path getCacheItemPath (final @Nonnull URL url)
      throws MalformedURLException 
      {
        final int port = url.getPort();
        final URL url2 = new URL(url.getProtocol(), url.getHost(), (port == 80) ? -1 : port, url.getFile());
        final Path cachePath = Paths.get(url2.toString().replaceAll(":", ""));
        return folderPath.resolve(cachePath);
      } 

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private static SessionInputBufferImpl sessionInputBufferFrom (final @Nonnull InputStream is) 
      {
        final HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
        final SessionInputBufferImpl sib = new SessionInputBufferImpl(metrics, 100);
        sib.bind(is);
        return sib;
      }

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private static SessionOutputBufferImpl sessionOutputBufferFrom (final @Nonnull OutputStream os) 
      {
        final HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
        final SessionOutputBufferImpl sob = new SessionOutputBufferImpl(metrics, 100);
        sob.bind(os);
        return sob;
      }

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private static HttpResponse responseFrom (final @Nonnull HttpCacheEntry entry) 
      {
        final BasicHttpResponse response = new BasicHttpResponse(entry.getStatusLine());
        response.setHeaders(entry.getAllHeaders());
        return response;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private static HttpCacheEntry entryFrom (final @Nonnull Path cacheContentPath, 
                                             final @Nonnull HttpResponse response) 
      {
        final Date date = new Date(); // FIXME: force hit?
//                        new Date(Files.getLastModifiedTime(cacheHeadersPath).toMillis());
        final Resource resource =  exists(cacheContentPath) ? new FileResource(cacheContentPath.toFile()) : null;
        return new HttpCacheEntry(date, date, response.getStatusLine(), response.getAllHeaders(), resource);
      }
  }

