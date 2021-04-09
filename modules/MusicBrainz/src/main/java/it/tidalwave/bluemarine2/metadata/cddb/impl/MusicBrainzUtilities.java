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
package it.tidalwave.bluemarine2.metadata.cddb.impl;

import javax.annotation.Nonnull;
import java.net.URLEncoder;
import lombok.NoArgsConstructor;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class MusicBrainzUtilities
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
//                .replace("/", " ").replace(':', ' ').replace('[', ' ').replace(']', ' ')
    // See https://lucene.apache.org/core/2_9_4/queryparsersyntax.html#Escaping%20Special%20Characters
    // + - && || ! ( ) { } [ ] ^ " ~ * ? : \
    // See http://stackoverflow.com/questions/9323848/how-can-i-escape-a-group-of-special-characters-in-java-in-one-method
    @Nonnull
    public static String escape (@Nonnull final String string)
      {
        return
          URLEncoder.encode(
                  string
//                     .replace("#", " ")
//
//                     .replace("/", " ")
//                     .replace(":", " ")
//                     .replace("[", " ")
//                     .replace("]", " ")
//                     .replace("(", " ")
//                     .replace(")", " ")
                     .replace("/", "\\/")
                     .replace(":", "\\:")
                     .replace("[", "\\[")
                     .replace("]", "\\]")
                     .replace("(", "\\(")
                     .replace(")", "\\)")
                , UTF_8);

//        final Pattern pattern = Pattern.compile("([-&\\|!\\(\\){}\\[\\]\\^\"\\~\\*\\?:\\\\])");
//        return pattern.matcher(string).replaceAll("\\\\$1");
      }
  }
