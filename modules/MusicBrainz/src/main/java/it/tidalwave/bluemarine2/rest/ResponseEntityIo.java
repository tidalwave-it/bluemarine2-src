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
package it.tidalwave.bluemarine2.rest;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.nio.charset.StandardCharsets.UTF_8;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class ResponseEntityIo
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static void store (final @Nonnull Path path,
                              final @Nonnull ResponseEntity<String> response,
                              final @Nonnull List<String> ignoredHeaders)
      throws IOException
      {
        log.trace("store({}, ..., ...)", path);

        Files.createDirectories(path.getParent());
        final StringWriter sw = new StringWriter();

        try (final PrintWriter pw = new PrintWriter(sw))
          {
            pw.printf("HTTP/1.1 %d %s%n", response.getStatusCode().value(), response.getStatusCode().name());
            response.getHeaders().entrySet().stream()
                    .filter(e -> !ignoredHeaders.contains(e.getKey()))
                    .sorted(comparing(e -> e.getKey()))
                    .forEach(e -> pw.printf("%s: %s%n", e.getKey(), e.getValue().get(0)));
            pw.println();
            pw.print(response.getBody());
          }

        Files.write(path, Arrays.asList(sw.toString()), UTF_8);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    /* package */ static Optional<ResponseEntity<String>> retrieve (final @Nonnull Path path)
      throws IOException
      {
        log.trace("retrieve({})", path);

        if (!Files.exists(path))
          {
            return Optional.empty();
          }

        final List<String> lines = Files.readAllLines(path, UTF_8);
        final HttpStatus status = HttpStatus.valueOf(Integer.parseInt(lines.get(0).split(" ")[1]));
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        int i = 1;

        for (; (i < lines.size()) && !lines.get(i).equals(""); i++)
          {
            final String[] split = lines.get(i).split(":");
            headers.add(split[0], split[1].trim());
          }

        final String body = lines.stream().skip(i + 1).collect(joining("\n"));
        final ResponseEntity<String> response = new ResponseEntity<>(body, headers, status);
//        log.trace(">>>> returning {}", response);

        return Optional.of(response);
      }
  }
