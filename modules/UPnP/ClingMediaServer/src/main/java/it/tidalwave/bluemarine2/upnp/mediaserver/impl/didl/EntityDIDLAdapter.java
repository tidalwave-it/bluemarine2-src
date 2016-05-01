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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.StorageFolder;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.bluemarine2.model.Entity;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Identifiable.Identifiable;

/***********************************************************************************************************************
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
@Immutable //@DciRole(datumType = Entity.class)
public class EntityDIDLAdapter extends CompositeDIDLAdapterSupport<Entity>
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public EntityDIDLAdapter (final @Nonnull Entity datum)
      {
        super(datum);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public DIDLObject toObject()
      {
        log.trace("toObject() - {}", datum);
        final Container container = new Container();
        container.setId(datum.asOptional(Identifiable).map(d -> d.getId().stringValue()).orElse("???")); // FIXME
        container.setParentID("parentId"); // FIXME
        container.setRestricted(false);

        datum.asOptional(SimpleComposite8.class).ifPresent(c -> container.setChildCount(c.findChildren().count()));
        // FIXME: missing children count
//        container.set(new DIDLObject.Class(datum.getClass().getName()));
        container.setClazz(StorageFolder.CLASS); // FIXME: or Container?
        container.setTitle(datum.asOptional(Displayable.Displayable).map(d -> d.getDisplayName()).orElse("???"));
        return container;
      }
  }