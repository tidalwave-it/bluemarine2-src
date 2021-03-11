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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;

/***********************************************************************************************************************
 *
 * An adapter which converts an object into DIDL stuff.
 *
 * @stereotype  Role, Adapter
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface DIDLAdapter
  {
    public static final Class<DIDLAdapter> DIDLAdapter = DIDLAdapter.class;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter @EqualsAndHashCode  @ToString
    public static class ContentHolder
      {
        /**
         * The {@link DIDLContent}.
         */
        @Nonnull
        private final DIDLContent content;

        /**
         * The number of items that are being returned - this take into account the fact that the client has
         * requested a subset of data.
         */
        @Nonnegative
        private final int numberReturned;

        /**
         * The number of items that would match the client request.
         */
        @Nonnegative
        private final int totalMatches;
      }

    /*******************************************************************************************************************
     *
     * Converts the owner object to a {@link DIDLContent}.
     *
     * @param   browseFlag  whether metadata for a single object or enumeration of children should be returned
     * @param   from        in case of multiple results, the first item to return
     * @param   maxResults  in case of multiple results, how many items to return
     * @return              the holder of {@code DIDLContent}
     *
     ******************************************************************************************************************/
    @Nonnull
    public default ContentHolder toContent (final @Nonnull BrowseFlag browseFlag,
                                            final @Nonnegative int from,
                                            final @Nonnegative int maxResults)
      throws Exception
      {
        final DIDLContent content = new DIDLContent();
        content.addObject(toObject());
        return new ContentHolder(content, 1, 1);
      }

    /*******************************************************************************************************************
     *
     * Converts the owner object to a {@link DIDLObject}.
     *
     * @return              the {@code DIDLObject}
     *
     ******************************************************************************************************************/
    @Nonnull
    public DIDLObject toObject()
      throws Exception;
  }
