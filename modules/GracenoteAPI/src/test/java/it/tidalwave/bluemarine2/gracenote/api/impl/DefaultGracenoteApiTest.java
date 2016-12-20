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
package it.tidalwave.bluemarine2.gracenote.api.impl;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitOption.*;
import static it.tidalwave.util.test.FileComparisonUtils8.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultGracenoteApiTest
  {
    private DefaultGracenoteApi underTest;

    @BeforeMethod
    public void setup()
      {
        underTest = new DefaultGracenoteApi();
        underTest.initialize();
        underTest.setUserId(System.getProperty("gracenote.user", ""));
        underTest.setClientId(System.getProperty("gracenote.client", ""));
      }

    @Test
    public void testQueryAlbumToc()
      throws Exception
      {
        // given
        // when
        final ResponseEntity<String> response = underTest.queryAlbumToc("150 21860 38705 47155 68112 81095 99740 114517 131995 145947 163532 176950 188370 218577 241080 272992 287877 307292");
        final Path actualResult = dump("queryAlbumTocResponse.txt", response);
        // then
        assertSameContents(PATH_EXPECTED_TEST_RESULTS.resolve("queryAlbumTocResponse.txt"), actualResult);
      }

    @Test
    public void testQueryAlbumFetch()
      throws Exception
      {
        // given
        // when
        final ResponseEntity<String> response = underTest.queryAlbumFetch("161343049-DE60B292E7510AB532A959E2F8140814");
        final Path actualResult = dump("queryAlbumFetchResponse.txt", response);
        // then
        assertSameContents(PATH_EXPECTED_TEST_RESULTS.resolve("queryAlbumFetchResponse.txt"), actualResult);
      }

    @Test(dataProvider = "gracenoteResourcesProvider")
    public void downloadGracenoteResource (final @Nonnull String testSet, final @Nonnull Path path)
      {
        underTest.initialize(); // FIXME

        try
          {
            final Optional<String> iTunesComment = readiTunesCommentFrom(path);
            log.info(">>>> {}", iTunesComment);

            final Path targetFolder = Paths.get("target/test-results/gracenote");

            if (iTunesComment.isPresent())
              {
                final String offsets = itunesCommentToAlbumToc(iTunesComment.get());
                final Path actualResult = targetFolder.resolve(testSet).resolve("albumToc").resolve(offsets.replace(' ', '/')).resolve("response.txt");
                final Path expectedResult = PATH_EXPECTED_TEST_RESULTS.resolve("gracenote").resolve(testSet).resolve("albumToc").resolve(offsets.replace(' ', '/')).resolve("response.txt");

                if (!Files.exists(actualResult))
                  {
                    log.info(">>>> writing to {}", actualResult);
                    Files.createDirectories(actualResult.getParent());
                    final ResponseEntity<String> response = underTest.queryAlbumToc(offsets);
                    dump(actualResult, response);

                    assertSameContents(expectedResult, actualResult);
                  }
              }
            else
              {
                  // TODO: write a file telling that there are no offsets, so you can assert it
              }

          }
        catch (IOException e)
          {
            log.error("", e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<String> readiTunesCommentFrom (final @Nonnull Path path)
      throws IOException
      {
        return Files.lines(path, UTF_8).filter(s -> s.contains("[iTunes.comment]"))
                                       .findFirst()
                                       .map(s -> s.replaceAll("^.*cddb1=", "").replaceAll(", .*$", ""));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] gracenoteResourcesProvider()
      throws IOException
      {
        final String testSetName = "iTunes-fg-20160504-1";
        final Path basePath = Paths.get("target/metadata").resolve(testSetName);
        return Files.walk(basePath, FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.getFileName().toString().startsWith(".")) // isHidden() throws exception
                    .map(path -> new Object[] { testSetName, path })
                    .collect(toList())
                    .toArray(new Object[0][0]);
      }

    @Nonnull
    private static String itunesCommentToAlbumToc (final @Nonnull String comment)
      {
        return Stream.of(comment.split("\\+")).skip(3).collect(Collectors.joining(" "));
      }

    private final static List<String> IGNORED_HEADERS =
            Arrays.asList("Date", "Server", "X-Powered-By", "Connection", "Keep-Alive", "Vary");

    @Nonnull
    private static Path dump (final @Nonnull String resourceName, final @Nonnull ResponseEntity<String> response)
      throws IOException
      {
        final Path actualResult = PATH_TEST_RESULTS.resolve(resourceName);
        dump(actualResult, response);
        return actualResult;
      }

    @Nonnull
    private static void dump (final @Nonnull Path actualResult, final @Nonnull ResponseEntity<String> response)
      throws IOException
      {
        Files.createDirectories(actualResult.getParent());
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        pw.printf("HTTP/1.1 %d %s%n", response.getStatusCode().value(), response.getStatusCode().name());
        response.getHeaders().entrySet().stream()
                                        .filter(e -> !IGNORED_HEADERS.contains(e.getKey()))
                                        .sorted(comparing(e -> e.getKey()))
                                        .forEach(e -> pw.printf("%s: %s%n", e.getKey(), e.getValue().get(0)));
        pw.println();
        pw.print(response.getBody());
        pw.close();
        Files.write(actualResult, Arrays.asList(sw.toString()), UTF_8);
      }
  }
