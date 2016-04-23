/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import it.tidalwave.bluemarine2.upnp.mediaserver.impl.DIDLAdapter;
import it.tidalwave.dci.annotation.DciRole;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;
import org.fourthline.cling.support.model.item.Photo;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
@DciRole(datumType = PhotoItem.class)
public class PhotoItemDIDLAdapter implements DIDLAdapter
  {
    @Nonnull
    private final PhotoItem photo;

    private final String creator = "Fabrizio Giudici";

    @Override
    public DIDLContent toContent (final BrowseFlag browseFlag, final int from, final int maxResults)
      {
        throw new UnsupportedOperationException();
      }

    @Override
    public DIDLObject toObject()
      {
        final ProtocolInfo protocolInfo = new DLNAProtocolInfo(Protocol.HTTP_GET, "*", "image/jpeg", "*");
        final String url = String.format("http://stoppingdown.net/media/stillimages/%s/%d/image.jpg", photo.getId(), 2560);
        final Res resource = new Res(protocolInfo, null, url);
        final String parentId = photo.getParent().getPath().toString();
        final Photo item = new Photo(photo.getId(), parentId, photo.getId(), creator, parentId, resource);
        return item;
      }
  }
