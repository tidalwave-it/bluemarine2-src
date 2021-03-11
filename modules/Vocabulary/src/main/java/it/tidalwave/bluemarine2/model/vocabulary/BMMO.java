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

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BMMO
  {
    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

    public static final String NS = "http://bluemarine.tidalwave.it/2015/04/mo/";

    public static final String S_P_LATEST_INDEXING_TIME = NS + "latestIndexingTime";
    public static final String S_P_DISK_NUMBER          = NS + "diskNumber";
    public static final String S_P_DISK_COUNT           = NS + "diskCount";
    public static final String S_P_PATH                 = NS + "path";
    public static final String S_P_FILE_SIZE            = NS + "fileSize";
    public static final String S_P_ITUNES_CDDB1         = NS + "iTunesCddb1";
    public static final String S_P_IMPORTED_FROM        = NS + "importedFrom";

    public static final String S_P_ALTERNATE_OF         = NS + "alternateOf";
    public static final String S_P_ALTERNATE_PICK_OF    = NS + "alternatePickOf";

    public static final String S_O_SOURCE_EMBEDDED      = "embedded";
    public static final String S_O_SOURCE_MUSICBRAINZ   = "musicbrainz";

    /** The file timestamp the latest time it was indexed. */
    public static final IRI P_LATEST_INDEXING_TIME      = FACTORY.createIRI(S_P_LATEST_INDEXING_TIME);

    public static final IRI P_DISK_NUMBER               = FACTORY.createIRI(S_P_DISK_NUMBER);

    public static final IRI P_DISK_COUNT                = FACTORY.createIRI(S_P_DISK_COUNT);

    public static final IRI P_PATH                      = FACTORY.createIRI(S_P_PATH);

    public static final IRI P_FILE_SIZE                 = FACTORY.createIRI(S_P_FILE_SIZE);

    public static final IRI P_ITUNES_CDDB1              = FACTORY.createIRI(S_P_ITUNES_CDDB1);

    /** Predicate that associates any subject to the data source that created it. */
    public static final IRI P_IMPORTED_FROM             = FACTORY.createIRI(S_P_IMPORTED_FROM);

    /** Predicate that marks an entity from a different source - e.g. an entity imported from MusicBrainz that is
        marked as alternate of an entity imported from embedded metadata. */
    public static final IRI P_ALTERNATE_OF              = FACTORY.createIRI(S_P_ALTERNATE_OF);

    /** Predicate that marks an entity as an alternative pick that has been suppressed. For instance, a record might
        appear as a single release or part of a multi-record release; one of them is marked as alternate pick. */
    public static final IRI P_ALTERNATE_PICK_OF         = FACTORY.createIRI(S_P_ALTERNATE_PICK_OF);

    /** Object of the {@link #P_IMPORTED_FROM} predicate that says that the subject was imported from embedded metadata. */
    public static final Value O_SOURCE_EMBEDDED         = FACTORY.createLiteral(S_O_SOURCE_EMBEDDED);

    /** Object of the {@link #P_IMPORTED_FROM} predicate that says that the subject was imported from MusicBrainz. */
    public static final Value O_SOURCE_MUSICBRAINZ      = FACTORY.createLiteral(S_O_SOURCE_MUSICBRAINZ);

    public static final Id ID_SOURCE_EMBEDDED           = new Id(S_O_SOURCE_EMBEDDED);
    public static final Id ID_SOURCE_MUSICBRAINZ        = new Id(S_O_SOURCE_MUSICBRAINZ);

    private static final String S_P_ALTO                              = NS + "alto";
    private static final String S_P_ARRANGER                          = NS + "arranger";
    private static final String S_P_BACKGROUND_SINGER                 = NS + "background_singer";
    private static final String S_P_BALANCE                           = NS + "balance";
    private static final String S_P_BARITONE                          = NS + "baritone";
    private static final String S_P_BASS                              = NS + "bass";
    private static final String S_P_BASS_BARITONE                     = NS + "bass_baritone";
    private static final String S_P_CHOIR                             = NS + "choir";
    private static final String S_P_CHORUS_MASTER                     = NS + "chorus_master";
    private static final String S_P_CONTRALTO                         = NS + "contralto";
    private static final String S_P_EDITOR                            = NS + "editor";
    private static final String S_P_LEAD_SINGER                       = NS + "lead_singer";
    private static final String S_P_MASTERING                         = NS + "mastering";
    private static final String S_P_MEZZO_SOPRANO                     = NS + "mezzo_soprano";
    private static final String S_P_MIX                               = NS + "mix";
    private static final String S_P_ORCHESTRA                         = NS + "orchestra";
    private static final String S_P_ORCHESTRATOR                      = NS + "orchestrator";
    private static final String S_P_PROGRAMMING                       = NS + "programming";
    private static final String S_P_RECORDING                         = NS + "recording";
    private static final String S_P_SOPRANO                           = NS + "soprano";
    private static final String S_P_TENOR                             = NS + "tenor";

    private static final String S_P_PERFORMER_ACCORDION               = NS + "performer_accordion";
    private static final String S_P_PERFORMER_ACOUSTIC_BASS_GUITAR    = NS + "performer_acoustic_bass_guitar";
    private static final String S_P_PERFORMER_ACOUSTIC_GUITAR         = NS + "performer_acoustic_guitar";
    private static final String S_P_PERFORMER_AGOGO                   = NS + "performer_agogo";
    private static final String S_P_PERFORMER_ALTO_SAX                = NS + "performer_alto_saxophone";
    private static final String S_P_PERFORMER_BANJO                   = NS + "performer_banjo";
    private static final String S_P_PERFORMER_BARITONE                = NS + "performer_baritone";
    private static final String S_P_PERFORMER_BARITONE_GUITAR         = NS + "performer_baritone_guitar";
    private static final String S_P_PERFORMER_BARITONE_SAX            = NS + "performer_baritone_sax";
    private static final String S_P_PERFORMER_BASS                    = NS + "performer_bass";
    private static final String S_P_PERFORMER_BASS_CLARINET           = NS + "performer_bass_clarinet";
    private static final String S_P_PERFORMER_BASS_DRUM               = NS + "performer_bass_drum";
    private static final String S_P_PERFORMER_BASS_GUITAR             = NS + "performer_bass_guitar";
    private static final String S_P_PERFORMER_BASS_TROMBONE           = NS + "performer_bass_trombone";
    private static final String S_P_PERFORMER_BASSOON                 = NS + "performer_bassoon";
    private static final String S_P_PERFORMER_BELLS                   = NS + "performer_bells";
    private static final String S_P_PERFORMER_BERIMBAU                = NS + "performer_berimbau";
    private static final String S_P_PERFORMER_BRASS                   = NS + "performer_brass";
    private static final String S_P_PERFORMER_BRUSHES                 = NS + "performer_brushes";
    private static final String S_P_PERFORMER_CELLO                   = NS + "performer_cello";
    private static final String S_P_PERFORMER_CLARINET                = NS + "performer_clarinet";
    private static final String S_P_PERFORMER_CONGAS                  = NS + "performer_congas";
    private static final String S_P_PERFORMER_CORNET                  = NS + "performer_cornet";
    private static final String S_P_PERFORMER_CYMBALS                 = NS + "performer_cymbals";
    private static final String S_P_PERFORMER_CLASSICAL_GUITAR        = NS + "performer_classical_guitar";
    private static final String S_P_PERFORMER_DOUBLE_BASS             = NS + "performer_double_bass";
    private static final String S_P_PERFORMER_DRUM_MACHINE            = NS + "performer_drum_machine";
    private static final String S_P_PERFORMER_DRUMS                   = NS + "performer_drums";
    private static final String S_P_PERFORMER_ELECTRIC_GUITAR         = NS + "performer_electric_guitar";
    private static final String S_P_PERFORMER_ELECTRIC_BASS_GUITAR    = NS + "performer_electric_bass_guitar";
    private static final String S_P_PERFORMER_ELECTRIC_PIANO          = NS + "performer_electric_piano";
    private static final String S_P_PERFORMER_ELECTRIC_SITAR          = NS + "performer_electric_sitar";
    private static final String S_P_PERFORMER_ELECTRONIC_DRUM_SET     = NS + "performer_electronic_drum_set";
    private static final String S_P_PERFORMER_ENGLISH_HORN            = NS + "performer_english_horn";
    private static final String S_P_PERFORMER_FLUGELHORN              = NS + "performer_flugelhorn";
    private static final String S_P_PERFORMER_FLUTE                   = NS + "performer_flute";
    private static final String S_P_PERFORMER_FRAME_DRUM              = NS + "performer_frame_drum";
    private static final String S_P_PERFORMER_FRENCH_HORN             = NS + "performer_french_horn";
    private static final String S_P_PERFORMER_GLOCKENSPIEL            = NS + "performer_glockenspiel";
    private static final String S_P_PERFORMER_GRAND_PIANO             = NS + "performer_grand_piano";
    private static final String S_P_PERFORMER_GUEST                   = NS + "performer_guest";
    private static final String S_P_PERFORMER_GUITAR                  = NS + "performer_guitar";
    private static final String S_P_PERFORMER_GUITAR_SYNTHESIZER      = NS + "performer_guitar_synthesizer";
    private static final String S_P_PERFORMER_GUITARS                 = NS + "performer_guitars";
    private static final String S_P_PERFORMER_HAMMOND_ORGAN           = NS + "performer_hammond_organ";
    private static final String S_P_PERFORMER_HANDCLAPS               = NS + "performer_handclaps";
    private static final String S_P_PERFORMER_HARMONICA               = NS + "performer_harmonica";
    private static final String S_P_PERFORMER_HARP                    = NS + "performer_harp";
    private static final String S_P_PERFORMER_HARPSICHORD             = NS + "performer_harpsichord";
    private static final String S_P_PERFORMER_HIHAT                   = NS + "performer_hihat";
    private static final String S_P_PERFORMER_HORN                    = NS + "performer_horn";
    private static final String S_P_PERFORMER_KEYBOARD                = NS + "performer_keyboard";
    private static final String S_P_PERFORMER_KOTO                    = NS + "performer_koto";
    private static final String S_P_PERFORMER_LUTE                    = NS + "performer_lute";
    private static final String S_P_PERFORMER_MARACAS                 = NS + "performer_maracas";
    private static final String S_P_PERFORMER_MARIMBA                 = NS + "performer_marimba";
    private static final String S_P_PERFORMER_MELLOPHONE              = NS + "performer_mellophone";
    private static final String S_P_PERFORMER_MELODICA                = NS + "performer_melodica";
    private static final String S_P_PERFORMER_OBOE                    = NS + "performer_oboe";
    private static final String S_P_PERFORMER_ORGAN                   = NS + "performer_organ";
    private static final String S_P_PERFORMER_OTHER_INSTRUMENTS       = NS + "performer_other_instruments";
    private static final String S_P_PERFORMER_PERCUSSION              = NS + "performer_percussion";
    private static final String S_P_PERFORMER_PIANO                   = NS + "performer_piano";
    private static final String S_P_PERFORMER_PICCOLO_TRUMPET         = NS + "performer_piccolo_trumpet";
    private static final String S_P_PERFORMER_PIPE_ORGAN              = NS + "performer_pipe_organ";
    private static final String S_P_PERFORMER_PSALTERY                = NS + "performer_psaltery";
    private static final String S_P_PERFORMER_RECORDER                = NS + "performer_recorder";
    private static final String S_P_PERFORMER_REEDS                   = NS + "performer_reeds";
    private static final String S_P_PERFORMER_RHODES_PIANO            = NS + "performer_rhodes_piano";
    private static final String S_P_PERFORMER_SANTUR                  = NS + "performer_santur";
    private static final String S_P_PERFORMER_SAXOPHONE               = NS + "performer_saxophone";
    private static final String S_P_PERFORMER_SHAKERS                 = NS + "performer_shakers";
    private static final String S_P_PERFORMER_SITAR                   = NS + "performer_sitar";
    private static final String S_P_PERFORMER_SLIDE_GUITAR            = NS + "performer_slide_guitar";
    private static final String S_P_PERFORMER_SNARE_DRUM              = NS + "performer_snare_drumr";
    private static final String S_P_PERFORMER_SOLO                    = NS + "performer_solo";
    private static final String S_P_PERFORMER_SOPRANO_SAX             = NS + "performer_soprano_saxophone";
    private static final String S_P_PERFORMER_SPANISH_ACOUSTIC_GUITAR = NS + "performer_spanish_acoustic_guitar";
    private static final String S_P_PERFORMER_STEEL_GUITAR            = NS + "performer_steel_guitar";
    private static final String S_P_PERFORMER_SYNCLAVIER              = NS + "performer_synclavier";
    private static final String S_P_PERFORMER_SYNTHESIZER             = NS + "performer_synthesizer";
    private static final String S_P_PERFORMER_TAMBOURINE              = NS + "performer_tambourine";
    private static final String S_P_PERFORMER_TENOR_SAX               = NS + "performer_tenor_sax";
    private static final String S_P_PERFORMER_TIMBALES                = NS + "performer_timbales";
    private static final String S_P_PERFORMER_TIMPANI                 = NS + "performer_timpani";
    private static final String S_P_PERFORMER_TIPLE                   = NS + "performer_tiple";
    private static final String S_P_PERFORMER_TROMBONE                = NS + "performer_trombone";
    private static final String S_P_PERFORMER_TRUMPET                 = NS + "performer_trumpet";
    private static final String S_P_PERFORMER_TUBA                    = NS + "performer_tuba";
    private static final String S_P_PERFORMER_TUBULAR_BELLS           = NS + "performer_tubular_bells";
    private static final String S_P_PERFORMER_TUNED_PERCUSSION        = NS + "performer_tuned_percussion";
    private static final String S_P_PERFORMER_UKULELE                 = NS + "performer_ukulele";
    private static final String S_P_PERFORMER_VIBRAPHONE              = NS + "performer_vibraphone";
    private static final String S_P_PERFORMER_VIOLA                   = NS + "performer_viola";
    private static final String S_P_PERFORMER_VIOLA_DA_GAMBA          = NS + "performer_viola_da_gamba";
    private static final String S_P_PERFORMER_VIOLIN                  = NS + "performer_violin";
    private static final String S_P_PERFORMER_WHISTLE                 = NS + "performer_whistle";
    private static final String S_P_PERFORMER_XYLOPHONE               = NS + "performer_xylophone";

    public static final IRI P_ARRANGER                          = FACTORY.createIRI(S_P_ARRANGER);
    public static final IRI P_ALTO                              = FACTORY.createIRI(S_P_ALTO);
    public static final IRI P_BACKGROUND_SINGER                 = FACTORY.createIRI(S_P_BACKGROUND_SINGER);
    public static final IRI P_BALANCE                           = FACTORY.createIRI(S_P_BALANCE);
    public static final IRI P_BARITONE                          = FACTORY.createIRI(S_P_BARITONE);
    public static final IRI P_BASS                              = FACTORY.createIRI(S_P_BASS);
    public static final IRI P_BASS_BARITONE                     = FACTORY.createIRI(S_P_BASS_BARITONE);
    public static final IRI P_CHOIR                             = FACTORY.createIRI(S_P_CHOIR);
    public static final IRI P_CHORUS_MASTER                     = FACTORY.createIRI(S_P_CHORUS_MASTER);
    public static final IRI P_CONTRALTO                         = FACTORY.createIRI(S_P_CONTRALTO);
    public static final IRI P_EDITOR                            = FACTORY.createIRI(S_P_EDITOR);
    public static final IRI P_LEAD_SINGER                       = FACTORY.createIRI(S_P_LEAD_SINGER);
    public static final IRI P_MASTERING                         = FACTORY.createIRI(S_P_MASTERING);
    public static final IRI P_MEZZO_SOPRANO                     = FACTORY.createIRI(S_P_MEZZO_SOPRANO);
    public static final IRI P_MIX                               = FACTORY.createIRI(S_P_MIX);
    public static final IRI P_ORCHESTRA                         = FACTORY.createIRI(S_P_ORCHESTRA);
    public static final IRI P_ORCHESTRATOR                      = FACTORY.createIRI(S_P_ORCHESTRATOR);
    public static final IRI P_PROGRAMMING                       = FACTORY.createIRI(S_P_PROGRAMMING);
    public static final IRI P_RECORDING                         = FACTORY.createIRI(S_P_RECORDING);
    public static final IRI P_SOPRANO                           = FACTORY.createIRI(S_P_SOPRANO);
    public static final IRI P_TENOR                             = FACTORY.createIRI(S_P_TENOR);

    public static final IRI P_PERFORMER_ACCORDION               = FACTORY.createIRI(S_P_PERFORMER_ACCORDION);
    public static final IRI P_PERFORMER_ACOUSTIC_BASS_GUITAR    = FACTORY.createIRI(S_P_PERFORMER_ACOUSTIC_BASS_GUITAR);
    public static final IRI P_PERFORMER_ACOUSTIC_GUITAR         = FACTORY.createIRI(S_P_PERFORMER_ACOUSTIC_GUITAR);
    public static final IRI P_PERFORMER_AGOGO                   = FACTORY.createIRI(S_P_PERFORMER_AGOGO);
    public static final IRI P_PERFORMER_ALTO_SAX                = FACTORY.createIRI(S_P_PERFORMER_ALTO_SAX);
    public static final IRI P_PERFORMER_BANJO                   = FACTORY.createIRI(S_P_PERFORMER_BANJO);
    public static final IRI P_PERFORMER_BARITONE                = FACTORY.createIRI(S_P_PERFORMER_BARITONE);
    public static final IRI P_PERFORMER_BARITONE_GUITAR         = FACTORY.createIRI(S_P_PERFORMER_BARITONE_GUITAR);
    public static final IRI P_PERFORMER_BARITONE_SAX            = FACTORY.createIRI(S_P_PERFORMER_BARITONE_SAX);
    public static final IRI P_PERFORMER_BASS                    = FACTORY.createIRI(S_P_PERFORMER_BASS);
    public static final IRI P_PERFORMER_BASS_CLARINET           = FACTORY.createIRI(S_P_PERFORMER_BASS_CLARINET);
    public static final IRI P_PERFORMER_BASS_DRUM               = FACTORY.createIRI(S_P_PERFORMER_BASS_DRUM);
    public static final IRI P_PERFORMER_BASS_GUITAR             = FACTORY.createIRI(S_P_PERFORMER_BASS_GUITAR);
    public static final IRI P_PERFORMER_BASS_TROMBONE           = FACTORY.createIRI(S_P_PERFORMER_BASS_TROMBONE);
    public static final IRI P_PERFORMER_BASSOON                 = FACTORY.createIRI(S_P_PERFORMER_BASSOON);
    public static final IRI P_PERFORMER_BELLS                   = FACTORY.createIRI(S_P_PERFORMER_BELLS);
    public static final IRI P_PERFORMER_BERIMBAU                = FACTORY.createIRI(S_P_PERFORMER_BERIMBAU);
    public static final IRI P_PERFORMER_BRASS                   = FACTORY.createIRI(S_P_PERFORMER_BRASS);
    public static final IRI P_PERFORMER_BRUSHES                 = FACTORY.createIRI(S_P_PERFORMER_BRUSHES);
    public static final IRI P_PERFORMER_CELLO                   = FACTORY.createIRI(S_P_PERFORMER_CELLO);
    public static final IRI P_PERFORMER_CLARINET                = FACTORY.createIRI(S_P_PERFORMER_CLARINET);
    public static final IRI P_PERFORMER_CONGAS                  = FACTORY.createIRI(S_P_PERFORMER_CONGAS);
    public static final IRI P_PERFORMER_CORNET                  = FACTORY.createIRI(S_P_PERFORMER_CORNET);
    public static final IRI P_PERFORMER_CYMBALS                 = FACTORY.createIRI(S_P_PERFORMER_CYMBALS);
    public static final IRI P_PERFORMER_CLASSICAL_GUITAR        = FACTORY.createIRI(S_P_PERFORMER_CLASSICAL_GUITAR);
    public static final IRI P_PERFORMER_DOUBLE_BASS             = FACTORY.createIRI(S_P_PERFORMER_DOUBLE_BASS);
    public static final IRI P_PERFORMER_DRUM_MACHINE            = FACTORY.createIRI(S_P_PERFORMER_DRUM_MACHINE);
    public static final IRI P_PERFORMER_DRUMS                   = FACTORY.createIRI(S_P_PERFORMER_DRUMS);
    public static final IRI P_PERFORMER_ELECTRIC_BASS_GUITAR    = FACTORY.createIRI(S_P_PERFORMER_ELECTRIC_BASS_GUITAR);
    public static final IRI P_PERFORMER_ELECTRIC_GUITAR         = FACTORY.createIRI(S_P_PERFORMER_ELECTRIC_GUITAR);
    public static final IRI P_PERFORMER_ELECTRIC_PIANO          = FACTORY.createIRI(S_P_PERFORMER_ELECTRIC_PIANO);
    public static final IRI P_PERFORMER_ELECTRIC_SITAR          = FACTORY.createIRI(S_P_PERFORMER_ELECTRIC_SITAR);
    public static final IRI P_PERFORMER_ELECTRONIC_DRUM_SET     = FACTORY.createIRI(S_P_PERFORMER_ELECTRONIC_DRUM_SET);
    public static final IRI P_PERFORMER_ENGLISH_HORN            = FACTORY.createIRI(S_P_PERFORMER_ENGLISH_HORN);
    public static final IRI P_PERFORMER_FLUGELHORN              = FACTORY.createIRI(S_P_PERFORMER_FLUGELHORN);
    public static final IRI P_PERFORMER_FLUTE                   = FACTORY.createIRI(S_P_PERFORMER_FLUTE);
    public static final IRI P_PERFORMER_FRAME_DRUM              = FACTORY.createIRI(S_P_PERFORMER_FRAME_DRUM);
    public static final IRI P_PERFORMER_FRENCH_HORN             = FACTORY.createIRI(S_P_PERFORMER_FRENCH_HORN);
    public static final IRI P_PERFORMER_GLOCKENSPIEL            = FACTORY.createIRI(S_P_PERFORMER_GLOCKENSPIEL);
    public static final IRI P_PERFORMER_GRAND_PIANO             = FACTORY.createIRI(S_P_PERFORMER_GRAND_PIANO);
    public static final IRI P_PERFORMER_GUEST                   = FACTORY.createIRI(S_P_PERFORMER_GUEST);
    public static final IRI P_PERFORMER_GUITAR                  = FACTORY.createIRI(S_P_PERFORMER_GUITAR);
    public static final IRI P_PERFORMER_GUITAR_SYNTHESIZER      = FACTORY.createIRI(S_P_PERFORMER_GUITAR_SYNTHESIZER);
    public static final IRI P_PERFORMER_GUITARS                 = FACTORY.createIRI(S_P_PERFORMER_GUITARS);
    public static final IRI P_PERFORMER_HAMMOND_ORGAN           = FACTORY.createIRI(S_P_PERFORMER_HAMMOND_ORGAN);
    public static final IRI P_PERFORMER_HANDCLAPS               = FACTORY.createIRI(S_P_PERFORMER_HANDCLAPS);
    public static final IRI P_PERFORMER_HARMONICA               = FACTORY.createIRI(S_P_PERFORMER_HARMONICA);
    public static final IRI P_PERFORMER_HARP                    = FACTORY.createIRI(S_P_PERFORMER_HARP);
    public static final IRI P_PERFORMER_HARPSICHORD             = FACTORY.createIRI(S_P_PERFORMER_HARPSICHORD);
    public static final IRI P_PERFORMER_HIHAT                   = FACTORY.createIRI(S_P_PERFORMER_HIHAT);
    public static final IRI P_PERFORMER_HORN                    = FACTORY.createIRI(S_P_PERFORMER_HORN);
    public static final IRI P_PERFORMER_KEYBOARD                = FACTORY.createIRI(S_P_PERFORMER_KEYBOARD);
    public static final IRI P_PERFORMER_KOTO                    = FACTORY.createIRI(S_P_PERFORMER_KOTO);
    public static final IRI P_PERFORMER_LUTE                    = FACTORY.createIRI(S_P_PERFORMER_LUTE);
    public static final IRI P_PERFORMER_MARACAS                 = FACTORY.createIRI(S_P_PERFORMER_MARACAS);
    public static final IRI P_PERFORMER_MARIMBA                 = FACTORY.createIRI(S_P_PERFORMER_MARIMBA);
    public static final IRI P_PERFORMER_MELLOPHONE              = FACTORY.createIRI(S_P_PERFORMER_MELLOPHONE);
    public static final IRI P_PERFORMER_MELODICA                = FACTORY.createIRI(S_P_PERFORMER_MELODICA);
    public static final IRI P_PERFORMER_OBOE                    = FACTORY.createIRI(S_P_PERFORMER_OBOE);
    public static final IRI P_PERFORMER_ORGAN                   = FACTORY.createIRI(S_P_PERFORMER_ORGAN);
    public static final IRI P_PERFORMER_OTHER_INSTRUMENTS       = FACTORY.createIRI(S_P_PERFORMER_OTHER_INSTRUMENTS);
    public static final IRI P_PERFORMER_PERCUSSION              = FACTORY.createIRI(S_P_PERFORMER_PERCUSSION);
    public static final IRI P_PERFORMER_PIANO                   = FACTORY.createIRI(S_P_PERFORMER_PIANO);
    public static final IRI P_PERFORMER_PICCOLO_TRUMPET         = FACTORY.createIRI(S_P_PERFORMER_PICCOLO_TRUMPET);
    public static final IRI P_PERFORMER_PIPE_ORGAN              = FACTORY.createIRI(S_P_PERFORMER_PIPE_ORGAN);
    public static final IRI P_PERFORMER_PSALTERY                = FACTORY.createIRI(S_P_PERFORMER_PSALTERY);
    public static final IRI P_PERFORMER_RECORDER                = FACTORY.createIRI(S_P_PERFORMER_RECORDER);
    public static final IRI P_PERFORMER_RHODES_PIANO            = FACTORY.createIRI(S_P_PERFORMER_RHODES_PIANO);
    public static final IRI P_PERFORMER_REEDS                   = FACTORY.createIRI(S_P_PERFORMER_REEDS);
    public static final IRI P_PERFORMER_SANTUR                  = FACTORY.createIRI(S_P_PERFORMER_SANTUR);
    public static final IRI P_PERFORMER_SAXOPHONE               = FACTORY.createIRI(S_P_PERFORMER_SAXOPHONE);
    public static final IRI P_PERFORMER_SHAKERS                 = FACTORY.createIRI(S_P_PERFORMER_SHAKERS);
    public static final IRI P_PERFORMER_SITAR                   = FACTORY.createIRI(S_P_PERFORMER_SITAR);
    public static final IRI P_PERFORMER_SLIDE_GUITAR            = FACTORY.createIRI(S_P_PERFORMER_SLIDE_GUITAR);
    public static final IRI P_PERFORMER_SNARE_DRUM              = FACTORY.createIRI(S_P_PERFORMER_SNARE_DRUM);
    public static final IRI P_PERFORMER_SOLO                    = FACTORY.createIRI(S_P_PERFORMER_SOLO);
    public static final IRI P_PERFORMER_SOPRANO_SAX             = FACTORY.createIRI(S_P_PERFORMER_SOPRANO_SAX);
    public static final IRI P_PERFORMER_SYNCLAVIER              = FACTORY.createIRI(S_P_PERFORMER_SYNCLAVIER);
    public static final IRI P_PERFORMER_SYNTHESIZER             = FACTORY.createIRI(S_P_PERFORMER_SYNTHESIZER);
    public static final IRI P_PERFORMER_SPANISH_ACOUSTIC_GUITAR = FACTORY.createIRI(S_P_PERFORMER_SPANISH_ACOUSTIC_GUITAR);
    public static final IRI P_PERFORMER_STEEL_GUITAR            = FACTORY.createIRI(S_P_PERFORMER_STEEL_GUITAR);
    public static final IRI P_PERFORMER_TAMBOURINE              = FACTORY.createIRI(S_P_PERFORMER_TAMBOURINE);
    public static final IRI P_PERFORMER_TENOR_SAX               = FACTORY.createIRI(S_P_PERFORMER_TENOR_SAX);
    public static final IRI P_PERFORMER_TIMBALES                = FACTORY.createIRI(S_P_PERFORMER_TIMBALES);
    public static final IRI P_PERFORMER_TIMPANI                 = FACTORY.createIRI(S_P_PERFORMER_TIMPANI);
    public static final IRI P_PERFORMER_TIPLE                   = FACTORY.createIRI(S_P_PERFORMER_TIPLE);
    public static final IRI P_PERFORMER_TROMBONE                = FACTORY.createIRI(S_P_PERFORMER_TROMBONE);
    public static final IRI P_PERFORMER_TRUMPET                 = FACTORY.createIRI(S_P_PERFORMER_TRUMPET);
    public static final IRI P_PERFORMER_TUBA                    = FACTORY.createIRI(S_P_PERFORMER_TUBA);
    public static final IRI P_PERFORMER_TUBULAR_BELLS           = FACTORY.createIRI(S_P_PERFORMER_TUBULAR_BELLS);
    public static final IRI P_PERFORMER_TUNED_PERCUSSION        = FACTORY.createIRI(S_P_PERFORMER_TUNED_PERCUSSION);
    public static final IRI P_PERFORMER_UKULELE                 = FACTORY.createIRI(S_P_PERFORMER_UKULELE);
    public static final IRI P_PERFORMER_VIBRAPHONE              = FACTORY.createIRI(S_P_PERFORMER_VIBRAPHONE);
    public static final IRI P_PERFORMER_VIOLA                   = FACTORY.createIRI(S_P_PERFORMER_VIOLA);
    public static final IRI P_PERFORMER_VIOLA_DA_GAMBA          = FACTORY.createIRI(S_P_PERFORMER_VIOLA_DA_GAMBA);
    public static final IRI P_PERFORMER_VIOLIN                  = FACTORY.createIRI(S_P_PERFORMER_VIOLIN);
    public static final IRI P_PERFORMER_WHISTLE                 = FACTORY.createIRI(S_P_PERFORMER_WHISTLE);
    public static final IRI P_PERFORMER_XYLOPHONE               = FACTORY.createIRI(S_P_PERFORMER_XYLOPHONE);

    @Nonnull
    public static IRI audioFileIriFor (final @Nonnull String sha1)
      {
        return FACTORY.createIRI("urn:bluemarine:audiofile:" + sha1);
      }

    @Nonnull
    public static IRI signalIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:signal:" + id.stringValue());
      }

    @Nonnull
    public static IRI trackIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:track:" + id.stringValue());
      }

    @Nonnull
    public static IRI performanceIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:performance:" + id.stringValue());
      }

    @Nonnull
    public static IRI recordIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:record:" + id.stringValue());
      }

    @Nonnull
    public static IRI artistIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:artist:" + id.stringValue());
      }
  }
