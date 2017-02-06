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
import java.util.ArrayList;
import java.util.Enumeration;
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
    private static final long serialVersionUID = -387471254552805904L;

    private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    @Nonnull
    private final Path rootPath;

    @Override
    protected void doGet (final @Nonnull HttpServletRequest request, final @Nonnull HttpServletResponse response)
      throws ServletException, IOException
      {
        log.trace("doGet(..., ...)");
        log.debug(">>>> request URI: {}", request.getRequestURI());

        for (final Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements(); )
          {
            final String headerName = names.nextElement();
            log.debug(">>>> request header: {} = {}", headerName, request.getHeader(headerName));
          }

        _doGet(request, response);

        log.debug(">>>> response {}", response.getStatus());

        for (final String headerName : response.getHeaderNames())
          {
            log.debug(">>>> response header: {} = {}", headerName, response.getHeaders(headerName));
          }
      }

    private void _doGet (final @Nonnull HttpServletRequest request, final @Nonnull HttpServletResponse response)
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

        final long length = (int)Files.size(resourcePath);
        final Range fullRange = new Range(0, length - 1, length);
        final List<Range> ranges = new ArrayList<>();

        final String range = request.getHeader("Range");

        if (range != null)
          {
            // Range header should match format "bytes=n-n,n-n,n-n...".
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$"))
              {
                log.error("Invalid range: {}", range);
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
              }

            // If any valid If-Range header, then process each part of byte range.
            for (final String part : range.substring(6).split(","))
              {
                // Assuming a file with length of 100, the following examples returns bytes at:
                // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                long start = subStringOrMinusOne(part, 0, part.indexOf("-"));
                long end = subStringOrMinusOne(part, part.indexOf("-") + 1, part.length());

                if (start == -1)
                  {
                    start = length - end;
                    end = length - 1;
                  }
                else if ((end == -1) || (end > length - 1))
                  {
                    end = length - 1;
                  }

                if (start > end)
                  {
                    log.error("Invalid range: {}", range);
                    response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                    response.sendError(SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return;
                  }

                ranges.add(new Range(start, end, length));
              }
          }

        if (ranges.size() > 1)
          {
            log.error("Can't support multi-range: {}", range);
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
          }

        response.setContentType("audio/mpeg");
    //                response.setContentType(Files.probeContentType(resourcePath)); FIXME
        final Range r = ranges.stream().findFirst().orElse(fullRange);
        response.setBufferSize(0);
        response.setContentLength((int)r.length);

        if (!fullRange.equals(r))
          {
            response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
            response.setStatus(SC_PARTIAL_CONTENT);
          }

        try (final RandomAccessFile input = new RandomAccessFile(resourcePath.toFile(), "r");
             final OutputStream output = response.getOutputStream())
          {
            // FIXME: use memory mapped i/o
            copy(input, output, r.start, r.length);
            output.flush(); // force EOF in case
          }
        catch (EOFException e)
          {
            log.debug("EOF - probably client closed connection");
          }
      }

    /**
     * Returns a substring of the given string value from the given begin index to the given end
     * index as a long. If the substring is empty, then -1 will be returned
     * @param value The string value to return a substring as long for.
     * @param beginIndex The begin index of the substring to be returned as long.
     * @param endIndex The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring is empty.
     */
    private long subStringOrMinusOne (String value, int beginIndex, int endIndex)
      {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
      }

    /**
     * Copy the given byte range of the given input to the given output.
     * @param input The input to copy the given range to the given output for.
     * @param output The output to copy the given range from the given input for.
     * @param start Start of the byte range.
     * @param length Length of the byte range.
     * @throws IOException If something fails at I/O level.
     */
    private void copy (RandomAccessFile input, OutputStream output, long start, long length)
      throws IOException
      {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;

        if (input.length() == length)
          {
            // Write full range.
            while ((read = input.read(buffer)) > 0)
              {
                output.write(buffer, 0, read);
              }
          }
        else
          {
            // Write partial range.
            input.seek(start);
            long toRead = length;

            while ((read = input.read(buffer)) > 0)
              {
                if ((toRead -= read) > 0)
                  {
                    output.write(buffer, 0, read);
                  }
                else
                  {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                  }
              }
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
