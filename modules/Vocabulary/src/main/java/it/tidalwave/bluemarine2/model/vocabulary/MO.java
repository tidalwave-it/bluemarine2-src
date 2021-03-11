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
package it.tidalwave.bluemarine2.model.vocabulary;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/***********************************************************************************************************************
 *
 * See http://musicontology.com/specification/
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MO
  {
    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

    private static final String NS = "http://purl.org/ontology/mo/";

    // Classes
    public static final String S_C_AUDIO_FILE       = NS + "AudioFile";
    public static final String S_C_CD               = NS + "CD";
    public static final String S_C_DIGITAL_SIGNAL   = NS + "DigitalSignal";
    public static final String S_C_MUSIC_ARTIST     = NS + "MusicArtist";
    public static final String S_C_PERFORMANCE      = NS + "Performance";
    public static final String S_C_RECORD           = NS + "Record";
    public static final String S_C_TRACK            = NS + "Track";

    public static final IRI C_AUDIO_FILE            = FACTORY.createIRI(S_C_AUDIO_FILE);
    public static final IRI C_CD                    = FACTORY.createIRI(S_C_CD);
    public static final IRI C_DIGITAL_SIGNAL        = FACTORY.createIRI(S_C_DIGITAL_SIGNAL);
    public static final IRI C_MUSIC_ARTIST          = FACTORY.createIRI(S_C_MUSIC_ARTIST);
    public static final IRI C_PERFORMANCE           = FACTORY.createIRI(S_C_PERFORMANCE);
    public static final IRI C_RECORD                = FACTORY.createIRI(S_C_RECORD);
    public static final IRI C_TRACK                 = FACTORY.createIRI(S_C_TRACK);

    // Predicates
    public static final String S_P_AMAZON_ASIN      = NS + "amazon_asin";
    public static final String S_P_BITS_PER_SAMPLE  = NS + "bitsPerSample";
    public static final String S_P_DURATION         = NS + "duration";
    public static final String S_P_ENCODES          = NS + "encodes";
    public static final String S_P_GTIN             = NS + "gtin";
    public static final String S_P_TRACK            = NS + "track";
    public static final String S_P_MEDIA_TYPE       = NS + "media_type";
    public static final String S_P_MUSICBRAINZ      = NS + "musicbrainz";
    public static final String S_P_MUSICBRAINZ_GUID = NS + "musicbrainz_guid";
    public static final String S_P_PUBLISHED_AS     = NS + "published_as";
    public static final String S_P_SAMPLE_RATE      = NS + "sampleRate";
    public static final String S_P_TRACK_COUNT      = NS + "track_count";
    public static final String S_P_TRACK_NUMBER     = NS + "track_number";

    public static final String S_P_CONDUCTOR        = NS + "conductor";
    public static final String S_P_ENGINEER         = NS + "engineer";
    public static final String S_P_PERFORMER        = NS + "performer";
    public static final String S_P_PRODUCER         = NS + "producer";
    public static final String S_P_RECORDED_AS      = NS + "recorded_as";
    public static final String S_P_SINGER           = NS + "singer";

    public static final IRI P_AMAZON_ASIN           = FACTORY.createIRI(S_P_AMAZON_ASIN);
    public static final IRI P_BITS_PER_SAMPLE       = FACTORY.createIRI(S_P_BITS_PER_SAMPLE);
    public static final IRI P_DURATION              = FACTORY.createIRI(S_P_DURATION);
    public static final IRI P_ENCODES               = FACTORY.createIRI(S_P_ENCODES);
    public static final IRI P_GTIN                  = FACTORY.createIRI(S_P_GTIN);
    public static final IRI P_TRACK                 = FACTORY.createIRI(S_P_TRACK);
    public static final IRI P_MEDIA_TYPE            = FACTORY.createIRI(S_P_MEDIA_TYPE);
    public static final IRI P_MUSICBRAINZ           = FACTORY.createIRI(S_P_MUSICBRAINZ);
    public static final IRI P_MUSICBRAINZ_GUID      = FACTORY.createIRI(S_P_MUSICBRAINZ_GUID);
    public static final IRI P_PUBLISHED_AS          = FACTORY.createIRI(S_P_PUBLISHED_AS);
    public static final IRI P_SAMPLE_RATE           = FACTORY.createIRI(S_P_SAMPLE_RATE);
    public static final IRI P_TRACK_COUNT           = FACTORY.createIRI(S_P_TRACK_COUNT);
    public static final IRI P_TRACK_NUMBER          = FACTORY.createIRI(S_P_TRACK_NUMBER);

    public static final IRI P_CONDUCTOR             = FACTORY.createIRI(S_P_CONDUCTOR);
    public static final IRI P_ENGINEER              = FACTORY.createIRI(S_P_ENGINEER);
    public static final IRI P_PERFORMER             = FACTORY.createIRI(S_P_PERFORMER);
    public static final IRI P_PRODUCER              = FACTORY.createIRI(S_P_PRODUCER);
    public static final IRI P_RECORDED_AS           = FACTORY.createIRI(S_P_RECORDED_AS);
    public static final IRI P_SINGER                = FACTORY.createIRI(S_P_SINGER);
  }
