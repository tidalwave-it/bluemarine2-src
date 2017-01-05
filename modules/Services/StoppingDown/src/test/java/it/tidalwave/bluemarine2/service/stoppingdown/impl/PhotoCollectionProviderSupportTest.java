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

import java.util.Collection;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PhotoCollectionProviderSupportTest extends PhotoCollectionProviderTestSupport
  {
    @Test
    public void must_properly_parse_PhotoItems()
      throws Exception
      {
        // given
        final PhotoCollectionProviderSupport underTest = new PhotoCollectionProviderSupport(URL_MOCK_RESOURCE);
        // when
        final Collection<PathAwareEntity> photoItems =
                underTest.findPhotos(mediaFolder, URL_MOCK_RESOURCE + "/themes/castles/images.xml");
        // then
        dumpAndAssertResults("photoItems.txt", photoItems);
      }
  }
