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

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MockPhotoCollectionProvider implements PhotoCollectionProvider
  {
    @Override @Nonnull
    public List<String> getPhotoIds()
      {
        return Arrays.asList("20071209-0072",
                             "20080223-0086",
                             "20151107-0301a",
                             "20151107-0315",
                             "20151107-0380",
                             "20160306-0100",
                             "20160306-0120",
                             "20160306-0132",
                             "20160306-0141",
                             "20160306-0233",
                             "20160306-0235",
                             "20160306-0587",
                             "20160306-0649",
                             "20160306-0715");

      }
  }
