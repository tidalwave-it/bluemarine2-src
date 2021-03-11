/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.commons.test;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * This class represents a file from a test set. It contains the test set name and the file path.
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PRIVATE) @Getter @Slf4j
public class TestSetTriple
  {
    public final String testSetName;

    public final Path testSetPath;

    public final Path filePath;

    /*******************************************************************************************************************
     *
     * Creates a {@link Stream} containing the files in the provided test sets. Files are resolved using the
     * {@link TestSetLocator}.
     *
     * @param   testSetNames    the test set names
     * @return                  the {@code Stream}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Stream<TestSetTriple> streamOfTestSetTriples (final @Nonnull Collection<String> testSetNames)
      {
        return streamOfTestSetTriples(testSetNames, n -> TestSetLocator.getMusicTestSetsPath().resolve(n));
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link Stream} containing the files in the provided test sets.
     *
     * @param   testSetNames        the test set names
     * @param   basePathProvider    a function that resolves a test set name to a test set base path
     * @return                      the {@code Stream}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Stream<TestSetTriple> streamOfTestSetTriples (final @Nonnull Collection<String> testSetNames,
                                                                final @Nonnull Function<String, Path> basePathProvider)
      {
        return testSetNames.stream()
                           .map(testSetName -> TestSetTriple.ofName(testSetName, basePathProvider))
                           .flatMap(TestSetTriple::walk)
                           .filter(t -> Files.isRegularFile(t.filePath))
                           .filter(t -> !t.filePath.getFileName().toString().startsWith(".")); // isHidden() throws exception
      }

    /*******************************************************************************************************************
     *
     * Returns a {@link Collector) that converts the stream into an {@code Object[][]) suitable for a
     * TestNG {@code DataProvider}.
     *
     * @return                  the {@code Collector}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Collector<Object, ?, Object[][]> toTestNGDataProvider()
      {
        return collectingAndThen(mapping(object -> new Object[] { object }, toList()),
                                 list -> list.toArray(new Object[0][0]));
      }

    /*******************************************************************************************************************
     *
     * Returns the path of the test set file relativized to the root of the test set.
     *
     * @return                  the relative path
     *
     ******************************************************************************************************************/
    @Nonnull
    public Path getRelativePath()
      {
        return testSetPath.relativize(filePath);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("%s: %s : %s", testSetName, testSetPath, getRelativePath());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static TestSetTriple ofName (final @Nonnull String testSetName,
                                         final @Nonnull Function<String, Path> basePathProvider)
      {
        return new TestSetTriple(testSetName, basePathProvider.apply(testSetName), null);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Stream<TestSetTriple> walk()
      {
        try
          {
            if (Files.exists(testSetPath))
              {
                return Files.walk(testSetPath, FOLLOW_LINKS)
                            .map(path -> new TestSetTriple(testSetName, testSetPath, path));
              }
            else
              {
                log.warn("MISSING TEST SET: {} - {}", testSetName, testSetPath);
                return Stream.empty();
              }
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }
  }

