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
package it.tidalwave.bluemarine2.mediascanner.impl.xmp;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * A class that applies a simple rule-based mapping on a path (a string segmented by slashes) to convert it to a token.
 *
 * For instance, this rule:
 *
 * <pre>
 * /x:xmpmeta/rdf:RDF/rdf:Description/exif:* = exif:$1
 * </pre>
 *
 * performs the following conversions:
 *
 * <pre>
 * /x:xmpmeta/rdf:RDF/rdf:Description/exif:ExposureMode -> exif:ExposureMode
 * /x:xmpmeta/rdf:RDF/rdf:Description/exif:FNumber -> exif:FNumber
 * </pre>
 *
 * This rule:
 *
 * <pre>
 * /x:xmpmeta/rdf:RDF/rdf:LensSpecification/exif:* /rdf:Alt/rdf:li=exif:$1
 * </pre>
 *
 * performs the following conversions:
 *
 * <pre>
 * /x:xmpmeta/rdf:RDF/rdf:Description/exif:LensSpecification/rdf:Alt/rdf:li -> exif:LensSpecification
 * </pre>
 *
 * It is possible to use different replacing groups (referred to as $1, $2, ...) by using multiple * in different
 * segments of the path.
 *
 * Globs are used instead of regular expressions to avoid the necessity of escaping characters (which would include
 * ':').
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class PathMapper
  {
    private static class Rule
      {
        @Nonnull
        private final Pattern pattern;

        @Nonnull
        private final String token;

        Rule (@Nonnull final String glob, @Nonnull final String token)
          {
            this.pattern = Pattern.compile(regexFromGlob(glob));
            this.token = token;
          }

        @Nonnull
        private static String regexFromGlob (@Nonnull final String glob)
          {
            final StringBuilder builder = new StringBuilder("^");

            for (int i = 0; i < glob.length(); ++i)
              {
                final char c = glob.charAt(i);

                switch (c)
                  {
                    case '*': builder.append("([^/:]*)"); break;
                    case '?': builder.append("(.)"); break;
                    case '.': builder.append("\\."); break;
                    case ':': builder.append("\\:"); break;
                    case '\\': builder.append("\\\\"); break;
                    default: builder.append(c);
                  }
              }

            return builder.append('$').toString();
          }
      }

    private final List<Rule> rules;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public PathMapper (@Nonnull final Collection<String> rulesAsString)
      {
        rules = rulesAsString.stream()
                             .map(s -> s.replaceAll("#.*", ""))
                             .filter(s -> !s.isBlank())
                             .map(s -> (s + " ").split("="))
                             .map(p -> new Rule(p[0].trim(), p[1].trim()))
                             .collect(toList());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<String> getTokenFor (@Nonnull final String path)
      {
        for (final Rule e : rules)
          {
            final Matcher matcher = e.pattern.matcher(path);

            if (matcher.matches())
              {
                String token = e.token;

                for (int i = 1; i <= matcher.groupCount(); i++)
                  {
                    token = token.replace("$" + i, matcher.group(i));
                  }

                return Optional.of(token);
              }
          }
        return Optional.empty();
      }
  }
