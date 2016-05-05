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
package it.tidalwave.bluemarine2.commons.test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.As8;
import it.tidalwave.role.SimpleComposite8;
import lombok.NoArgsConstructor;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public class TestUtilities
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public static void dumpAndAssertResults (final @Nonnull String fileName, final @Nonnull Collection<?> data)
      throws IOException
      {
        final Path actualResult = Paths.get("target", "test-results", fileName);
        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", fileName);
        Files.createDirectories(actualResult.getParent());
        final Stream<String> stream = data.stream().map(Object::toString);
        Files.write(actualResult, (Iterable<String>)stream::iterator, StandardCharsets.UTF_8);
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public static List<String> dump (final @Nonnull As8 entity)
      {
        final List<String> result = new ArrayList<>();
        result.add("" + entity);

        final Optional<SimpleComposite8> asOptional = entity.asOptional(SimpleComposite8.class);
        asOptional.ifPresent(c -> c.findChildren().results().forEach(child -> result.addAll(dump((As8)child))));

        return result;
      }
  }
