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
package it.tidalwave.bluemarine2.model.vocabulary;

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BM
  {
    private final static ValueFactory factory = ValueFactoryImpl.getInstance();

    public static final String PREFIX                   = "http://bluemarine.tidalwave.it/2015/04/mo/";

    public static final String S_LATEST_INDEXING_TIME   = PREFIX + "latestIndexingTime";
    public static final String S_DISK_NUMBER            = PREFIX + "diskNumber";
    public static final String S_DISK_COUNT             = PREFIX + "diskCount";
    public static final String S_PATH                   = PREFIX + "path";
    public static final String S_FILE_SIZE              = PREFIX + "fileSize";

//    public static final String S_FULL_CREDITS           = PREFIX + "fullCredits";

//    public static final URI FULL_CREDITS                = factory.createURI(S_FULL_CREDITS);

    /** The file timestamp the latest time it was indexed. */
    public static final URI LATEST_INDEXING_TIME        = factory.createURI(S_LATEST_INDEXING_TIME);

    public static final URI DISK_NUMBER                 = factory.createURI(S_DISK_NUMBER);

    public static final URI DISK_COUNT                  = factory.createURI(S_DISK_COUNT);

    public static final URI PATH                        = factory.createURI(S_PATH);

    public static final URI FILE_SIZE                   = factory.createURI(S_FILE_SIZE);
//    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
//    public static final URI LATEST_MB_METADATA          = factory.createURI(PREFIX + "latestMusicBrainzMetadata");
//
//    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
//    public static final URI FAILED_MB_METADATA          = factory.createURI(PREFIX + "failedMusicBrainzMetadata");

    @Nonnull
    public static URI audioFileUriFor (final @Nonnull Id id)
      {
        return factory.createURI("urn:bluemarine:audiofile:" + id.stringValue());
      }

    @Nonnull
    public static URI signalUriFor (final @Nonnull Id id)
      {
        return factory.createURI("urn:bluemarine:signal:" + id.stringValue());
      }

    @Nonnull
    public static URI localTrackUriFor (final @Nonnull Id id)
      {
        return factory.createURI("urn:bluemarine:track:" + id.stringValue());
      }

    @Nonnull
    public static URI localRecordUriFor (final @Nonnull Id id)
      {
        return factory.createURI("urn:bluemarine:record:" + id.stringValue());
      }

    @Nonnull
    public static URI localArtistUriFor (final @Nonnull Id id)
      {
        return factory.createURI("urn:bluemarine:artist:" + id.stringValue());
      }

    @Nonnull
    public static URI musicBrainzUriFor (final @Nonnull String resourceType, final @Nonnull Id id)
      {
        return factory.createURI(String.format("http://dbtune.org/musicbrainz/resource/%s/%s",
                                               resourceType, id.stringValue()));
      }
  }
