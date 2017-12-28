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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom.Gallery;
import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom.GalleryFinder;
import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom6.jpa.AgLibraryCollection;
import lombok.RequiredArgsConstructor;
import static java.util.stream.Collectors.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id:$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class DefaultGalleryFinder extends Finder8Support<Gallery, GalleryFinder> implements GalleryFinder
  {
    private static final long serialVersionUID = 7871326166118429273L;

    private static final String query = "SELECT lc FROM AgLibraryCollection lc "
                                      + "WHERE lc.creationId='com.adobe.ag.webGallery' "
                                      + "ORDER BY lc.name";

    @Nonnull // FIXME: @Inject
    private final EntityManager em;

    public DefaultGalleryFinder (final @Nonnull DefaultGalleryFinder other, final @Nonnull Object override)
      {
        this(other.em);
      }

    @Override @Nonnull
    protected List<? extends Gallery> computeNeededResults()
      {
        return em.createQuery(query, AgLibraryCollection.class).getResultList().stream()
                .map(lc -> DefaultGallery.fromEntity(lc, em))
                .collect(toList());
      }
  }
