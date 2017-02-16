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
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

import javax.annotation.Nonnull;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.finder.AudioFileFinder;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString
public class RepositoryAudioFileFinder extends RepositoryFinderSupport<AudioFile, AudioFileFinder>
                                         implements AudioFileFinder
  {
    private static final long serialVersionUID = -5065322643235453428L;

    private final static String QUERY_PERFORMANCES = readSparql(RepositoryAudioFileFinder.class, "AudioFiles.sparql");

    /*******************************************************************************************************************
     *
     * Default constructor.
     *
     ******************************************************************************************************************/
    public RepositoryAudioFileFinder (final @Nonnull Repository repository)
      {
        super(repository, "audioFile");
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryAudioFileFinder (final @Nonnull RepositoryAudioFileFinder other,
                                      final @Nonnull Object override)
      {
        super(other, override);
//        final RepositoryAudioFileFinder source = getSource(RepositoryAudioFileFinder.class, other, override);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected QueryAndParameters prepareQuery()
      {
        return QueryAndParameters.withSparql(QUERY_PERFORMANCES);
//                                 .withParameter("track",  trackId.map(this::iriFor))
//                                 .withParameter("artist", performerId.map(this::iriFor));
      }
  }
