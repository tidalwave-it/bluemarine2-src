/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import javax.xml.xpath.XPathExpression;
import java.util.List;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.mockito.Mockito.when;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ThemesPhotoCollectionProviderTest extends PhotoCollectionProviderTestSupport
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "selectorProvider")
    public void must_properly_parse_themes (final @Nonnull String selector,
                                            final @Nonnull XPathExpression expression)
      throws Exception
      {
        // given
        final ThemesPhotoCollectionProvider underTest = new ThemesPhotoCollectionProvider(URL_MOCK_RESOURCE);
        // when
        final List<GalleryDescription> themeDescriptions = underTest.parseThemes(expression);
        // then
        dumpAndAssertResults(selector, themeDescriptions);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "must_properly_parse_themes")
    public void must_properly_create_hierarchy()
      throws Exception
      {
        // given
        final ThemesPhotoCollectionProvider underTest = new ThemesPhotoCollectionProvider(URL_MOCK_RESOURCE);
        // when
        final EntityFinder finder = underTest.findPhotos(mediaFolder);
        // then
        when(mediaFolder.findChildren()).thenReturn(finder);
        dumpAndAssertResults("themes-hierarchy.txt", dump(mediaFolder));
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
