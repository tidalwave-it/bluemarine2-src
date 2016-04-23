/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.spi.AsDelegateProvider;
import it.tidalwave.util.spi.EmptyAsDelegateProvider;
import it.tidalwave.bluemarine2.model.Entity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class VirtualMediaFolderTest
  {
    static class TestCaseBuilder
      {
        @Getter
        private final Map<Path, VirtualMediaFolder> folderMap = new HashMap<>();

        private final Map<Path, Collection<Entity>> childrenMap = new HashMap<>();

        @Getter
        private final Set<Path> paths = new TreeSet<>();

        TestCaseBuilder()
          {
            createFolder("/music/artists/Bach/BWV104");
            createFolder("/music/artists/Bach/BWV105");
            createFolder("/music/artists/Bach/BWV106");
            createFolder("/music/artists/Mozart/K10");
            createFolder("/music/artists/Mozart/K11");
            createFolder("/music/songs/Night and Day");
            createFolder("/music/songs/Round Midnight");
            createFolder("/photos/john doe/photo1");
            createFolder("/photos/john doe/photo2");
            createFolder("/photos/john doe/photo3");
            createFolder("/photos/jane smith/photo1");
            createFolder("/photos/jane smith/photo2");

//            log.info("folders: {}", folderMap);
//            log.info("children: {}", childrenMap);
          }

        @Nonnull
        private VirtualMediaFolder createFolder (final @Nonnull String pathAsString)
          {
            return createFolder(Paths.get(pathAsString));
          }

        @Nonnull
        private VirtualMediaFolder createFolder (final @Nonnull Path path)
          {
//            log.info("createFolder({})", path);
            paths.add(path);

            VirtualMediaFolder folder = folderMap.get(path);

            if (folder == null)
              {
                final Path parentPath = path.getParent();
                final VirtualMediaFolder parent = (parentPath != null) ? createFolder(parentPath) : null;

                if (parentPath != null)
                  {
                    Collection<Entity> c = childrenMap.get(parentPath);

                    if (c == null)
                      {
                        c = new ArrayList<>();
                        childrenMap.put(parentPath, c);
                      }
                  }

                folder = new VirtualMediaFolder(parent, path, path.toString(), () -> childrenMap.get(path));
                folderMap.put(path, folder);

                if (parentPath != null)
                  {
                    childrenMap.get(parentPath).add(folder);
                  }
              }

            return folder;
          }
      }

    private VirtualMediaFolder underTest;

    @BeforeMethod
    public void setup()
      {
        AsDelegateProvider.Locator.set(new EmptyAsDelegateProvider());
        underTest = new TestCaseBuilder().getFolderMap().get(Paths.get("/"));
      }

    @Test
    public void must_correctly_find_all_children()
      {
        final List<? extends Entity> children = underTest.findChildren().results();
        assertThat(children.size(), is(2));
        assertThat(children.get(0).as(Displayable).getDisplayName(), is("/music"));
        assertThat(children.get(1).as(Displayable).getDisplayName(), is("/photos"));
      }

    @Test(dataProvider = "pathsProvider")
    public void must_correctly_find_children_by_path (final @Nonnull Path path)
      {
        final List<? extends Entity> children = underTest.findChildren().withPath(path).results();
        assertThat(children.size(), is(1));
        assertThat(children.get(0).as(Displayable).getDisplayName(), is(path.toString()));
      }

    @DataProvider
    public static Object[][] pathsProvider()
      {
        return new TestCaseBuilder().getPaths().stream()
                                               .map(p -> new Object[] { p })
                                               .collect(Collectors.toList())
                                               .toArray(new Object[0][0]);
      }
  }
