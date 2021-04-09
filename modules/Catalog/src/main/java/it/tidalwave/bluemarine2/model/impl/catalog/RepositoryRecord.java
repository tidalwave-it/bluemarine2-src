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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.net.URL;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.BindingSet;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryOptimizedTrackFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.finder.RepositoryRecordImageFinder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An implementation of {@link Record} that is mapped to a {@link Repository}.
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @Getter @Slf4j
public class RepositoryRecord extends RepositoryEntitySupport implements Record
  {
    @Getter @Nonnull
    private final Optional<Integer> diskNumber;

    @Getter @Nonnull
    private final Optional<Integer> diskCount;

    @Getter @Nonnull
    private final Optional<Integer> trackCount; // FIXME: should be used as a shortcut for queries

    @Getter @Nonnull
    private final Optional<String> asin;

    @Getter @Nonnull
    private final Optional<String> gtin;

    public RepositoryRecord (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        super(repository, bindingSet, "record");
        diskNumber = toInteger(bindingSet.getBinding("disk_number"));
        diskCount  = toInteger(bindingSet.getBinding("disk_count"));
        trackCount = toInteger(bindingSet.getBinding("track_count"));
        asin       = toString(bindingSet.getBinding("asin"));
        gtin       = toString(bindingSet.getBinding("gtin"));
      }

    @Override @Nonnull
    public TrackFinder findTracks()
      {
        return configured(new RepositoryOptimizedTrackFinder(repository, trackCount)).inRecord(this);
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
