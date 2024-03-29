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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.Header;
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
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import it.tidalwave.util.annotation.VisibleForTesting;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class SimpleHttpCacheStorage implements HttpCacheStorage
  {
    private static final String PATH_CONTENT = "content";
    private static final String PATH_HEADERS = "headers";

    private static final Collection<String> NEVER_EXPIRING_HEADERS = List.of("Cache-Control", "Expires", "Pragma");
    
    @Getter @Setter
    private Path folderPath = Paths.get(System.getProperty("java.io.tmpdir"));
    
    /** When this field is {@code true} the headers of items extracted from the cache are manipulated so it appears
     *  they never expire. */
    @Getter @Setter
    private boolean neverExpiring;
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void putEntry (@Nonnull final String key, @Nonnull final HttpCacheEntry entry)
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
    public HttpCacheEntry getEntry (@Nonnull final String key)
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
    public void removeEntry (@Nonnull final String key)
      throws IOException
      {
        log.debug("removeEntry({})", key);
        // FIXME
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void updateEntry (@Nonnull final String key, @Nonnull final HttpCacheUpdateCallback callback)
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
    @VisibleForTesting boolean isCachedResourcePresent (@Nonnull final String key)
      throws MalformedURLException
      {
        final Path cachePath = getCacheItemPath(new URL(key));
        final Path cacheHeadersPath = cachePath.resolve(PATH_HEADERS);
        final Path cacheContentPath = cachePath.resolve(PATH_CONTENT);
        log.trace(">>>> probing cached entry at {}", cachePath);
        
        return exists(cacheHeadersPath) && exists(cacheContentPath);
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private Path getCacheItemPath (@Nonnull final URL url)
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
    private static SessionInputBufferImpl sessionInputBufferFrom (@Nonnull final InputStream is)
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
    private static SessionOutputBufferImpl sessionOutputBufferFrom (@Nonnull final OutputStream os)
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
    private static HttpResponse responseFrom (@Nonnull final HttpCacheEntry entry)
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
    private HttpCacheEntry entryFrom (@Nonnull final Path cacheContentPath,
                                      @Nonnull final HttpResponse response)
      {
        final Date date = new Date(); // FIXME: force hit?
//                        new Date(Files.getLastModifiedTime(cacheHeadersPath).toMillis());
        final Resource resource =  exists(cacheContentPath) ? new FileResource(cacheContentPath.toFile()) : null;
        
        List<Header> headers = new ArrayList<>(List.of(response.getAllHeaders()));
        
        if (neverExpiring)
          {
            headers = headers.stream().filter(header -> !NEVER_EXPIRING_HEADERS.contains(header.getName()))
                                      .collect(Collectors.toList());
            headers.add(new BasicHeader("Expires", "Mon, 31 Dec 2099 00:00:00 GMT"));
          }
        
        return new HttpCacheEntry(date, date, response.getStatusLine(), headers.toArray(new Header[0]), resource);
      }
  }

