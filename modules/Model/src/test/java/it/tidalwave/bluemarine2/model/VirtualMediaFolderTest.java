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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.spi.AsDelegateProvider;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.bluemarine2.commons.test.TestSetTriple.toTestNGDataProvider;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * This is an integration test, also exercises {@link FactoryBasedEntityFinder}.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class VirtualMediaFolderTest extends SpringTestSupport
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static class TestCaseBuilder
      {
        @Getter
        private final Map<Path, VirtualMediaFolder> folderMap = new HashMap<>();

        private final Map<Path, Collection<PathAwareEntity>> childrenMap = new HashMap<>();

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

        private void createFolder (final @Nonnull String pathAsString)
          {
            findOrCreateFolder(Paths.get(pathAsString));
          }

        @Nonnull
        private VirtualMediaFolder findOrCreateFolder (final @Nonnull Path path)
          {
            //return folderMap.computeIfAbsent(path, k1 ->
            if (!folderMap.containsKey(path))
              {
                paths.add(path);
                final Optional<Path> parentPath = Optional.ofNullable(path.getParent());
                final Optional<VirtualMediaFolder> parent = parentPath.map(this::findOrCreateFolder);
                final VirtualMediaFolder.EntityCollectionFactory f = p -> childrenMap.get(path);
                final VirtualMediaFolder folder = new VirtualMediaFolder(parent, path, path.toString(), f);
                folderMap.put(path, folder);
                parentPath.ifPresent(pp -> childrenMap.computeIfAbsent(pp, k2 -> new ArrayList<>()).add(folder));

                return folder;
              }
            else
              {
                return folderMap.get(path);
              }
          }
      }

    private VirtualMediaFolder underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public VirtualMediaFolderTest()
      {
        super("/META-INF/MockBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        AsDelegateProvider.Locator.set(AsDelegateProvider.empty());
        underTest = new TestCaseBuilder().getFolderMap().get(Paths.get("/"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_correctly_find_all_children()
      {
        // when
        final List<? extends PathAwareEntity> children = underTest.findChildren().results();
        // then
        assertThat(children.size(), is(2));
        assertThat(children.get(0).as(_Displayable_).getDisplayName(), is("/music"));
        assertThat(children.get(1).as(_Displayable_).getDisplayName(), is("/photos"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "pathsProvider")
    public void must_correctly_find_children_by_path (final @Nonnull Path path)
      {
        // when
        final List<? extends PathAwareEntity> children = underTest.findChildren().withPath(path).results();
        // then
        log.debug("findChildren().withPath({}).results() = {}", path, children);
        assertThat(children.size(), is(1));
        assertThat(children.get(0).as(_Displayable_).getDisplayName(), is(path.toString()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] pathsProvider()
      {
        return new TestCaseBuilder().getPaths().stream().collect(toTestNGDataProvider());
      }
  }
