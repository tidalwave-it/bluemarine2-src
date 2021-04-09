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
import java.util.Collections;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import it.tidalwave.util.As;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.Identifiable._Identifiable_;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.UpnpUtilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public abstract class DIDLAdapterSupport<T extends As> implements DIDLAdapter
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
    protected <D extends DIDLObject> D setCommonFields (@Nonnull final D didlObject)
      {
        didlObject.setRestricted(false);
        didlObject.setCreator("blueMarine II"); // FIXME
        datum.maybeAs(_Identifiable_).ifPresent(identifiable -> didlObject.setId(externalized(identifiable.getId().stringValue())));
        datum.maybeAs(_Displayable_).map(displayable -> didlObject.setTitle(displayable.getDisplayName()));

        if (didlObject instanceof Container)
          {
            final Container container = (Container)didlObject;
            datum.maybeAs(_SimpleComposite_).ifPresent(c -> container.setChildCount(c.findChildren().count()));
//
//          ALTERNATE FIX, BAD
//            final Optional<Integer> x = datum.asOptional(Record.class).flatMap(r -> r.getTrackCount());
//
//            if (x.isPresent())
//              {
//                x.ifPresent(container::setChildCount);
//              }
//            else
//              {
//                datum.asOptional(SimpleComposite8).ifPresent(c -> container.setChildCount(c.findChildren().count()));
//              }

            container.setItems(Collections.emptyList());
          }

        return didlObject;
      }
  }
