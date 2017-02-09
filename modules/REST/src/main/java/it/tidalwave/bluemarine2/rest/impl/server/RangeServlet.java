/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.rest.impl.server;

import javax.annotation.Nonnull;
import java.util.List;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import static javax.servlet.http.HttpServletResponse.*;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedPath;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class RangeServlet extends HttpServlet
  {
    private static final long serialVersionUID = -3874712134552805904L;

    @Nonnull
    private final Path rootPath;

    @Override
    protected void doGet (final @Nonnull HttpServletRequest request, final @Nonnull HttpServletResponse response)
      throws ServletException, IOException
      {
        final Path resourcePath =
                  normalizedPath(rootPath.resolve(urlDecoded(request.getRequestURI().replaceAll("^/", ""))));
        log.debug(">>>> resource path: {}", resourcePath);

    //            if (isTroubled(resourcePath))
    //              {
    //                log.error(">>>> path affected by BMT-46: {}", resourcePath);
    //                response.setStatus(SC_INTERNAL_SERVER_ERROR);
    //                return;
    //              }

        if (!Files.exists(resourcePath))
          {
            log.error(">>>> resource not found: {}", resourcePath);
            response.setStatus(SC_NOT_FOUND);
            return;
          }

        if (Files.isDirectory(resourcePath))
          {
            log.error(">>>> cannot serve directories: {}", resourcePath);
            response.setStatus(SC_UNAUTHORIZED);
            return;
          }

        final long length = Files.size(resourcePath);
        final Range fullRange = Range.full(length);
        final List<Range> ranges = Range.fromHeader(request.getHeader("Range"), length);

        if (ranges.size() > 1)
          {
            log.error("Can't support multi-range: {}", ranges);
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
          }

        response.setContentType("audio/mpeg");
    //                response.setContentType(Files.probeContentType(resourcePath)); FIXME
        final Range range = ranges.stream().findFirst().orElse(fullRange);
        response.setBufferSize(0);
        response.setContentLength((int)range.getLength());

        if (!fullRange.equals(range))
          {
            response.setHeader("Content-Range", range.toHeader());
            response.setStatus(SC_PARTIAL_CONTENT);
          }

        try (final RandomAccessFile input = new RandomAccessFile(resourcePath.toFile(), "r");
             final OutputStream output = response.getOutputStream())
          {
            range.copy(input, output);
            output.flush(); // force EOF in case
          }
        catch (EOFException e)
          {
            log.debug("EOF - probably client closed connection");
          }
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
  }
