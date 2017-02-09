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
package it.tidalwave.bluemarine2.rest.impl.server;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j @Getter @EqualsAndHashCode
public class Range
  {
    private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    private final long start;
    private final long end;
    private final long length;
    private final long total;

    /*******************************************************************************************************************
     *
     * Construct a byte range.
     *
     * @param   start   start of the byte range.
     * @param   end     end of the byte range.
     * @param   total   total length of the byte source.
     *
     ******************************************************************************************************************/
    public Range (final @Nonnegative long start, final @Nonnegative long end, final @Nonnegative long total)
      {
        this.start = start;
        this.end = end;
        this.length = end - start + 1;
        this.total = total;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Range full (final @Nonnegative long length)
      {
        return new Range(0, length - 1, length);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Range subrange (final @Nonnegative long size)
      {
       return new Range(start, Math.min(end, start + size - 1), total);
      }

    /*******************************************************************************************************************
     *
     * Parses a range from a HTTP header.
     *
     * @param   rangeHeader     the HTTP header
     * @param   length          the length of the full datum
     * @return                  the range
     *
     ******************************************************************************************************************/
    @Nonnegative
    public static List<Range> fromHeader (final @Nullable String rangeHeader, final @Nonnegative long length)
      {
        final List<Range> ranges = new ArrayList<>();

        if (rangeHeader != null)
          {
            // Range header should match format "bytes=n-n,n-n,n-n...".
            if (!rangeHeader.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$"))
              {
                throw new IllegalArgumentException("Invalid range: " + rangeHeader);
              }

            // If any valid If-Range header, then process each part of byte range.
            for (final String part : rangeHeader.substring(6).split(","))
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
                    throw new IllegalArgumentException("Invalid range: " + rangeHeader);
                  }

                ranges.add(new Range(start, end, length));
              }
          }

        return ranges;
      }

    /*******************************************************************************************************************
     *
     * Copy the given byte range of the given input to the given output.
     *
     * @param   input           the input to copy the given range to the given output for.
     * @param   output          the output to copy the given range from the given input for.
     * @throws  IOException     if something fails at I/O level.
     *
     ******************************************************************************************************************/
    public void copy (final @Nonnull RandomAccessFile input, final @Nonnull OutputStream output)
      throws IOException
      {
                // FIXME: use memory mapped i/o
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
     ******************************************************************************************************************/
    @Nonnull
    public String toHeader()
      {
        return "bytes " + start + "-" + end + "/" + total;
      }

    /*******************************************************************************************************************
     *
     * Returns a substring of the given string value from the given begin index to the given end index as a long. If the
     * substring is empty, then -1 will be returned
     *
     * @param   value       the string value to return a substring as long for.
     * @param   beginIndex  the begin index of the substring to be returned as long.
     * @param   endIndex    the end index of the substring to be returned as long.
     * @return              a substring of the given string value as long or -1 if substring is empty.
     *
     ******************************************************************************************************************/
    private static long subStringOrMinusOne (final @Nonnull String value,
                                             final @Nonnegative int beginIndex,
                                             final @Nonnegative int endIndex)
      {
        final String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
      }
  }

