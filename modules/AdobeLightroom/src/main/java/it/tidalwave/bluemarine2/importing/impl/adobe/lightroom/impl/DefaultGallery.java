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
package it.tidalwave.bluemarine2.importing.impl.adobe.lightroom.impl;

import javax.annotation.Nonnull;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom.*;
import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom6.jpa.AgLibraryCollection;
import it.tidalwave.util.Id;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id:$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Getter @ToString(of = { "name", "id" })
public class DefaultGallery implements Gallery, Identifiable
  {
    @Nonnull
    private final EntityManager em;

    @Nonnull
    private final Id id;

    @Nonnull
    private final String name;

    @Nonnull
    private final Optional<Id> parentId;

    @Override @Nonnull
    public Optional<Gallery> getParent()
      {
        return parentId.map(pid -> fromEntity(em.find(AgLibraryCollection.class, Integer.parseInt(pid.stringValue())), em));
      }

    @Nonnull
    protected static DefaultGallery fromEntity (final @Nonnull AgLibraryCollection lc, final @Nonnull EntityManager em)
      {
          // FIXME: use flyweight
        return new DefaultGallery(em, new Id(lc.getIdLocal()), lc.getName(), Optional.ofNullable(lc.getParent()).map(Id::new));
      }
  }
