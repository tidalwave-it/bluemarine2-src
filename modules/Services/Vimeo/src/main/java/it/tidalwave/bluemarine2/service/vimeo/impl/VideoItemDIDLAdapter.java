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
package it.tidalwave.bluemarine2.service.vimeo.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.util.Arrays;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;
import org.fourthline.cling.support.model.item.VideoItem;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter.ContentHolder;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * A role that converts a {@link PhotoItem} into DIDL content.
 *
 * @stereotype  Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
 // FIXME: this introduces a dependency on UPnP. It's needed because it contains stuff related to StoppingDown (URLs).
 // FIXME: move PhotoItem to Model, this class to UPnP and try to make the URLs contained in metadata of PhotoItem.
@RequiredArgsConstructor
@Immutable @DciRole(datumType = VideoMediaItem.class)
public class VideoItemDIDLAdapter implements DIDLAdapter
  {
    @Nonnull
    private final VideoMediaItem datum;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public ContentHolder toContent (final BrowseFlag browseFlag, final int from, final int maxResults)
      {
        final DIDLContent content = new DIDLContent();
        content.addObject(toObject());
        return new ContentHolder(content, 1, 1);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public DIDLObject toObject()
      {
        final ProtocolInfo protocolInfo = new DLNAProtocolInfo(Protocol.HTTP_GET, "*", datum.getMimeType(), "*");
        final Path parentPath = datum.getParent().get().getPath();
        final VideoItem item = new VideoItem();
        item.setId(parentPath.resolve(datum.getId()).toString()); // FIXME: use relativePath
        item.setParentID(parentPath.toString());
        item.setTitle(datum.getTitle());
        item.setRestricted(false);
        item.setCreator("unknown");
        item.setResources(Arrays.asList(new Res(protocolInfo, null, datum.getUrl())));
//        item.setDescription(datum.getTitle());
        return item;
      }
  }
