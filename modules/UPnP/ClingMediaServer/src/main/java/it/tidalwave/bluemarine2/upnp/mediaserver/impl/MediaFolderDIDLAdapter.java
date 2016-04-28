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

import javax.annotation.concurrent.Immutable;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Collections;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.StorageFolder;
import it.tidalwave.util.AsException;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.role.Parentable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.bluemarine2.model.role.Parentable.Parentable;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.UPnPUtilities.*;

/***********************************************************************************************************************
 *
 * An implementation of {@link DIDLAdapter} for {@link MediaFolder}.
 *
 * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
 *
 * @stereotype  Role, Adapter
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
@Immutable @DciRole(datumType = MediaFolder.class)
public class MediaFolderDIDLAdapter implements DIDLAdapter
  {
    @Nonnull
    private final MediaFolder datum;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public ContentHolder toContent (final @Nonnull BrowseFlag browseFlag,
                                    final @Nonnegative int from,
                                    final @Nonnegative int maxResults)
      {
        final DIDLContent content = new DIDLContent();
        int numberReturned = 0;
        int totalMatches = 0;

        switch (browseFlag)
          {
            case METADATA:
                totalMatches = numberReturned = 1;
                content.addObject(toObject());
                break;

            case DIRECT_CHILDREN:
                final EntityFinder finder = datum.findChildren();
                totalMatches = finder.count();
                finder.from(from)
                      .max(maxResults)
                      .results()
                      .stream()
                      .forEach(child -> content.addObject(child.as(DIDLAdapter).toObject()));
                numberReturned = (int)content.getCount();
                break;

            default:
                throw new IllegalArgumentException(browseFlag.toString());
          }

        return new ContentHolder(content, numberReturned, totalMatches);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public DIDLObject toObject()
      {
        final Container container = new Container();
        container.setClazz(StorageFolder.CLASS);
        container.setRestricted(false);
        container.setId(pathToDidlId(datum.getPath()));
        container.setTitle(datum.as(Displayable).getDisplayName());
        container.setCreator("blueMarine II"); // FIXME
        container.setChildCount(datum.findChildren().count());
        container.setItems(Collections.emptyList());

        try
          {
            final Parentable<MediaFolder> parentable = datum.as(Parentable);
            container.setParentID(parentable.hasParent() ? pathToDidlId(parentable.getParent().getPath()) : ID_NONE);
          }
        catch (AsException e)
          {
            container.setParentID(ID_NONE);
          }
//                container.getSearchClasses().add(PhotoAlbum.CLASS);
//                container.getSearchClasses().add(MusicAlbum.CLASS);
//                container.getSearchClasses().add("object.item.imageItem.photo");

        return container;
      }
  }
