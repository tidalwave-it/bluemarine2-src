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
    
    public static final URI AUDIOFILE           = factory.createURI(PREFIX + "AudioFile");
    public static final URI CD                  = factory.createURI(PREFIX + "CD");
    public static final URI MUSIC_ARTIST        = factory.createURI(PREFIX + "MusicArtist");
    public static final URI RECORD              = factory.createURI(PREFIX + "Record");
    public static final URI TRACK               = factory.createURI(PREFIX + "Track");

    public static final URI BITS_PER_SAMPLE     = factory.createURI(PREFIX + "bitsPerSample");
    public static final URI DURATION            = factory.createURI(PREFIX + "duration");
    public static final URI _TRACK              = factory.createURI(PREFIX + "track");
    public static final URI MEDIA_TYPE          = factory.createURI(PREFIX + "media_type");
    public static final URI MUSICBRAINZ         = factory.createURI(PREFIX + "musicbrainz");
    public static final URI MUSICBRAINZ_GUID    = factory.createURI(PREFIX + "musicbrainz_guid");
    public static final URI SAMPLE_RATE         = factory.createURI(PREFIX + "sampleRate");
    public static final URI TRACK_COUNT         = factory.createURI(PREFIX + "track_count");
    public static final URI TRACK_NUMBER        = factory.createURI(PREFIX + "track_number");
  }
