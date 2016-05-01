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
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.spi.EntityWithPathAdapter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
@Immutable @DciRole(datumType = EntityWithPathAdapter.class)
public class EntityWithPathDecoratorDIDLAdapter extends CompositeDIDLAdapterSupport<EntityWithPathAdapter>
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public EntityWithPathDecoratorDIDLAdapter (final @Nonnull EntityWithPathAdapter datum)
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
      throws Exception
      {
        log.debug("toObject() - {}", datum.getAdaptee());
        final DIDLObject item = asDIDLAdapter(datum.getAdaptee()).toObject();
        log.trace(">>>> item: {}", item);
        datum.getParent().ifPresent(parent -> item.setParentID(parent.getPath().toString()));
        item.setId(datum.getPath().toString());
        return item;
      }
  }