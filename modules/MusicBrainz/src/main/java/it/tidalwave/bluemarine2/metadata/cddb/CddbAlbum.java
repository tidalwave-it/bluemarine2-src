/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.cddb;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.Cddb;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Immutable @Getter @EqualsAndHashCode @ToString @Builder
public class CddbAlbum
  {
    private static final Pattern PATTERN_DISC_LENGTH = Pattern.compile("# *Disc length: ([0-9]+) *seconds");

    private static final Pattern PATTERN_TRACK_FRAME_OFFSETS = Pattern.compile("#\\s*[0-9]+");

    @Nonnull
    private final Map<String, String> properties;

    @Nonnull
    private final Cddb cddb;

    @Nonnull
    public static CddbAlbum of (final @Nonnull String stringResponse)
      {
        final String[] split = stringResponse.split("\n");

        final Cddb cddb = Cddb.builder()
                            .discId("") // FIXME
                            .trackFrameOffsets(
                                    Stream.of(split)
                                          .skip(2)
                                          .filter(string -> PATTERN_TRACK_FRAME_OFFSETS.matcher(string).matches())
                                          .mapToInt(s -> Integer.parseInt(s.substring(1).trim()))
                                          .toArray())
                            .discLength(
                                    Stream.of(split)
                                          .skip(2)
                                          .map(string -> PATTERN_DISC_LENGTH.matcher(string))
                                          .filter(Matcher::matches)
                                          .map(matcher -> Integer.parseInt(matcher.group(1)))
                                          .findFirst()
                                          .get())
                            .build();

        return builder().cddb(cddb)
                        .properties(
                                Stream.of(split)
                                      .skip(2)
                                      .filter(string -> !string.trim().isEmpty() && !string.startsWith("#"))
                                      .map(string -> string.split("="))
                                      .filter(strings -> strings.length == 2)
                                      .collect(toMap(strings -> strings[0].trim(),
                                                     strings -> strings[1].trim(),
                                                     (u, v) -> u,
                                                     TreeMap::new)))
                        .build();
      }

    @Nonnull
    public Optional<String> getProperty (final @Nonnull String key)
      {
        return Optional.ofNullable(properties.get(key));
      }

    @Nonnull
    public String toDumpString()
      {
        return "CddbAlbum\n"
              + "trackFrameOffsets: " + Arrays.stream(cddb.getTrackFrameOffsets())
                                              .mapToObj(String::valueOf)
                                              .collect(joining(" ")) + "\n"
              + "discLenght: " + cddb.getDiscLength() + "\n"
              + properties.entrySet().stream()
                                     .map(e -> String.format("%s: %s", e.getKey(), e.getValue()))
                                     .collect(joining("\n"));
      }
  }
