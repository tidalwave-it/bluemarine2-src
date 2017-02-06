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
import javax.annotation.concurrent.Immutable;
import org.fourthline.cling.support.model.DIDLObject;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The {@link DIDLAdapter} for {@link MusicArtist}.
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @DciRole(datumType = MusicArtist.class) @Slf4j
public class MusicArtistDIDLAdapter extends DIDLAdapterSupport<MusicArtist>
  {
    public MusicArtistDIDLAdapter (final @Nonnull MusicArtist datum, final @Nonnull ResourceServer server)
      {
        super(datum, server);
      }

    @Override @Nonnull
    public DIDLObject toObject()
      {
        log.debug("toObject() - {}", datum);
        // parentID not set here
        return setCommonFields(new org.fourthline.cling.support.model.container.MusicArtist());
      }
  }
