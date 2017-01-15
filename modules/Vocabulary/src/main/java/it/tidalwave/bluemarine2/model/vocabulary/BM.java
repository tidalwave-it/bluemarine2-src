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
package it.tidalwave.bluemarine2.model.vocabulary;

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BM
  {
    private final static ValueFactory factory = SimpleValueFactory.getInstance();

    public static final String PREFIX                   = "http://bluemarine.tidalwave.it/2015/04/mo/";

    public static final String S_LATEST_INDEXING_TIME   = PREFIX + "latestIndexingTime";
    public static final String S_DISK_NUMBER            = PREFIX + "diskNumber";
    public static final String S_DISK_COUNT             = PREFIX + "diskCount";
    public static final String S_PATH                   = PREFIX + "path";
    public static final String S_FILE_SIZE              = PREFIX + "fileSize";
    public static final String S_ITUNES_CDDB1           = PREFIX + "iTunesCddb1";

//    public static final String S_FULL_CREDITS           = PREFIX + "fullCredits";

//    public static final IRI FULL_CREDITS                = factory.createIRI(S_FULL_CREDITS);

    /** The file timestamp the latest time it was indexed. */
    public static final IRI LATEST_INDEXING_TIME        = factory.createIRI(S_LATEST_INDEXING_TIME);

    public static final IRI DISK_NUMBER                 = factory.createIRI(S_DISK_NUMBER);

    public static final IRI DISK_COUNT                  = factory.createIRI(S_DISK_COUNT);

    public static final IRI PATH                        = factory.createIRI(S_PATH);

    public static final IRI FILE_SIZE                   = factory.createIRI(S_FILE_SIZE);

    public static final IRI ITUNES_CDDB1                = factory.createIRI(S_ITUNES_CDDB1);

//    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
//    public static final IRI LATEST_MB_METADATA          = factory.createIRI(PREFIX + "latestMusicBrainzMetadata");
//
//    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
//    public static final IRI FAILED_MB_METADATA          = factory.createIRI(PREFIX + "failedMusicBrainzMetadata");

    @Nonnull
    public static IRI audioFileIriFor (final @Nonnull Id id)
      {
        return factory.createIRI("urn:bluemarine:audiofile:" + id.stringValue());
      }

    @Nonnull
    public static IRI signalIriFor (final @Nonnull Id id)
      {
        return factory.createIRI("urn:bluemarine:signal:" + id.stringValue());
      }

    @Nonnull
    public static IRI localTrackIriFor (final @Nonnull Id id)
      {
        return factory.createIRI("urn:bluemarine:track:" + id.stringValue());
      }

    @Nonnull
    public static IRI localRecordIriFor (final @Nonnull Id id)
      {
        return factory.createIRI("urn:bluemarine:record:" + id.stringValue());
      }

    @Nonnull
    public static IRI localArtistIriFor (final @Nonnull Id id)
      {
        return factory.createIRI("urn:bluemarine:artist:" + id.stringValue());
      }

    @Nonnull
    public static IRI musicBrainzIriFor (final @Nonnull String resourceType, final @Nonnull Id id)
      {
        return factory.createIRI(String.format("http://dbtune.org/musicbrainz/resource/%s/%s",
                                               resourceType, id.stringValue()));
      }
  }
