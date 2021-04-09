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
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

import javax.annotation.Nonnull;
import java.net.URL;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.util.Finder;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryRecordImageFinder extends RepositoryFinderSupport<URL, Finder<URL>>
//                                         implements TrackFinder
  {
    private static final long serialVersionUID = -9049698559834098380L;

    private final static String QUERY_RECORD_IMAGE = readSparql(RepositoryMusicArtistFinder.class, "RecordImage.sparql");

    @Nonnull
    private final Record record;

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryRecordImageFinder (final @Nonnull Repository repository, final @Nonnull Record record)
      {
        super(repository, "record");
        this.record = record;
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryRecordImageFinder (final @Nonnull RepositoryRecordImageFinder other,
                                        final @Nonnull Object override)
      {
        super(other, override);
        final RepositoryRecordImageFinder source = getSource(RepositoryRecordImageFinder.class, other, override);
        this.record = source.record;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected QueryAndParameters prepareQuery()
      {
        return QueryAndParameters.withSparql(QUERY_RECORD_IMAGE)
                                 .withParameter("record", iriFor(record.getId()));
      }
  }
