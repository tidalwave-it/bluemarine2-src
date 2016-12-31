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
package it.tidalwave.bluemarine2.commons.test;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class TestSetLocator
  {
    public static final Path PATH_TEST_RESULTS = Paths.get("target/test-results");

    public static final Path PATH_EXPECTED_TEST_RESULTS = Paths.get("target/test-classes/expected-results");

    public static final String PROPERTY_MUSIC_TEST_SETS_PATH = "blueMarine2.musicTestSets.path";

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Collection<String> allTestSets()
      {
        return Collections.unmodifiableCollection(Arrays.asList("iTunes-fg-20160504-1", "iTunes-fg-20161210-1"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnegative
    public static Path getMusicTestSetsPath()
      {
        final Path musicTestSets = Paths.get(System.getProperty(PROPERTY_MUSIC_TEST_SETS_PATH, "/doesNotExist"));

        if (!Files.exists(musicTestSets))
          {
            throw new RuntimeException("Cannot run tests: set 'blueMarine2.musicTestSets.path' to the folder "
                                     + "containing test sets (current value: " + musicTestSets + ")");
          }

        return musicTestSets;
      }
  }
