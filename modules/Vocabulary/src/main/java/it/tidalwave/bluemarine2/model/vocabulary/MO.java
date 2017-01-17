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
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MO
  {
    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

    private static final String PREFIX = "http://purl.org/ontology/mo/";

    // Classes
    public static final String S_C_AUDIO_FILE       = PREFIX + "AudioFile";
    public static final String S_C_CD               = PREFIX + "CD";
    public static final String S_C_DIGITAL_SIGNAL   = PREFIX + "DigitalSignal";
    public static final String S_C_MUSIC_ARTIST     = PREFIX + "MusicArtist";
    public static final String S_C_RECORD           = PREFIX + "Record";
    public static final String S_C_TRACK            = PREFIX + "Track";

    public static final String S_C_PERFORMANCE      = PREFIX + "Performance";

    public static final IRI C_AUDIO_FILE            = FACTORY.createIRI(S_C_AUDIO_FILE);
    public static final IRI C_CD                    = FACTORY.createIRI(S_C_CD);
    public static final IRI C_DIGITAL_SIGNAL        = FACTORY.createIRI(S_C_DIGITAL_SIGNAL);
    public static final IRI C_MUSIC_ARTIST          = FACTORY.createIRI(S_C_MUSIC_ARTIST);
    public static final IRI C_RECORD                = FACTORY.createIRI(S_C_RECORD);
    public static final IRI C_TRACK                 = FACTORY.createIRI(S_C_TRACK);

    public static final IRI C_PERFORMANCE           = FACTORY.createIRI(S_C_PERFORMANCE);

    // Predicates
    public static final String S_P_AMAZON_ASIN      = PREFIX + "amazon_asin";
    public static final String S_P_BITS_PER_SAMPLE  = PREFIX + "bitsPerSample";
    public static final String S_P_DURATION         = PREFIX + "duration";
    public static final String S_P_ENCODES          = PREFIX + "encodes";
    public static final String S_P_GTIN             = PREFIX + "gtin";
    public static final String S_P_TRACK            = PREFIX + "track";
    public static final String S_P_MEDIA_TYPE       = PREFIX + "media_type";
    public static final String S_P_MUSICBRAINZ_GUID = PREFIX + "musicbrainz_guid";
    public static final String S_P_PUBLISHED_AS     = PREFIX + "published_as";
    public static final String S_P_SAMPLE_RATE      = PREFIX + "sampleRate";
    public static final String S_P_TRACK_COUNT      = PREFIX + "track_count";
    public static final String S_P_TRACK_NUMBER     = PREFIX + "track_number";

    public static final String S_P_PERFORMER        = PREFIX + "performer";
    public static final String S_P_CONDUCTOR        = PREFIX + "conductor";
    public static final String S_P_CHOIR            = PREFIX + "choir";
    public static final String S_P_SINGER           = PREFIX + "singer";
    public static final String S_P_LEAD_SINGER      = PREFIX + "lead_singer";
    public static final String S_P_SOPRANO          = PREFIX + "soprano";
    public static final String S_P_RECORDED_AS      = PREFIX + "recorded_as";

    public static final IRI P_AMAZON_ASIN           = FACTORY.createIRI(S_P_AMAZON_ASIN);
    public static final IRI P_BITS_PER_SAMPLE       = FACTORY.createIRI(S_P_BITS_PER_SAMPLE);
    public static final IRI P_DURATION              = FACTORY.createIRI(S_P_DURATION);
    public static final IRI P_ENCODES               = FACTORY.createIRI(S_P_ENCODES);
    public static final IRI P_GTIN                  = FACTORY.createIRI(S_P_GTIN);
    public static final IRI P_TRACK                 = FACTORY.createIRI(S_P_TRACK);
    public static final IRI P_MEDIA_TYPE            = FACTORY.createIRI(S_P_MEDIA_TYPE);
    public static final IRI P_MUSICBRAINZ_GUID      = FACTORY.createIRI(S_P_MUSICBRAINZ_GUID);
    public static final IRI P_PUBLISHED_AS          = FACTORY.createIRI(S_P_PUBLISHED_AS);
    public static final IRI P_SAMPLE_RATE           = FACTORY.createIRI(S_P_SAMPLE_RATE);
    public static final IRI P_TRACK_COUNT           = FACTORY.createIRI(S_P_TRACK_COUNT);
    public static final IRI P_TRACK_NUMBER          = FACTORY.createIRI(S_P_TRACK_NUMBER);

    public static final IRI P_PERFORMER             = FACTORY.createIRI(S_P_PERFORMER);
    public static final IRI P_CONDUCTOR             = FACTORY.createIRI(S_P_CONDUCTOR);
    public static final IRI P_CHOIR                 = FACTORY.createIRI(S_P_CHOIR);
    public static final IRI P_SINGER                = FACTORY.createIRI(S_P_SINGER);
    public static final IRI P_LEAD_SINGER           = FACTORY.createIRI(S_P_LEAD_SINGER);
    public static final IRI P_SOPRANO               = FACTORY.createIRI(S_P_SOPRANO);
    public static final IRI P_RECORDED_AS           = FACTORY.createIRI(S_P_RECORDED_AS);

    // Must be validated
    private static final String S_P_ARRANGER        = PREFIX + "arranger";
    private static final String S_P_BACKGROUND_SINGER = PREFIX + "background_singer";
    private static final String S_P_BALANCE         = PREFIX + "balance";
    private static final String S_P_BASS            = PREFIX + "bass";
    private static final String S_P_BARITONE        = PREFIX + "baritone";
    private static final String S_P_EDITOR          = PREFIX + "editor";
    private static final String S_P_ENGINEER        = PREFIX + "engineer";
    private static final String S_P_MEZZO_SOPRANO   = PREFIX + "mezzo_soprano";
    private static final String S_P_MIX             = PREFIX + "mix";
    private static final String S_P_ORCHESTRATOR    = PREFIX + "orchestrator";
    private static final String S_P_PRODUCER        = PREFIX + "producer";
    private static final String S_P_PROGRAMMING     = PREFIX + "programming";
    private static final String S_P_RECORDING       = PREFIX + "recording";

    public static final IRI P_ARRANGER              = FACTORY.createIRI(S_P_ARRANGER);
    public static final IRI P_BACKGROUND_SINGER     = FACTORY.createIRI(S_P_BACKGROUND_SINGER);
    public static final IRI P_BALANCE               = FACTORY.createIRI(S_P_BALANCE);
    public static final IRI P_BARITONE              = FACTORY.createIRI(S_P_BARITONE);
    public static final IRI P_BASS                  = FACTORY.createIRI(S_P_BASS);
    public static final IRI P_EDITOR                = FACTORY.createIRI(S_P_EDITOR);
    public static final IRI P_ENGINEER              = FACTORY.createIRI(S_P_ENGINEER);
    public static final IRI P_MEZZO_SOPRANO         = FACTORY.createIRI(S_P_MEZZO_SOPRANO);
    public static final IRI P_MIX                   = FACTORY.createIRI(S_P_MIX);
    public static final IRI P_ORCHESTRATOR          = FACTORY.createIRI(S_P_ORCHESTRATOR);
    public static final IRI P_PRODUCER              = FACTORY.createIRI(S_P_PRODUCER);
    public static final IRI P_PROGRAMMING           = FACTORY.createIRI(S_P_PROGRAMMING);
    public static final IRI P_RECORDING             = FACTORY.createIRI(S_P_RECORDING);
  }
