/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.StorageFolder;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A fallback {@link DIDLAdapter} for any {@link Entity}, used if no more specialised roles are found.
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @Slf4j @ToString @DciRole(datumType = Entity.class)
public class EntityDIDLAdapter extends CompositeDIDLAdapterSupport<Entity>
  {
    public EntityDIDLAdapter (final @Nonnull Entity datum, final @Nonnull ResourceServer server)
      {
        super(datum, server);
      }

    @Override @Nonnull
    public DIDLObject toObject()
      {
        log.debug("toObject() - {}", datum);
        final Container container = setCommonFields(new Container());
        container.setClazz(StorageFolder.CLASS); // FIXME: or Container?
        return container;
      }
  }
