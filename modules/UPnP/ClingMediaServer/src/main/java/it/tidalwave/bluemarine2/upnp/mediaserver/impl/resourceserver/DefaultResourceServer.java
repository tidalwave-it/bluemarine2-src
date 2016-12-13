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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.resourceserver;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.PropertyNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static javax.servlet.http.HttpServletResponse.*;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedToNativeForm;
import static it.tidalwave.bluemarine2.util.Miscellaneous.normalizedPath;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultResourceServer implements ResourceServer
  {
    @Getter
    private String ipAddress = "";

    @Getter
    private int port;

    private Server server;

    private Path rootPath;

    protected static class Range
      {
        private final long start;
        private final long end;
        private final long length;
        private final long total;

        /**
         * Construct a byte range.
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range (final long start, final long end, final long total)
          {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private final ServletAdapter servlet = new ServletAdapter()
      {
        private static final long serialVersionUID = -387471254552805904L;

        private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

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
      };

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPowerOnNotification (final @ListensTo @Nonnull PowerOnNotification notification)
      throws Exception
      {
        log.info("onPowerOnNotification({})", notification);
        rootPath = notification.getProperties().get(PropertyNames.ROOT_PATH);
        ipAddress = getNonLoopbackIPv4Address().getHostAddress();
        server = new Server(InetSocketAddress.createUnresolved(ipAddress, Integer.getInteger("port", 0)));
        server.setHandler(servlet.asHandler());
        server.start();
        port = server.getConnectors()[0].getLocalPort(); // jetty 8
//        port = ((ServerConnector)server.getConnectors()[0]).getLocalPort(); // jetty 9
        log.info(">>>> resource server jetty started at {}:{} serving resources at {}", ipAddress, port, rootPath);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private InetAddress getNonLoopbackIPv4Address()
      throws SocketException
      {
        for (final Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements() ; )
          {
            final NetworkInterface itf =  en.nextElement();

            for (final Enumeration<InetAddress> ee = itf.getInetAddresses(); ee.hasMoreElements() ;)
              {
                final InetAddress address = ee.nextElement();

                if (!address.isLoopbackAddress() && (address instanceof Inet4Address))
                  {
                    return address;
                  }
              }
          }

        log.warn("Returning loopback address!");
        return InetAddress.getLoopbackAddress();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PreDestroy // FIXME: user PowerOffNotification
    private void shutDown()
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
        final Path path = rootPath.relativize(resource.getPath());
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
