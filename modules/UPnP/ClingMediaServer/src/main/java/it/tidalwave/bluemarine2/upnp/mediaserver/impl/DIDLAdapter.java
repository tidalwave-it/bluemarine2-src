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
import java.util.Collections;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.StorageFolder;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.Finder;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.role.Parentable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.Identifiable.Identifiable;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static it.tidalwave.bluemarine2.model.role.Parentable.Parentable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DIDLAdapter // FIXME: turn into @DciRole
  {
    private static final String ID_NONE = "-1";

    @Nonnull
    private final Entity owner;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public DIDLContent toContent (final @Nonnull BrowseFlag browseFlag,
                                  final @Nonnegative int from,
                                  final @Nonnegative int maxResults)
      {
        final DIDLContent content = new DIDLContent();

        switch (browseFlag)
          {
            case METADATA:
                content.addContainer(createContainer(owner));
                break;

            case DIRECT_CHILDREN:
                try
                  {
                    final Finder<Entity> finder = owner.as(SimpleComposite8).findChildren();
                    finder.from(from)
//                          .max(maxResults) FIXME: doesn't work
                          .results()
                          .stream()
                          .forEach(child -> content.addContainer(createContainer(child)));
                  }
                catch (AsException e) // no Composite
                  {
                    log.debug("", e);
                  }

                break;
          }

        return content;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Container createContainer (final @Nonnull Entity entity)
      {
        final Container container = new Container();
        container.setClazz(StorageFolder.CLASS);
        container.setRestricted(false);
        container.setId(entity.as(Identifiable).getId().stringValue()); // FIXME: translate "0" for root
        container.setTitle(entity.as(Displayable).getDisplayName());
        container.setCreator("blueMarine II"); // FIXME
        container.setChildCount(0);
        container.setItems(Collections.emptyList());

        try
          {
            final Parentable<As> parentable = entity.as(Parentable);
            container.setParentID(parentable.hasParent()
                    ? parentable.getParent().as(Identifiable).getId().stringValue()
                    : ID_NONE);
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
