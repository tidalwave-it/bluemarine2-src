/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
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
import java.util.Collections;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import it.tidalwave.util.As8;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.Identifiable.Identifiable;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.UpnpUtilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public abstract class DIDLAdapterSupport<T extends As8> implements DIDLAdapter
  {
    @Nonnull
    protected final T datum;

    @Nonnull
    protected final ResourceServer server;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected <X extends DIDLObject> X setCommonFields (final @Nonnull X didlObject)
      {
        didlObject.setRestricted(false);
        didlObject.setCreator("blueMarine II"); // FIXME
        datum.asOptional(Identifiable).ifPresent(identifiable -> didlObject.setId(externalized(identifiable.getId().stringValue())));
        datum.asOptional(Displayable).map(displayable -> didlObject.setTitle(displayable.getDisplayName()));

        if (didlObject instanceof Container)
          {
            final Container container = (Container)didlObject;
            datum.asOptional(SimpleComposite8).ifPresent(c -> container.setChildCount(c.findChildren().count()));
            container.setItems(Collections.emptyList());
          }

//        if (datum instanceof PathAwareEntity)
//          {
//            didlObject.setParentID(((PathAwareEntity)datum).getParent().map(p -> p.getPath().toString()).orElse(ID_NONE));
//          }

        return didlObject;
      }
  }
