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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import it.tidalwave.bluemarine2.model.AudioFile;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultResourceServer implements ResourceServer
  {
    @Getter
    private String ipAddress = "";

    @Getter
    private int port;

    private Server server;

    private final Path root = Paths.get("/Users/fritz/Library/Application Support/blueMarine2/"); // FIXME

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private final ServletAdapter servlet = new ServletAdapter()
      {
        private static final long serialVersionUID = -387471254552805904L;

        @Override
        protected void doGet (final @Nonnull HttpServletRequest request, final @Nonnull HttpServletResponse response)
          throws ServletException, IOException
          {
            log.debug("request URI: {}", request.getRequestURI());
            final Path resourcePath = root.resolve(urlDecoded(request.getRequestURI().replaceAll("^/", "")));
            log.debug(">>>> resource path: {}", resourcePath);

            if (!Files.exists(resourcePath))
              {
                log.error(">>>> resource not found: {}", resourcePath);
                response.setStatus(404); // FIXME
              }
            else
              {
                response.setContentType(Files.probeContentType(resourcePath));
                response.setContentLength((int)Files.size(resourcePath));
                Files.copy(resourcePath, response.getOutputStream());
              }
          }
      };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PostConstruct // FIXME: user power on
    private void initialize()
      throws Exception
      {
        ipAddress = InetAddress.getLocalHost().getHostAddress();
        server = new Server(InetSocketAddress.createUnresolved(ipAddress, Integer.getInteger("port", 0)));
        server.setHandler(servlet.asHandler());
        server.start();
        port = server.getConnectors()[0].getLocalPort();
        log.info(">>>> resource server jetty started at {}:{}", ipAddress, port);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PreDestroy // FIXME: user power off
    private void destroy()
      throws Exception
      {
        server.stop();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    public String urlForResource (final @Nonnull AudioFile resource)
      {
        final Path path = root.relativize(resource.getPath());

        final String s = StreamSupport.stream(path.spliterator(), false)
                                      .map(p -> urlEncoded(p.toString()))
                                      .collect(Collectors.joining("/"));

        return "http://" + ipAddress + ":" + port + "/" + s;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String urlDecoded (final @Nonnull String string)
      {
        try
          {
            return URLDecoder.decode(string, "UTF-8");
          }
        catch (UnsupportedEncodingException e)
          {
            throw new RuntimeException(e);
          }

      }
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String urlEncoded (final @Nonnull String string)
      {
        try
          {
            return URLEncoder.encode(string, "UTF-8");
          }
        catch (UnsupportedEncodingException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
