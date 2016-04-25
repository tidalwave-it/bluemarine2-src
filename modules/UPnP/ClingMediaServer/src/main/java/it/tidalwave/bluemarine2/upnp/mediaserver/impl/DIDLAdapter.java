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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;

/***********************************************************************************************************************
 *
 * @stereotype  Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface DIDLAdapter
  {
    public static final Class<DIDLAdapter> DIDLAdapter = DIDLAdapter.class;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public DIDLContent toContent (@Nonnull BrowseFlag browseFlag,
                                  @Nonnegative int from,
                                  @Nonnegative int maxResults);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public DIDLObject toObject();

    /*******************************************************************************************************************
     *
     * Returns the number of items that are being returned - this take into account the fact that the client has
     * requested a subset of data.
     *
     * This method provides a meaningful result only after
     * {@link #toContent(org.fourthline.cling.support.model.BrowseFlag, int, int)} has been called.
     *
     * @return  the number of items being returned.
     *
     ******************************************************************************************************************/
    @Nonnegative
    public default int getNumberReturned()
      {
        return 1;
      }

    /*******************************************************************************************************************
     *
     * Returns the number of items that would match the client request.
     *
     * This method provides a meaningful result only after
     * {@link #toContent(org.fourthline.cling.support.model.BrowseFlag, int, int)} has been called.
     *
     * @return  the number of items that matched.
     *
     ******************************************************************************************************************/
    @Nonnegative
    public default int getTotalMatches()
      {
        return 1;
      }
  }
