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
public final class BM // FIXME: rename to BMMO
  {
    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

    public static final String PREFIX                   = "http://bluemarine.tidalwave.it/2015/04/mo/";

    public static final String S_LATEST_INDEXING_TIME   = PREFIX + "latestIndexingTime";
    public static final String S_DISK_NUMBER            = PREFIX + "diskNumber";
    public static final String S_DISK_COUNT             = PREFIX + "diskCount";
    public static final String S_PATH                   = PREFIX + "path";
    public static final String S_FILE_SIZE              = PREFIX + "fileSize";
    public static final String S_ITUNES_CDDB1           = PREFIX + "iTunesCddb1";
    public static final String S_P_IMPORTED_FROM        = PREFIX + "importedFrom";

//    public static final String S_FULL_CREDITS           = PREFIX + "fullCredits";

//    public static final IRI FULL_CREDITS                = factory.createIRI(S_FULL_CREDITS);

    /** The file timestamp the latest time it was indexed. */
    public static final IRI LATEST_INDEXING_TIME        = FACTORY.createIRI(S_LATEST_INDEXING_TIME);

    public static final IRI DISK_NUMBER                 = FACTORY.createIRI(S_DISK_NUMBER);

    public static final IRI DISK_COUNT                  = FACTORY.createIRI(S_DISK_COUNT);

    public static final IRI PATH                        = FACTORY.createIRI(S_PATH);

    public static final IRI FILE_SIZE                   = FACTORY.createIRI(S_FILE_SIZE);

    public static final IRI ITUNES_CDDB1                = FACTORY.createIRI(S_ITUNES_CDDB1);

    /** Predicate that associates any subject to the data source that created it. */
    public static final IRI P_IMPORTED_FROM             = FACTORY.createIRI(S_P_IMPORTED_FROM);

    /** Object of the P_SOURCE predicate that says that the subject was imported from MusicBrainz. */
    public static final IRI O_EMBEDDED                  = FACTORY.createIRI("http://bluemarine.tidalwave.it/source#embedded");

    /** Object of the P_SOURCE predicate that says that the subject was imported from MusicBrainz. */
    public static final IRI O_MUSICBRAINZ               = FACTORY.createIRI("http://musicbrainz.org");

//    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
//    public static final IRI LATEST_MB_METADATA          = factory.createIRI(PREFIX + "latestMusicBrainzMetadata");
//
//    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
//    public static final IRI FAILED_MB_METADATA          = factory.createIRI(PREFIX + "failedMusicBrainzMetadata");

    private static final String S_P_ARRANGER                        = PREFIX + "arranger";
    private static final String S_P_BACKGROUND_SINGER               = PREFIX + "background_singer";
    private static final String S_P_BALANCE                         = PREFIX + "balance";
    private static final String S_P_BASS                            = PREFIX + "bass";
    private static final String S_P_BARITONE                        = PREFIX + "baritone";
    private static final String S_P_CHOIR                           = PREFIX + "choir";
    private static final String S_P_CHORUS_MASTER                   = PREFIX + "chorus_master";
    private static final String S_P_EDITOR                          = PREFIX + "editor";
    private static final String S_P_LEAD_SINGER                     = PREFIX + "lead_singer";
    private static final String S_P_MASTERING                       = PREFIX + "mastering";
    private static final String S_P_MEZZO_SOPRANO                   = PREFIX + "mezzo_soprano";
    private static final String S_P_MIX                             = PREFIX + "mix";
    private static final String S_P_ORCHESTRA                       = PREFIX + "orchestra";
    private static final String S_P_ORCHESTRATOR                    = PREFIX + "orchestrator";
    private static final String S_P_PROGRAMMING                     = PREFIX + "programming";
    private static final String S_P_RECORDING                       = PREFIX + "recording";
    private static final String S_P_SOPRANO                         = PREFIX + "soprano";

    private static final String S_P_PERFORMER_ACCORDION             = PREFIX + "performer_accordion";
    private static final String S_P_PERFORMER_ACOUSTIC_BASS_GUITAR  = PREFIX + "performer_acoustic_bass_guitar";
    private static final String S_P_PERFORMER_ACOUSTIC_GUITAR       = PREFIX + "performer_acoustic_guitar";
    private static final String S_P_PERFORMER_AGOGO                 = PREFIX + "performer_agogo";
    private static final String S_P_PERFORMER_ALTO_SAX              = PREFIX + "performer_alto_saxophone";
    private static final String S_P_PERFORMER_BANJO                 = PREFIX + "performer_banjo";
    private static final String S_P_PERFORMER_BARITONE              = PREFIX + "performer_baritone";
    private static final String S_P_PERFORMER_BARITONE_GUITAR       = PREFIX + "performer_baritone_guitar";
    private static final String S_P_PERFORMER_BASS                  = PREFIX + "performer_bass";
    private static final String S_P_PERFORMER_BASS_CLARINET         = PREFIX + "performer_bass_clarinet";
    private static final String S_P_PERFORMER_BASS_DRUM             = PREFIX + "performer_bass_drum";
    private static final String S_P_PERFORMER_BASS_GUITAR           = PREFIX + "performer_bass_guitar";
    private static final String S_P_PERFORMER_BASS_TROMBONE         = PREFIX + "performer_bass_trombone";
    private static final String S_P_PERFORMER_BASSOON               = PREFIX + "performer_bassoon";
    private static final String S_P_PERFORMER_BELLS                 = PREFIX + "performer_bells";
    private static final String S_P_PERFORMER_BERIMBAU              = PREFIX + "performer_berimbau";
    private static final String S_P_PERFORMER_BRASS                 = PREFIX + "performer_brass";
    private static final String S_P_PERFORMER_BRUSHES               = PREFIX + "performer_brushes";
    private static final String S_P_PERFORMER_CELLO                 = PREFIX + "performer_cello";
    private static final String S_P_PERFORMER_CLARINET              = PREFIX + "performer_clarinet";
    private static final String S_P_PERFORMER_CONGAS                = PREFIX + "performer_congas";
    private static final String S_P_PERFORMER_CORNET                = PREFIX + "performer_cornet";
    private static final String S_P_PERFORMER_CYMBALS               = PREFIX + "performer_cymbals";
    private static final String S_P_PERFORMER_CLASSICAL_GUITAR      = PREFIX + "performer_classical_guitar";
    private static final String S_P_PERFORMER_DOUBLE_BASS           = PREFIX + "performer_double_bass";
    private static final String S_P_PERFORMER_DRUMS                 = PREFIX + "performer_drums";
    private static final String S_P_PERFORMER_ELECTRIC_GUITAR       = PREFIX + "performer_electric_guitar";
    private static final String S_P_PERFORMER_ELECTRIC_BASS_GUITAR  = PREFIX + "performer_electric_bass_guitar";
    private static final String S_P_PERFORMER_ELECTRIC_PIANO        = PREFIX + "performer_electric_piano";
    private static final String S_P_PERFORMER_ELECTRIC_SITAR        = PREFIX + "performer_electric_sitar";
    private static final String S_P_PERFORMER_ELECTRONIC_DRUM_SET   = PREFIX + "performer_electronic_drum_set";
    private static final String S_P_PERFORMER_ENGLISH_HORN          = PREFIX + "performer_english_horn";
    private static final String S_P_PERFORMER_FLUGELHORN            = PREFIX + "performer_flugelhorn";
    private static final String S_P_PERFORMER_FLUTE                 = PREFIX + "performer_flute";
    private static final String S_P_PERFORMER_FRAME_DRUM            = PREFIX + "performer_frame_drum";
    private static final String S_P_PERFORMER_FRENCH_HORN           = PREFIX + "performer_french_horn";
    private static final String S_P_PERFORMER_GLOCKENSPIEL          = PREFIX + "performer_glockenspiel";
    private static final String S_P_PERFORMER_GRAND_PIANO           = PREFIX + "performer_grand_piano";
    private static final String S_P_PERFORMER_GUEST                 = PREFIX + "performer_guest";
    private static final String S_P_PERFORMER_GUITAR                = PREFIX + "performer_guitar";
    private static final String S_P_PERFORMER_GUITAR_SYNTHESIZER    = PREFIX + "performer_guitar_synthesizer";
    private static final String S_P_PERFORMER_GUITARS               = PREFIX + "performer_guitars";
    private static final String S_P_PERFORMER_HAMMOND_ORGAN         = PREFIX + "performer_hammond_organ";
    private static final String S_P_PERFORMER_HANDCLAPS             = PREFIX + "performer_handclaps";
    private static final String S_P_PERFORMER_HARMONICA             = PREFIX + "performer_harmonica";
    private static final String S_P_PERFORMER_HARP                  = PREFIX + "performer_harp";
    private static final String S_P_PERFORMER_HARPSICHORD           = PREFIX + "performer_harpsichord";
    private static final String S_P_PERFORMER_HIHAT                 = PREFIX + "performer_hihat";
    private static final String S_P_PERFORMER_HORN                  = PREFIX + "performer_horn";
    private static final String S_P_PERFORMER_KEYBOARD              = PREFIX + "performer_keyboard";
    private static final String S_P_PERFORMER_KOTO                  = PREFIX + "performer_koto";
    private static final String S_P_PERFORMER_LUTE                  = PREFIX + "performer_lute";
    private static final String S_P_PERFORMER_MARACAS               = PREFIX + "performer_maracas";
    private static final String S_P_PERFORMER_MARIMBA               = PREFIX + "performer_marimba";
    private static final String S_P_PERFORMER_MELLOPHONE            = PREFIX + "performer_mellophone";
    private static final String S_P_PERFORMER_MELODICA              = PREFIX + "performer_melodica";
    private static final String S_P_PERFORMER_OBOE                  = PREFIX + "performer_oboe";
    private static final String S_P_PERFORMER_ORGAN                 = PREFIX + "performer_organ";
    private static final String S_P_PERFORMER_PERCUSSION            = PREFIX + "performer_percussion";
    private static final String S_P_PERFORMER_PIANO                 = PREFIX + "performer_piano";
    private static final String S_P_PERFORMER_PICCOLO_TRUMPET       = PREFIX + "performer_piccolo_trumpet";
    private static final String S_P_PERFORMER_PIPE_ORGAN            = PREFIX + "performer_pipe_organ";
    private static final String S_P_PERFORMER_PSALTERY              = PREFIX + "performer_psaltery";
    private static final String S_P_PERFORMER_RECORDER              = PREFIX + "performer_recorder";
    private static final String S_P_PERFORMER_RHODES_PIANO          = PREFIX + "performer_rhodes_piano";
    private static final String S_P_PERFORMER_SANTUR                = PREFIX + "performer_santur";
    private static final String S_P_PERFORMER_SAXOPHONE             = PREFIX + "performer_saxophone";
    private static final String S_P_PERFORMER_SHAKERS               = PREFIX + "performer_shakers";
    private static final String S_P_PERFORMER_SITAR                 = PREFIX + "performer_sitar";
    private static final String S_P_PERFORMER_SLIDE_GUITAR          = PREFIX + "performer_slide_guitar";
    private static final String S_P_PERFORMER_SNARE_DRUM            = PREFIX + "performer_snare_drumr";
    private static final String S_P_PERFORMER_SOLO                  = PREFIX + "performer_solo";
    private static final String S_P_PERFORMER_SOPRANO_SAX           = PREFIX + "performer_soprano_saxophone";
    private static final String S_P_PERFORMER_SPANISH_ACOUSTIC_GUITAR = PREFIX + "performer_spanish_acoustic_guitar";
    private static final String S_P_PERFORMER_SYNCLAVIER            = PREFIX + "performer_synclavier";
    private static final String S_P_PERFORMER_SYNTHESIZER           = PREFIX + "performer_synthesizer";
    private static final String S_P_PERFORMER_TAMBOURINE            = PREFIX + "performer_tambourine";
    private static final String S_P_PERFORMER_TENOR_SAX             = PREFIX + "performer_tenor_sax";
    private static final String S_P_PERFORMER_TIMPANI               = PREFIX + "performer_timpani";
    private static final String S_P_PERFORMER_TIPLE                 = PREFIX + "performer_tiple";
    private static final String S_P_PERFORMER_TROMBONE              = PREFIX + "performer_trombone";
    private static final String S_P_PERFORMER_TRUMPET               = PREFIX + "performer_trumpet";
    private static final String S_P_PERFORMER_TUBA                  = PREFIX + "performer_tuba";
    private static final String S_P_PERFORMER_TUBULAR_BELLS         = PREFIX + "performer_tubular_bells";
    private static final String S_P_PERFORMER_TUNED_PERCUSSION      = PREFIX + "performer_tuned_percussion";
    private static final String S_P_PERFORMER_UKULELE               = PREFIX + "performer_ukulele";
    private static final String S_P_PERFORMER_VIBRAPHONE            = PREFIX + "performer_vibraphone";
    private static final String S_P_PERFORMER_VIOLA                 = PREFIX + "performer_viola";
    private static final String S_P_PERFORMER_VIOLA_DA_GAMBA        = PREFIX + "performer_viola_da_gamba";
    private static final String S_P_PERFORMER_VIOLIN                = PREFIX + "performer_violin";
    private static final String S_P_PERFORMER_WHISTLE               = PREFIX + "performer_whistle";
    private static final String S_P_PERFORMER_XYLOPHONE             = PREFIX + "performer_xylophone";

    public static final IRI P_ARRANGER                          = FACTORY.createIRI(S_P_ARRANGER);
    public static final IRI P_BACKGROUND_SINGER                 = FACTORY.createIRI(S_P_BACKGROUND_SINGER);
    public static final IRI P_BALANCE                           = FACTORY.createIRI(S_P_BALANCE);
    public static final IRI P_BARITONE                          = FACTORY.createIRI(S_P_BARITONE);
    public static final IRI P_BASS                              = FACTORY.createIRI(S_P_BASS);
    public static final IRI P_CHOIR                             = FACTORY.createIRI(S_P_CHOIR);
    public static final IRI P_CHORUS_MASTER                     = FACTORY.createIRI(S_P_CHORUS_MASTER);
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

    public static final IRI P_PERFORMER_ACCORDION               = FACTORY.createIRI(S_P_PERFORMER_ACCORDION);
    public static final IRI P_PERFORMER_ACOUSTIC_BASS_GUITAR    = FACTORY.createIRI(S_P_PERFORMER_ACOUSTIC_BASS_GUITAR);
    public static final IRI P_PERFORMER_ACOUSTIC_GUITAR         = FACTORY.createIRI(S_P_PERFORMER_ACOUSTIC_GUITAR);
    public static final IRI P_PERFORMER_AGOGO                   = FACTORY.createIRI(S_P_PERFORMER_AGOGO);
    public static final IRI P_PERFORMER_ALTO_SAX                = FACTORY.createIRI(S_P_PERFORMER_ALTO_SAX);
    public static final IRI P_PERFORMER_BANJO                   = FACTORY.createIRI(S_P_PERFORMER_BANJO);
    public static final IRI P_PERFORMER_BARITONE                = FACTORY.createIRI(S_P_PERFORMER_BARITONE);
    public static final IRI P_PERFORMER_BARITONE_GUITAR         = FACTORY.createIRI(S_P_PERFORMER_BARITONE_GUITAR);
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
    public static final IRI P_PERFORMER_PERCUSSION              = FACTORY.createIRI(S_P_PERFORMER_PERCUSSION);
    public static final IRI P_PERFORMER_PIANO                   = FACTORY.createIRI(S_P_PERFORMER_PIANO);
    public static final IRI P_PERFORMER_PICCOLO_TRUMPET         = FACTORY.createIRI(S_P_PERFORMER_PICCOLO_TRUMPET);
    public static final IRI P_PERFORMER_PIPE_ORGAN              = FACTORY.createIRI(S_P_PERFORMER_PIPE_ORGAN);
    public static final IRI P_PERFORMER_PSALTERY                = FACTORY.createIRI(S_P_PERFORMER_PSALTERY);
    public static final IRI P_PERFORMER_RECORDER                = FACTORY.createIRI(S_P_PERFORMER_RECORDER);
    public static final IRI P_PERFORMER_RHODES_PIANO            = FACTORY.createIRI(S_P_PERFORMER_RHODES_PIANO);
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
    public static final IRI P_PERFORMER_TAMBOURINE              = FACTORY.createIRI(S_P_PERFORMER_TAMBOURINE);
    public static final IRI P_PERFORMER_TENOR_SAX               = FACTORY.createIRI(S_P_PERFORMER_TENOR_SAX);
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
    public static IRI recordIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:record:" + id.stringValue());
      }

    @Nonnull
    public static IRI artistIriFor (final @Nonnull Id id)
      {
        return FACTORY.createIRI("urn:bluemarine:artist:" + id.stringValue());
      }

    @Nonnull
    public static IRI musicBrainzIriFor (final @Nonnull String resourceType, final @Nonnull Id id)
      {
        return FACTORY.createIRI(String.format("urn:musicbrainz:%s:%s", resourceType, id.stringValue()));
      }
  }
