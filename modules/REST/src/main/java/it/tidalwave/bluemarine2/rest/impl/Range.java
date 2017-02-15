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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
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
    public static Range full (final @Nonnegative long total)
      {
        return new Range(0, total - 1, total);
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
     * @param   total           the length of the full datum
     * @return                  the range
     *
     ******************************************************************************************************************/
    @Nonnull
    public static List<Range> fromHeader (final @Nullable String rangeHeader, final @Nonnegative long total)
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
                    start = total - end;
                    end = total - 1;
                  }
                else if ((end == -1) || (end > total - 1))
                  {
                    end = total - 1;
                  }

                if (start > end)
                  {
                    throw new IllegalArgumentException("Invalid range: " + rangeHeader);
                  }

                ranges.add(new Range(start, end, total));
              }
          }

        return ranges;
      }

    /*******************************************************************************************************************
     *
     * Returns a {@link ResourceRegion} mapping the portion of bytes matching this range.
     *
     * @param       resource    the resource
     * @return                  the region
     *
     ******************************************************************************************************************/
    @Nonnull
    public ResourceRegion getRegion (final @Nonnull Resource resource)
      {
        return new ResourceRegion(resource, start, length);
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

