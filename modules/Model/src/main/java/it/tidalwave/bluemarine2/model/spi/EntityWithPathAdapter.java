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
package it.tidalwave.bluemarine2.model.spi;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.nio.file.Path;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.EntityWithPath;
import it.tidalwave.bluemarine2.model.impl.EntityAdapterSupport;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An adapter for {@link Entity} to a {@link MediaFolder}. It can be used to adapt entities that naturally do
 * not belong to a hierarchy, such as an artist, to contexts where a hierarchy is needed (e.g. for browsing).
 *
 * @stereotype Datum
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class EntityWithPathAdapter extends EntityAdapterSupport<Entity> implements EntityWithPath
  {
    public EntityWithPathAdapter (final @Nonnull Entity adaptee,
                                  final @Nonnull Path pathSegment)
      {
        super(pathSegment, adaptee, Optional.empty());
      }

    public EntityWithPathAdapter (final @Nonnull Entity adaptee,
                                  final @Nonnull Path pathSegment,
                                  final @Nonnull EntityWithPath parent,
                                  final @Nonnull String displayName,
                                  final @Nonnull Object ... roles)
      {
        super(parent.getPath().resolve(pathSegment),
              adaptee,
              Optional.of(parent),
              computeRoles(parent, pathSegment, displayName, roles));
      }
  }
