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
package it.tidalwave.bluemarine2.model.spi;

import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class PathAwareEntityTest
  {
    private static final Path PARENT_PATH = Paths.get("/parent");
    private static final Path CHILD_PATH = PARENT_PATH.resolve("child");
    private static final Path FULL_PATH = PARENT_PATH.resolve("/foo/bar");

    @Test
    public void relativePath_in_entity_with_parent_must_be_relativised()
      {
        // given
        final PathAwareEntity parentEntity = mock(PathAwareEntity.class);
        when(parentEntity.getPath()).thenReturn(PARENT_PATH);

        final PathAwareEntity underTest = mock(PathAwareEntity.class);
        when(underTest.getParent()).thenReturn(Optional.of(parentEntity));
        when(underTest.getPath()).thenReturn(CHILD_PATH);
        when(underTest.getRelativePath()).thenCallRealMethod();
        // when
        final Path relativePath = underTest.getRelativePath();
        // then
        assertThat(relativePath.toString(), is("child"));
      }

    @Test
    public void relativePath_in_parentless_entity_must_be_the_same_as_Path()
      {
        // given
        final PathAwareEntity underTest = mock(PathAwareEntity.class);
        when(underTest.getParent()).thenReturn(Optional.empty());
        when(underTest.getPath()).thenReturn(FULL_PATH);
        when(underTest.getRelativePath()).thenCallRealMethod();
        // when
        final Path relativePath = underTest.getRelativePath();
        // then
        assertThat(relativePath.toString(), is("/foo/bar"));
      }
  }
