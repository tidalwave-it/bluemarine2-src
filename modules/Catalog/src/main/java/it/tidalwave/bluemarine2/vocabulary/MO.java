/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.vocabulary;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/***********************************************************************************************************************
 *
 * See http://musicontology.com/specification/
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MO 
  {
    private final static ValueFactory factory = ValueFactoryImpl.getInstance();
    
    private static final String PREFIX = "http://purl.org/ontology/mo/";
    
    // Classes
    public static final String S_C_AUDIO_FILE     = PREFIX + "AudioFile";
    public static final String S_C_CD             = PREFIX + "CD";
    public static final String S_C_MUSIC_ARTIST   = PREFIX + "MusicArtist";
    public static final String S_C_RECORD         = PREFIX + "Record";
    public static final String S_C_TRACK          = PREFIX + "Track";
    
    public static final URI C_AUDIO_FILE          = factory.createURI(S_C_AUDIO_FILE);
    public static final URI C_CD                  = factory.createURI(S_C_CD);
    public static final URI C_MUSIC_ARTIST        = factory.createURI(S_C_MUSIC_ARTIST);
    public static final URI C_RECORD              = factory.createURI(S_C_RECORD);
    public static final URI C_TRACK               = factory.createURI(S_C_TRACK);

    // Properties
    public static final URI P_BITS_PER_SAMPLE     = factory.createURI(PREFIX + "bitsPerSample");
    public static final URI P_DURATION            = factory.createURI(PREFIX + "duration");
    public static final URI P_TRACK               = factory.createURI(PREFIX + "track");
    public static final URI P_MEDIA_TYPE          = factory.createURI(PREFIX + "media_type");
    public static final URI P_MUSICBRAINZ_GUID    = factory.createURI(PREFIX + "musicbrainz_guid");
    public static final URI P_PUBLISHED_AS        = factory.createURI(PREFIX + "published_as");
    public static final URI P_SAMPLE_RATE         = factory.createURI(PREFIX + "sampleRate");
    public static final URI P_TRACK_COUNT         = factory.createURI(PREFIX + "track_count");
    public static final URI P_TRACK_NUMBER        = factory.createURI(PREFIX + "track_number");
  }
