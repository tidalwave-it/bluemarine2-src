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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import it.tidalwave.bluemarine2.model.Entity;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.xpath.XPathExpression;
import org.testng.annotations.DataProvider;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ThemesPhotoCollectionProviderSupportTest
  {
    private static final String URL_MOCK_RESOURCE = "file:src/test/resources/themes.xhtml";

    private ApplicationContext context;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        // required for DCI stuff
        context = new ClassPathXmlApplicationContext("classpath*:META-INF/DciBeans.xml");
      }

    /*******************************************************************************************************************
     *
     * This test uses mock data.
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "selectorProvider")
    public void must_properly_parse_themes (final @Nonnull String selector,
                                            final @Nonnull XPathExpression expression)
      throws Exception
      {
        // given
        final MediaFolder mediaFolder = mock(MediaFolder.class);
        when(mediaFolder.getPath()).thenReturn(Paths.get("/folder"));
        when(mediaFolder.toString()).thenReturn("MediaFolder(\"/folder\"))");
        final ThemesPhotoCollectionProvider underTest = new ThemesPhotoCollectionProvider(URL_MOCK_RESOURCE);
        // when
        final List<GalleryDescription> themeDescriptions = underTest.parseThemes(expression);
        // then
        final Path actualResult = Paths.get("target", "test-results", selector);
        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", selector);
        Files.createDirectories(actualResult.getParent());
        final Stream<String> stream = themeDescriptions.stream()
                                                .map(gd -> String.format("%s: %s", gd.getDisplayName(), gd.getUrl()));
        Files.write(actualResult, (Iterable<String>)stream::iterator, StandardCharsets.UTF_8);
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }

    /*******************************************************************************************************************
     *
     * This test retrieves actual data from the network.
     *
     ******************************************************************************************************************/
    @Test
    public void must_properly_create_hierarchy()
      throws Exception
      {
        // given
        final MediaFolder mediaFolder = mock(MediaFolder.class);
        when(mediaFolder.getPath()).thenReturn(Paths.get("/folder"));
        when(mediaFolder.toString()).thenReturn("MediaFolder(\"/folder\"))");
        final ThemesPhotoCollectionProvider underTest = new ThemesPhotoCollectionProvider();
        // when
        final EntityFinder finder = underTest.findPhotos(mediaFolder);
        when(mediaFolder.findChildren()).thenReturn(finder);
        // then
        final Path actualResult = Paths.get("target", "test-results", "nodes.txt");
        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", "nodes.txt");
        Files.createDirectories(actualResult.getParent());
        final Stream<String> stream = dump(mediaFolder).stream();
        Files.write(actualResult, (Iterable<String>)stream::iterator, StandardCharsets.UTF_8);
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<String> dump (final @Nonnull Entity entity)
      {
        final List<String> result = new ArrayList<>();
        result.add("" + entity);

        if (entity instanceof MediaFolder)
          {
            ((MediaFolder)entity).findChildren().forEach(child -> result.addAll(dump(child)));
          }

        return result;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] selectorProvider()
      {
        return new Object[][]
          {
            { "themes.txt", ThemesPhotoCollectionProvider.XPATH_SUBJECTS_THUMBNAIL_EXPR },
            { "places.txt", ThemesPhotoCollectionProvider.XPATH_PLACES_THUMBNAIL_EXPR }
          };
      }
  }
