/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.impl.PathAwareMediaFolderDecorator;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @DciRole(datumType = PathAwareMediaFolderDecorator.class) @ToString
public class PathAwareMediaFolderDecoratorDIDLAdapter extends PathAwareDecoratorDIDLAdapter
  {
    public PathAwareMediaFolderDecoratorDIDLAdapter (final @Nonnull PathAwareMediaFolderDecorator datum,
                                                     final @Nonnull ResourceServer server)
      {
        super(datum, server);
      }
  }
