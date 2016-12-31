/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.net.URL;
import org.openrdf.repository.Repository;
import org.openrdf.query.BindingSet;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.finder.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordImageFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryTrackFinder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An implementation of {@link Record} that is mapped to a {@link Repository}.
 * 
 * @stereotype  Datum
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @Slf4j
public class RepositoryRecord extends RepositoryEntitySupport implements Record
  {
    public RepositoryRecord (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "record");
      }
   
    @Override @Nonnull
    public TrackFinder findTracks() 
      {
        return new RepositoryTrackFinder(repository).inRecord(this);
        // FIXME? sorted in the query - .sort(new TrackComparator());
      }

    @Override @Nonnull
    public Optional<URL> getImageUrl() 
      {
        final List<? extends URL> imageUrls = new RepositoryRecordImageFinder(repository, this).results();
        // FIXME: check - images are ordered by size
        Collections.reverse(imageUrls);
        return imageUrls.isEmpty() ? Optional.empty() : Optional.of(imageUrls.get(0));
      }
    
    @Override @Nonnull
    public String toString() 
      {
        return String.format("RepositoryRecord(rdfs:label=%s, %s)", rdfsLabel, id);
      }
  }
