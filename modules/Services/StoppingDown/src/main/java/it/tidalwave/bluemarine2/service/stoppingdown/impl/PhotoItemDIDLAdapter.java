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
 */package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Path;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;
import org.fourthline.cling.support.model.item.Photo;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.DIDLAdapter;
import lombok.RequiredArgsConstructor;
import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.toList;

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
@Immutable @DciRole(datumType = PhotoItem.class)
public class PhotoItemDIDLAdapter implements DIDLAdapter
  {
    private static final String MEDIA_URL_TEMPLATE =
            PhotoCollectionProviderSupport.URL_STOPPINGDOWN + "/media/stillimages/%s/%d/image.jpg";

    private static final List<Integer> SIZES = Arrays.asList(200, 400, 800, 1280, 1920, 2560);

    @Nonnull
    private final PhotoItem datum;

    private final String creator = "Fabrizio Giudici";

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
        final ProtocolInfo protocolInfo = new DLNAProtocolInfo(Protocol.HTTP_GET, "*", "image/jpeg", "*");
        final Res[] resources = SIZES.stream()
                                     .sorted(reverseOrder())
                                     .map(size -> createResource(protocolInfo, size))
                                     .collect(toList())
                                     .toArray(new Res[0]);
        final Path parentPath = datum.getParent().getPath();
        final String parentId = parentPath.toString();
        final String photoId = parentPath.resolve(datum.getId()).toString();
        final String title = datum.getId();
        final Photo item = new Photo(photoId, parentId, title, creator, parentId, resources);
        item.setDescription(datum.getTitle());
        item.setDate(dateFor(datum.getId()));
        return item;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Res createResource (final @Nonnull ProtocolInfo protocolInfo, final int size)
      {
        final Res resource = new Res(protocolInfo, null, computeUrl(size));
        resource.setResolution(size, size);
        return resource;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private String computeUrl (final int size)
      {
        return String.format(MEDIA_URL_TEMPLATE, datum.getId(), size);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String dateFor (final String id)
      {
        return String.format("%s-%s-%s", id.substring(0, 4), id.substring(4, 6), id.substring(6, 8));
      }
  }
