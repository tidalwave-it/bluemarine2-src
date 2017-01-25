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
package it.tidalwave.bluemarine2.model.impl.catalog.browser;

import org.springframework.core.annotation.Order;
import it.tidalwave.util.As;
import it.tidalwave.util.DefaultFilterSortCriterion;
import it.tidalwave.text.AsDisplayableComparator;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Order(40)
public class RepositoryBrowserByTrack extends RepositoryBrowserSupport
  {
    static class ByTrackName extends DefaultFilterSortCriterion<As>
      {
        private static final long serialVersionUID = -4197094807679806523L;

        public ByTrackName()
          {
            super(new AsDisplayableComparator(), "---");
          }
      }

    public RepositoryBrowserByTrack()
      {
        super(catalog -> catalog.findTracks().sort(new ByTrackName()));
      }
  }
