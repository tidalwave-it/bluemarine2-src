/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.impl.audio.musicbrainz;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.musicbrainz.ns.mmd_2.Artist;
import org.musicbrainz.ns.mmd_2.DefTrackData;
import org.musicbrainz.ns.mmd_2.Disc;
import org.musicbrainz.ns.mmd_2.Medium;
import org.musicbrainz.ns.mmd_2.MediumList;
import org.musicbrainz.ns.mmd_2.Recording;
import org.musicbrainz.ns.mmd_2.Relation;
import org.musicbrainz.ns.mmd_2.Relation.AttributeList.Attribute;
import org.musicbrainz.ns.mmd_2.RelationList;
import org.musicbrainz.ns.mmd_2.Release;
import org.musicbrainz.ns.mmd_2.ReleaseGroup;
import org.musicbrainz.ns.mmd_2.ReleaseGroupList;
import org.musicbrainz.ns.mmd_2.ReleaseList;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.util.ModelBuilder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.Cddb;
import it.tidalwave.bluemarine2.model.vocabulary.*;
import it.tidalwave.bluemarine2.rest.RestResponse;
import it.tidalwave.bluemarine2.metadata.cddb.CddbAlbum;
import it.tidalwave.bluemarine2.metadata.cddb.CddbMetadataProvider;
import it.tidalwave.bluemarine2.metadata.musicbrainz.MusicBrainzMetadataProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.metadata.musicbrainz.MusicBrainzMetadataProvider.*;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
@RequiredArgsConstructor
public class MusicBrainzAudioMedatataImporter
  {
    private static final QName QNAME_SCORE = new QName("http://musicbrainz.org/ns/ext#-2.0", "score");

    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

    private static final String[] TOC_INCLUDES = { "aliases", "artist-credits", "labels", "recordings" };

    private static final String[] RELEASE_INCLUDES = { "aliases", "artist-credits", "discids", "labels", "recordings" };

    private static final String[] RECORDING_INCLUDES = { "aliases", "artist-credits", "artist-rels" };

    private static final Map<String, IRI> PERFORMER_MAP = new HashMap<>();

    private static final IRI SOURCE_MUSICBRAINZ = FACTORY.createIRI(BMMO.NS, "source#musicbrainz");

    @Nonnull
    private final CddbMetadataProvider cddbMetadataProvider;

    @Nonnull
    private final MusicBrainzMetadataProvider mbMetadataProvider;

    @Getter @Setter
    private int trackOffsetsMatchThreshold = 2500;

    @Getter @Setter
    private int releaseGroupScoreThreshold = 50;

    /** If {@code true}, in case of multiple collections to pick from, those that are not the least one are marked as
        alternative. */
    @Getter @Setter
    private boolean discourageCollections = true;

    private final Set<String> processedTocs = new HashSet<>();

    enum Validation
      {
        TRACK_OFFSETS_MATCH_REQUIRED,
        TRACK_OFFSETS_MATCH_NOT_REQUIRED
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    static
      {
        PERFORMER_MAP.put("arranger",                           BMMO.P_ARRANGER);
        PERFORMER_MAP.put("balance",                            BMMO.P_BALANCE);
        PERFORMER_MAP.put("chorus master",                      BMMO.P_CHORUS_MASTER);
        PERFORMER_MAP.put("conductor",                          MO.P_CONDUCTOR);
        PERFORMER_MAP.put("editor",                             BMMO.P_EDITOR);
        PERFORMER_MAP.put("engineer",                           MO.P_ENGINEER);
        PERFORMER_MAP.put("instrument arranger",                BMMO.P_ARRANGER);
        PERFORMER_MAP.put("mastering",                          BMMO.P_MASTERING);
        PERFORMER_MAP.put("mix",                                BMMO.P_MIX);
        PERFORMER_MAP.put("orchestrator",                       BMMO.P_ORCHESTRATOR);
        PERFORMER_MAP.put("performer",                          MO.P_PERFORMER);
        PERFORMER_MAP.put("performing orchestra",               BMMO.P_ORCHESTRA);
        PERFORMER_MAP.put("producer",                           MO.P_PRODUCER);
        PERFORMER_MAP.put("programming",                        BMMO.P_PROGRAMMING);
        PERFORMER_MAP.put("recording",                          BMMO.P_RECORDING);
        PERFORMER_MAP.put("remixer",                            BMMO.P_MIX);
        PERFORMER_MAP.put("sound",                              MO.P_ENGINEER);

        PERFORMER_MAP.put("vocal",                              MO.P_SINGER);
        PERFORMER_MAP.put("vocal/additional",                   BMMO.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/alto vocals",                  BMMO.P_ALTO);
        PERFORMER_MAP.put("vocal/background vocals",            BMMO.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/baritone vocals",              BMMO.P_BARITONE);
        PERFORMER_MAP.put("vocal/bass-baritone vocals",         BMMO.P_BASS_BARITONE);
        PERFORMER_MAP.put("vocal/bass vocals",                  BMMO.P_BASS);
        PERFORMER_MAP.put("vocal/choir vocals",                 BMMO.P_CHOIR);
        PERFORMER_MAP.put("vocal/contralto vocals",             BMMO.P_CONTRALTO);
        PERFORMER_MAP.put("vocal/guest",                        MO.P_SINGER);
        PERFORMER_MAP.put("vocal/lead vocals",                  BMMO.P_LEAD_SINGER);
        PERFORMER_MAP.put("vocal/mezzo-soprano vocals",         BMMO.P_MEZZO_SOPRANO);
        PERFORMER_MAP.put("vocal/other vocals",                 BMMO.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/solo",                         BMMO.P_LEAD_SINGER);
        PERFORMER_MAP.put("vocal/soprano vocals",               BMMO.P_SOPRANO);
        PERFORMER_MAP.put("vocal/spoken vocals",                MO.P_SINGER);
        PERFORMER_MAP.put("vocal/tenor vocals",                 BMMO.P_TENOR);

        PERFORMER_MAP.put("instrument",                         MO.P_PERFORMER);
        PERFORMER_MAP.put("instrument/accordion",               BMMO.P_PERFORMER_ACCORDION);
        PERFORMER_MAP.put("instrument/acoustic guitar",         BMMO.P_PERFORMER_ACOUSTIC_GUITAR);
        PERFORMER_MAP.put("instrument/acoustic bass guitar",    BMMO.P_PERFORMER_ACOUSTIC_BASS_GUITAR);
        PERFORMER_MAP.put("instrument/agogô",                   BMMO.P_PERFORMER_AGOGO);
        PERFORMER_MAP.put("instrument/alto saxophone",          BMMO.P_PERFORMER_ALTO_SAX);
        PERFORMER_MAP.put("instrument/banjo",                   BMMO.P_PERFORMER_BANJO);
        PERFORMER_MAP.put("instrument/baritone guitar",         BMMO.P_PERFORMER_BARITONE_GUITAR);
        PERFORMER_MAP.put("instrument/baritone saxophone",      BMMO.P_PERFORMER_BARITONE_SAX);
        PERFORMER_MAP.put("instrument/bass",                    BMMO.P_PERFORMER_BASS);
        PERFORMER_MAP.put("instrument/bass clarinet",           BMMO.P_PERFORMER_BASS_CLARINET);
        PERFORMER_MAP.put("instrument/bass drum",               BMMO.P_PERFORMER_BASS_DRUM);
        PERFORMER_MAP.put("instrument/bass guitar",             BMMO.P_PERFORMER_BASS_GUITAR);
        PERFORMER_MAP.put("instrument/bass trombone",           BMMO.P_PERFORMER_BASS_TROMBONE);
        PERFORMER_MAP.put("instrument/bassoon",                 BMMO.P_PERFORMER_BASSOON);
        PERFORMER_MAP.put("instrument/bells",                   BMMO.P_PERFORMER_BELLS);
        PERFORMER_MAP.put("instrument/berimbau",                BMMO.P_PERFORMER_BERIMBAU);
        PERFORMER_MAP.put("instrument/brass",                   BMMO.P_PERFORMER_BRASS);
        PERFORMER_MAP.put("instrument/brushes",                 BMMO.P_PERFORMER_BRUSHES);
        PERFORMER_MAP.put("instrument/cello",                   BMMO.P_PERFORMER_CELLO);
        PERFORMER_MAP.put("instrument/clarinet",                BMMO.P_PERFORMER_CLARINET);
        PERFORMER_MAP.put("instrument/classical guitar",        BMMO.P_PERFORMER_CLASSICAL_GUITAR);
        PERFORMER_MAP.put("instrument/congas",                  BMMO.P_PERFORMER_CONGAS);
        PERFORMER_MAP.put("instrument/cornet",                  BMMO.P_PERFORMER_CORNET);
        PERFORMER_MAP.put("instrument/cymbals",                 BMMO.P_PERFORMER_CYMBALS);
        PERFORMER_MAP.put("instrument/double bass",             BMMO.P_PERFORMER_DOUBLE_BASS);
        PERFORMER_MAP.put("instrument/drums",                   BMMO.P_PERFORMER_DRUMS);
        PERFORMER_MAP.put("instrument/drum machine",            BMMO.P_PERFORMER_DRUM_MACHINE);
        PERFORMER_MAP.put("instrument/electric bass guitar",    BMMO.P_PERFORMER_ELECTRIC_BASS_GUITAR);
        PERFORMER_MAP.put("instrument/electric guitar",         BMMO.P_PERFORMER_ELECTRIC_GUITAR);
        PERFORMER_MAP.put("instrument/electric piano",          BMMO.P_PERFORMER_ELECTRIC_PIANO);
        PERFORMER_MAP.put("instrument/electric sitar",          BMMO.P_PERFORMER_ELECTRIC_SITAR);
        PERFORMER_MAP.put("instrument/electronic drum set",     BMMO.P_PERFORMER_ELECTRONIC_DRUM_SET);
        PERFORMER_MAP.put("instrument/english horn",            BMMO.P_PERFORMER_ENGLISH_HORN);
        PERFORMER_MAP.put("instrument/flugelhorn",              BMMO.P_PERFORMER_FLUGELHORN);
        PERFORMER_MAP.put("instrument/flute",                   BMMO.P_PERFORMER_FLUTE);
        PERFORMER_MAP.put("instrument/frame drum",              BMMO.P_PERFORMER_FRAME_DRUM);
        PERFORMER_MAP.put("instrument/french horn",             BMMO.P_PERFORMER_FRENCH_HORN);
        PERFORMER_MAP.put("instrument/glockenspiel",            BMMO.P_PERFORMER_GLOCKENSPIEL);
        PERFORMER_MAP.put("instrument/grand piano",             BMMO.P_PERFORMER_GRAND_PIANO);
        PERFORMER_MAP.put("instrument/guest",                   BMMO.P_PERFORMER_GUEST);
        PERFORMER_MAP.put("instrument/guitar",                  BMMO.P_PERFORMER_GUITAR);
        PERFORMER_MAP.put("instrument/guitar synthesizer",      BMMO.P_PERFORMER_GUITAR_SYNTHESIZER);
        PERFORMER_MAP.put("instrument/guitars",                 BMMO.P_PERFORMER_GUITARS);
        PERFORMER_MAP.put("instrument/handclaps",               BMMO.P_PERFORMER_HANDCLAPS);
        PERFORMER_MAP.put("instrument/hammond organ",           BMMO.P_PERFORMER_HAMMOND_ORGAN);
        PERFORMER_MAP.put("instrument/harmonica",               BMMO.P_PERFORMER_HARMONICA);
        PERFORMER_MAP.put("instrument/harp",                    BMMO.P_PERFORMER_HARP);
        PERFORMER_MAP.put("instrument/harpsichord",             BMMO.P_PERFORMER_HARPSICHORD);
        PERFORMER_MAP.put("instrument/hi-hat",                  BMMO.P_PERFORMER_HIHAT);
        PERFORMER_MAP.put("instrument/horn",                    BMMO.P_PERFORMER_HORN);
        PERFORMER_MAP.put("instrument/keyboard",                BMMO.P_PERFORMER_KEYBOARD);
        PERFORMER_MAP.put("instrument/koto",                    BMMO.P_PERFORMER_KOTO);
        PERFORMER_MAP.put("instrument/lute",                    BMMO.P_PERFORMER_LUTE);
        PERFORMER_MAP.put("instrument/maracas",                 BMMO.P_PERFORMER_MARACAS);
        PERFORMER_MAP.put("instrument/marimba",                 BMMO.P_PERFORMER_MARIMBA);
        PERFORMER_MAP.put("instrument/mellophone",              BMMO.P_PERFORMER_MELLOPHONE);
        PERFORMER_MAP.put("instrument/melodica",                BMMO.P_PERFORMER_MELODICA);
        PERFORMER_MAP.put("instrument/oboe",                    BMMO.P_PERFORMER_OBOE);
        PERFORMER_MAP.put("instrument/organ",                   BMMO.P_PERFORMER_ORGAN);
        PERFORMER_MAP.put("instrument/other instruments",       BMMO.P_PERFORMER_OTHER_INSTRUMENTS);
        PERFORMER_MAP.put("instrument/percussion",              BMMO.P_PERFORMER_PERCUSSION);
        PERFORMER_MAP.put("instrument/piano",                   BMMO.P_PERFORMER_PIANO);
        PERFORMER_MAP.put("instrument/piccolo trumpet",         BMMO.P_PERFORMER_PICCOLO_TRUMPET);
        PERFORMER_MAP.put("instrument/pipe organ",              BMMO.P_PERFORMER_PIPE_ORGAN);
        PERFORMER_MAP.put("instrument/psaltery",                BMMO.P_PERFORMER_PSALTERY);
        PERFORMER_MAP.put("instrument/recorder",                BMMO.P_PERFORMER_RECORDER);
        PERFORMER_MAP.put("instrument/reeds",                   BMMO.P_PERFORMER_REEDS);
        PERFORMER_MAP.put("instrument/rhodes piano",            BMMO.P_PERFORMER_RHODES_PIANO);
        PERFORMER_MAP.put("instrument/santur",                  BMMO.P_PERFORMER_SANTUR);
        PERFORMER_MAP.put("instrument/saxophone",               BMMO.P_PERFORMER_SAXOPHONE);
        PERFORMER_MAP.put("instrument/shakers",                 BMMO.P_PERFORMER_SHAKERS);
        PERFORMER_MAP.put("instrument/sitar",                   BMMO.P_PERFORMER_SITAR);
        PERFORMER_MAP.put("instrument/slide guitar",            BMMO.P_PERFORMER_SLIDE_GUITAR);
        PERFORMER_MAP.put("instrument/snare drum",              BMMO.P_PERFORMER_SNARE_DRUM);
        PERFORMER_MAP.put("instrument/solo",                    BMMO.P_PERFORMER_SOLO);
        PERFORMER_MAP.put("instrument/soprano saxophone",       BMMO.P_PERFORMER_SOPRANO_SAX);
        PERFORMER_MAP.put("instrument/spanish acoustic guitar", BMMO.P_PERFORMER_SPANISH_ACOUSTIC_GUITAR);
        PERFORMER_MAP.put("instrument/steel guitar",            BMMO.P_PERFORMER_STEEL_GUITAR);
        PERFORMER_MAP.put("instrument/synclavier",              BMMO.P_PERFORMER_SYNCLAVIER);
        PERFORMER_MAP.put("instrument/synthesizer",             BMMO.P_PERFORMER_SYNTHESIZER);
        PERFORMER_MAP.put("instrument/tambourine",              BMMO.P_PERFORMER_TAMBOURINE);
        PERFORMER_MAP.put("instrument/tenor saxophone",         BMMO.P_PERFORMER_TENOR_SAX);
        PERFORMER_MAP.put("instrument/timbales",                BMMO.P_PERFORMER_TIMBALES);
        PERFORMER_MAP.put("instrument/timpani",                 BMMO.P_PERFORMER_TIMPANI);
        PERFORMER_MAP.put("instrument/tiple",                   BMMO.P_PERFORMER_TIPLE);
        PERFORMER_MAP.put("instrument/trombone",                BMMO.P_PERFORMER_TROMBONE);
        PERFORMER_MAP.put("instrument/trumpet",                 BMMO.P_PERFORMER_TRUMPET);
        PERFORMER_MAP.put("instrument/tuba",                    BMMO.P_PERFORMER_TUBA);
        PERFORMER_MAP.put("instrument/tubular bells",           BMMO.P_PERFORMER_TUBULAR_BELLS);
        PERFORMER_MAP.put("instrument/tuned percussion",        BMMO.P_PERFORMER_TUNED_PERCUSSION);
        PERFORMER_MAP.put("instrument/ukulele",                 BMMO.P_PERFORMER_UKULELE);
        PERFORMER_MAP.put("instrument/vibraphone",              BMMO.P_PERFORMER_VIBRAPHONE);
        PERFORMER_MAP.put("instrument/viola",                   BMMO.P_PERFORMER_VIOLA);
        PERFORMER_MAP.put("instrument/viola da gamba",          BMMO.P_PERFORMER_VIOLA_DA_GAMBA);
        PERFORMER_MAP.put("instrument/violin",                  BMMO.P_PERFORMER_VIOLIN);
        PERFORMER_MAP.put("instrument/whistle",                 BMMO.P_PERFORMER_WHISTLE);
        PERFORMER_MAP.put("instrument/xylophone",               BMMO.P_PERFORMER_XYLOPHONE);
      }

    /*******************************************************************************************************************
     *
     * Aggregate of a {@link Release}, a {@link Medium} inside that {@code Release} and a {@link Disc} inside that
     * {@code Medium}.
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @AllArgsConstructor @Getter
    static class ReleaseMediumDisk
      {
        @Nonnull
        private final Release release;

        @Nonnull
        private final Medium medium;

        @Wither
        private Disc disc;

        @Wither
        private boolean alternative;

        private String embeddedTitle;

        private int score;

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public ReleaseMediumDisk withEmbeddedTitle (final @Nonnull String embeddedTitle)
          {
            return new ReleaseMediumDisk(release, medium, disc, alternative, embeddedTitle,
                                         similarity(pickTitle(), embeddedTitle));
          }

        /***************************************************************************************************************
         *
         * Prefer Medium title - typically available in case of disk collections, in which case Release has got
         * the collection title, which is very generic.
         *
         **************************************************************************************************************/
        @Nonnull
        public String pickTitle()
          {
            return Optional.ofNullable(medium.getTitle()).orElse(release.getTitle());
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public ReleaseMediumDisk alternativeIf (final boolean condition)
          {
            return withAlternative(alternative || condition);
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public Id computeId()
          {
            return createSha1IdNew(getRelease().getId() + "+" + getDisc().getId());
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public Optional<Integer> getDiskCount()
          {
            return Optional.ofNullable(release.getMediumList()).map(MediumList::getCount).map(BigInteger::intValue);
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public Optional<Integer> getDiskNumber()
          {
            return Optional.ofNullable(medium.getPosition()).map(BigInteger::intValue);
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public Optional<String> getAsin()
          {
            return Optional.ofNullable(release.getAsin());
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public Optional<String> getBarcode()
          {
            return Optional.ofNullable(release.getBarcode());
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public Cddb getCddb()
          {
            return MediaItem.Metadata.Cddb.builder()
                    .discId("") // FIXME
                    .trackFrameOffsets(disc.getOffsetList().getOffset()
                            .stream()
                            .map(offset -> offset.getValue())
                            .mapToInt(x -> x.intValue())
                            .toArray())
                    .build();
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Nonnull
        public String getMediumAndDiscString()
          {
            return String.format("%s/%s", medium.getTitle(), (disc != null) ? disc.getId() : "null");
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Override
        public boolean equals (final @CheckForNull Object other)
          {
            if (this == other)
              {
                return true;
              }

            if ((other == null) || (getClass() != other.getClass()))
              {
                return false;
              }

            return Objects.equals(this.computeId(), ((ReleaseMediumDisk)other).computeId());
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Override
        public int hashCode()
          {
            return computeId().hashCode();
          }

        /***************************************************************************************************************
         *
         **************************************************************************************************************/
        @Override @Nonnull
        public String toString()
          {
            return String.format("ALT: %-5s ASIN: %-10s BARCODE: %-13s SCORE: %4d #: %3s/%3s PICKED: %s EMBEDDED: %s RELEASE: %s MEDIUM: %s",
                        alternative,
                        release.getAsin(), release.getBarcode(),
                        getScore(),
                        getDiskNumber().map(n -> "" + n).orElse(""), getDiskCount().map(n -> "" + n).orElse(""),
                        pickTitle(), embeddedTitle, release.getTitle(), medium.getTitle());
          }
      }

    /*******************************************************************************************************************
     *
     * Aggregate of a {@link Relation} and a target type.
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor(access = PRIVATE) @Getter
    static class RelationAndTargetType
      {
        @Nonnull
        private final Relation relation;

        @Nonnull
        private final String targetType;

        @Nonnull
        public static Stream<RelationAndTargetType> toStream (final @Nonnull RelationList relationList)
          {
            return relationList.getRelation().stream()
                                             .map(rel -> new RelationAndTargetType(rel, relationList.getTargetType()));
          }
      }

    /*******************************************************************************************************************
     *
     * Downloads and imports MusicBrainz data for the given {@link Metadata}.
     *
     * @param   metadata                the {@code Metadata}
     * @return                          the RDF triples
     * @throws  InterruptedException    in case of I/O error
     * @throws  IOException             in case of I/O error
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Model> handleMetadata (final @Nonnull Metadata metadata)
      throws InterruptedException, IOException
      {
        final ModelBuilder model                  = createModelBuilder();
        final Optional<String> optionalAlbumTitle = metadata.get(ALBUM);
        final Optional<Cddb> optionalCddb         = metadata.get(CDDB);

        if (optionalAlbumTitle.isPresent() && !optionalAlbumTitle.get().trim().isEmpty() && optionalCddb.isPresent())
          {
            final String albumTitle = optionalAlbumTitle.get();
            final Cddb cddb         = optionalCddb.get();
            final String toc        = cddb.getToc();

            synchronized (processedTocs)
              {
                if (processedTocs.contains(toc))
                  {
                    return Optional.empty();
                  }

                processedTocs.add(toc);
              }

            log.info("QUERYING MUSICBRAINZ FOR TOC OF: {}", albumTitle);
            final List<ReleaseMediumDisk> rmds = new ArrayList<>();
            final RestResponse<ReleaseList> releaseList = mbMetadataProvider.findReleaseListByToc(toc, TOC_INCLUDES);
            // even though we're querying by TOC, matching offsets is required to kill many false results
            releaseList.ifPresent(releases -> rmds.addAll(findReleases(releases, cddb, Validation.TRACK_OFFSETS_MATCH_REQUIRED)));

            if (rmds.isEmpty())
              {
                log.info("TOC NOT FOUND, QUERYING MUSICBRAINZ FOR TITLE: {}", albumTitle);
                final List<ReleaseGroup> releaseGroups = new ArrayList<>();
                releaseGroups.addAll(mbMetadataProvider.findReleaseGroupByTitle(albumTitle)
                                                       .map(ReleaseGroupList::getReleaseGroup)
                                                       .orElse(emptyList()));

                final Optional<String> alternateTitle = cddbAlternateTitleOf(metadata);
                alternateTitle.ifPresent(t -> log.info("ALSO USING ALTERNATE TITLE: {}", t));
                releaseGroups.addAll(alternateTitle.map(_f(mbMetadataProvider::findReleaseGroupByTitle))
                                                   .map(response -> response.get().getReleaseGroup())
                                                   .orElse(emptyList()));
                rmds.addAll(findReleases(releaseGroups, cddb, Validation.TRACK_OFFSETS_MATCH_REQUIRED));
              }

            model.with(markedAlternative(rmds, albumTitle).stream()
                                                             .parallel()
                                                             .map(_f(rmd -> handleRelease(metadata, rmd)))
                                                             .collect(toList()));
          }

        return Optional.of(model.toModel());
      }

    /*******************************************************************************************************************
     *
     * Given a valid list of {@link ReleaseMediumDisk}s - that is, that has been already validated and correctly matches
     * the searched record - if it contains more than one element picks the most suitable one. Unwanted elements are
     * not filtered out, because it's not always possible to automatically pick the best one: in fact, some entries
     * might differ for ASIN or barcode; or might be items individually sold or part of a collection. It makes sense to
     * offer the user the possibility of manually pick them later. So, instead of being filtered out, those elements
     * are marked as "alternative" (and they will be later marked as such in the triple store).
     *
     * These are the performed steps:
     *
     * <ol>
     * <li>Eventual duplicates are collapsed.</li>
     * <li>If required, in case of members of collections, collections that are larger than the least are marked as
     *     alternative.</li>
     * <li>A matching score is computed about the affinity of the title found in MusicBrainz metadata with respected
     *     to the title in the embedded metadata.</li>
     * <li>Elements that don't reach the maximum score are marked as alternative.</li>
     * <li>If at least one element has got the ASIN, other elements that don't bear it are marked as alternative.</li>
     * <li>If at least one element has got the barcode, other elements that don't bear it are marked as alternative.
     * </li>
     * <li>If the pick is not unique yet, an ASIN is picked as the first in lexicoraphic order and elements not
     *     bearing it are marked as alternative.</li>
     * <li>If the pick is not unique yet, a barcode is picked as the first in lexicoraphic order and elements not
     *     bearing it are marked as alternative.</li>
     * <li>If the pick is not unique yet, elements other than the first one are marked as alternative.</i>
     * </ol>
     *
     * The last criteria are implemented for giving consistency to automated tests, considering that the order in which
     * elements are found is not guaranteed because of multi-threading.
     *
     * @param   inRmds          the incoming {@code ReleaseAndMedium}s
     * @param   embeddedTitle   the album title found in the file
     * @return                  the outcoming {@code ReleaseAndMedium}s
     *
     ******************************************************************************************************************/
    @Nonnull
    private List<ReleaseMediumDisk> markedAlternative (final @Nonnull List<ReleaseMediumDisk> inRmds,
                                                       final @Nonnull String embeddedTitle)
      {
        if (inRmds.size() <= 1)
          {
            return inRmds;
          }

        List<ReleaseMediumDisk> rmds = new ArrayList<>(inRmds.stream()
                                                             .map(rmd -> rmd.withEmbeddedTitle(embeddedTitle))
                                                             .collect(toSet()));
        rmds = discourageCollections ? markedAlternativeIfNotLeastCollection(rmds) :rmds;
        rmds = markedAlternativeByTitleAffinity(rmds);

        final boolean asinPresent = rmds.stream().filter(rmd -> !rmd.isAlternative() && rmd.getAsin().isPresent()).findAny().isPresent();
        rmds = rmds.stream().map(rmd -> rmd.alternativeIf(asinPresent && !rmd.getAsin().isPresent())).collect(toList());

        final boolean barcodePresent = rmds.stream().filter(rmd -> !rmd.isAlternative() && rmd.getBarcode().isPresent()).findAny().isPresent();
        rmds = rmds.stream().map(rmd -> rmd.alternativeIf(barcodePresent && !rmd.getBarcode().isPresent())).collect(toList());

        if (asinPresent && (countOfNotAlternative(rmds) > 1))
          {
            final Optional<String> asin = rmds.stream().filter(rmd -> !rmd.isAlternative())
                                                       .map(rmd -> rmd.getAsin().get())
                                                       .sorted()
                                                       .findFirst();
            rmds = rmds.stream().map(rmd -> rmd.alternativeIf(!rmd.getAsin().equals(asin))).collect(toList());
          }

        if (barcodePresent && (countOfNotAlternative(rmds) > 1))
          {
            final Optional<String> barcode = rmds.stream().filter(rmd -> !rmd.isAlternative())
                                                          .map(rmd -> rmd.getBarcode().get())
                                                          .sorted()
                                                          .findFirst();
            rmds = rmds.stream().map(rmd -> rmd.alternativeIf(!rmd.getBarcode().equals(barcode))).collect(toList());
          }

        rmds = excessKeepersMarkedAlternative(rmds);

        synchronized (log) // keep log lines together
          {
            log.info("MULTIPLE RESULTS");
            rmds.stream().forEach(rmd -> log.info(">>> MULTIPLE RESULTS: {}", rmd.toString()));
          }

        final int count = countOfNotAlternative(rmds);
        assert count == 1 : "Still too many items not alternative: " + count;

        return rmds;
      }

    /*******************************************************************************************************************
     *
     * Sweeps the given {@link ReleaseMediumDisk}s and marks as alternative all the items after a not alternative item.
     *
     * @param   rmds    the incoming {@code ReleaseMediumDisk}
     * @return          the processed {@code ReleaseMediumDisk}
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<ReleaseMediumDisk> excessKeepersMarkedAlternative (final @Nonnull List<ReleaseMediumDisk> rmds)
      {
        if (countOfNotAlternative(rmds) > 1)
          {
            boolean foundGoodOne = false;
            // FIXME: should be sorted for test consistency
            for (int i = 0; i < rmds.size(); i++)
              {
                rmds.set(i, rmds.get(i).alternativeIf(foundGoodOne));
                foundGoodOne |= !rmds.get(i).isAlternative();
              }
          }

        return rmds;
      }

    /*******************************************************************************************************************
     *
     * Sweeps the given {@link ReleaseMediumDisk}s and marks as alternative all the items which are not part of the
     * disk collections with the minimum size.
     *
     * @param   rmds    the incoming {@code ReleaseMediumDisk}
     * @return          the processed {@code ReleaseMediumDisk}
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<ReleaseMediumDisk> markedAlternativeIfNotLeastCollection (final @Nonnull List<ReleaseMediumDisk> rmds)
      {
        final int leastSize = rmds.stream().filter(rmd -> !rmd.isAlternative())
                                           .mapToInt(rmd -> rmd.getDiskCount().orElse(1))
                                           .min().getAsInt();
        return rmds.stream().map(rmd -> rmd.alternativeIf(rmd.getDiskCount().orElse(1) > leastSize)).collect(toList());
      }

    /*******************************************************************************************************************
     *
     * Sweeps the given {@link ReleaseMediumDisk}s and marks as alternative the items without the best score.
     *
     * @param   rmds    the incoming {@code ReleaseMediumDisk}
     * @return          the processed {@code ReleaseMediumDisk}
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<ReleaseMediumDisk> markedAlternativeByTitleAffinity (final @Nonnull List<ReleaseMediumDisk> rmds)
      {
        final int bestScore = rmds.stream().filter(rmd -> !rmd.isAlternative())
                                           .mapToInt(ReleaseMediumDisk::getScore)
                                           .max().getAsInt();
        return rmds.stream().map(rmd -> rmd.alternativeIf(rmd.getScore() < bestScore)).collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnegative
    private static int countOfNotAlternative (final @Nonnull List<ReleaseMediumDisk> rmds)
      {
        return (int)rmds.stream().filter(rmd -> !rmd.isAlternative()).count();
      }

    /*******************************************************************************************************************
     *
     * Extracts data from the given release. For MusicBrainz, a Release is typically a disk, but it can be made of
     * multiple disks in case of many tracks.
     *
     * @param   metadata                the {@code Metadata}
     * @param   rmd                     the release
     * @return                          the RDF triples
     * @throws  InterruptedException    in case of I/O error
     * @throws  IOException             in case of I/O error
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleRelease (final @Nonnull Metadata metadata, final @Nonnull ReleaseMediumDisk rmd)
      throws IOException, InterruptedException
      {
        final Medium medium              = rmd.getMedium();
        final String releaseId           = rmd.getRelease().getId();
        final List<DefTrackData> tracks  = medium.getTrackList().getDefTrack();
        final String embeddedRecordTitle = metadata.get(ALBUM).get(); // .orElse(parent.getPath().toFile().getName());
        final Cddb cddb                  = metadata.get(CDDB).get();
        final String recordTitle         = rmd.pickTitle();
        final IRI embeddedRecordIri      = recordIriOf(metadata, embeddedRecordTitle);
        final IRI recordIri              = BMMO.recordIriFor(rmd.computeId());
        log.info("importing {} {} ...", recordTitle, (rmd.isAlternative() ? "(alternative)" : ""));

        ModelBuilder model = createModelBuilder()
            .with(recordIri, RDF.TYPE,              MO.C_RECORD)
            .with(recordIri, RDFS.LABEL,            literalFor(recordTitle))
            .with(recordIri, DC.TITLE,              literalFor(recordTitle))
            .with(recordIri, BMMO.P_IMPORTED_FROM,  BMMO.O_SOURCE_MUSICBRAINZ)
            .with(recordIri, BMMO.P_ALTERNATE_OF,   embeddedRecordIri)
            .with(recordIri, MO.P_MEDIA_TYPE,       MO.C_CD)
            .with(recordIri, MO.P_TRACK_COUNT,      literalFor(tracks.size()))
            .with(recordIri, MO.P_MUSICBRAINZ_GUID, literalFor(releaseId))
            .with(recordIri, MO.P_MUSICBRAINZ,      musicBrainzIriFor("release", releaseId))
            .with(recordIri, MO.P_AMAZON_ASIN,      literalFor(rmd.getAsin()))
            .with(recordIri, MO.P_GTIN,             literalFor(rmd.getBarcode()))

            .with(tracks.stream().parallel()
                                 .map(_f(track -> handleTrack(rmd, cddb, recordIri, track)))
                                 .collect(toList()));

        if (rmd.isAlternative())
          {
            model = model.with(recordIri, BMMO.P_ALTERNATE_PICK_OF, embeddedRecordIri);
          }

        return model;
        // TODO: release.getLabelInfoList();
        // TODO: record producer - requires inc=artist-rels
      }

    /*******************************************************************************************************************
     *
     * Extracts data from the given {@link DefTrackData}.
     *
     * @param   rmd                     the release
     * @param   cddb                    the CDDB of the track we're handling
     * @param   track                   the track
     * @return                          the RDF triples
     * @throws  InterruptedException    in case of I/O error
     * @throws  IOException             in case of I/O error
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrack (final @Nonnull ReleaseMediumDisk rmd,
                                      final @Nonnull Cddb cddb,
                                      final @Nonnull IRI recordIri,
                                      final @Nonnull DefTrackData track)
      throws IOException, InterruptedException
      {
        final IRI trackIri                 = trackIriOf(track.getId());
        final int trackNumber              = track.getPosition().intValue();
        final Optional<Integer> diskCount  = emptyIfOne(rmd.getDiskCount());
        final Optional<Integer> diskNumber = diskCount.flatMap(dc -> rmd.getDiskNumber());
        final String recordingId           = track.getRecording().getId();
//                final Recording recording = track.getRecording();
        final Recording recording = mbMetadataProvider.getResource(RECORDING, recordingId, RECORDING_INCLUDES).get();
        final String trackTitle   = recording.getTitle();
//                    track.getRecording().getAliasList().getAlias().get(0).getSortName();
        final IRI signalIri       = signalIriFor(cddb, track.getPosition().intValue());
        log.info(">>>>>>>> {}. {}", trackNumber, trackTitle);

        return createModelBuilder()
            .with(recordIri, MO.P_TRACK,            trackIri)
            .with(recordIri, BMMO.P_DISK_COUNT,     literalForInt(diskCount))
            .with(recordIri, BMMO.P_DISK_NUMBER,    literalForInt(diskNumber))

            .with(signalIri, MO.P_PUBLISHED_AS,     trackIri)

            .with(trackIri,  RDF.TYPE,              MO.C_TRACK)
            .with(trackIri,  RDFS.LABEL,            literalFor(trackTitle))
            .with(trackIri,  DC.TITLE,              literalFor(trackTitle))
            .with(trackIri,  BMMO.P_IMPORTED_FROM,  BMMO.O_SOURCE_MUSICBRAINZ)
            .with(trackIri,  MO.P_TRACK_NUMBER,     literalFor(trackNumber))
            .with(trackIri,  MO.P_MUSICBRAINZ_GUID, literalFor(track.getId()))
            .with(trackIri,  MO.P_MUSICBRAINZ,      musicBrainzIriFor("track", track.getId()))

            .with(handleTrackRelations(signalIri, trackIri, recordIri, recording));
      }

    /*******************************************************************************************************************
     *
     * Extracts data from the relations of the given {@link Recording}.
     *
     * @param   signalIri   the IRI of the signal associated to the track we're handling
     * @param   recording   the {@code Recording}
     * @return              the RDF triples
    *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrackRelations (final @Nonnull IRI signalIri,
                                               final @Nonnull IRI trackIri,
                                               final @Nonnull IRI recordIri,
                                               final @Nonnull Recording recording)
      {
        return createModelBuilder().with(recording.getRelationList()
                                                  .stream()
                                                  .parallel()
                                                  .flatMap(RelationAndTargetType::toStream)
                                                  .map(ratt -> handleTrackRelation(signalIri, trackIri, recordIri, recording, ratt))
                                                  .collect(toList()));
      }

    /*******************************************************************************************************************
     *
     * Extracts data from a relation of the given {@link Recording}.
     *
     * @param   signalIri   the IRI of the signal associated to the track we're handling
     * @param   recording   the {@code Recording}
     * @param   ratt        the relation
     * @return              the RDF triples
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrackRelation (final @Nonnull IRI signalIri,
                                              final @Nonnull IRI trackIri,
                                              final @Nonnull IRI recordIri,
                                              final @Nonnull Recording recording,
                                              final @Nonnull RelationAndTargetType ratt)
      {
        final Relation relation = ratt.getRelation();
        final String targetType = ratt.getTargetType();
        final List<Attribute> attributes = getAttributes(relation);
//        final Target target = relation.getTarget();
        final String type   = relation.getType();
        final Artist artist = relation.getArtist();

        log.info(">>>>>>>>>>>> {} {} {} {} ({})", targetType,
                                                  type,
                                                  attributes.stream().map(a -> toString(a)).collect(toList()),
                                                  artist.getName(),
                                                  artist.getId());

        final IRI performanceIri = performanceIriFor(recording.getId());
        final IRI artistIri      = artistIriOf(artist.getId());

        final ModelBuilder model = createModelBuilder()
            .with(performanceIri,  RDF.TYPE,              MO.C_PERFORMANCE)
            .with(performanceIri,  BMMO.P_IMPORTED_FROM,  BMMO.O_SOURCE_MUSICBRAINZ)
            .with(performanceIri,  MO.P_MUSICBRAINZ_GUID, literalFor(recording.getId()))
            .with(performanceIri,  MO.P_RECORDED_AS,      signalIri)

            .with(artistIri,       RDF.TYPE,              MO.C_MUSIC_ARTIST)
            .with(artistIri,       RDFS.LABEL,            literalFor(artist.getName()))
            .with(artistIri,       FOAF.NAME,             literalFor(artist.getName()))
            .with(artistIri,       BMMO.P_IMPORTED_FROM,  BMMO.O_SOURCE_MUSICBRAINZ)
            .with(artistIri,       MO.P_MUSICBRAINZ_GUID, literalFor(artist.getId()))
            .with(artistIri,       MO.P_MUSICBRAINZ,      musicBrainzIriFor("artist", artist.getId()))

            // TODO these could be inferred - performance shortcuts. Catalog queries rely upon these.
            .with(recordIri,       FOAF.MAKER,            artistIri)
            .with(trackIri,        FOAF.MAKER,            artistIri)
            .with(performanceIri,  FOAF.MAKER,            artistIri);
//            .with(signalIri,       FOAF.MAKER,          artistIri);

        if ("artist".equals(targetType))
          {
            predicatesForArtists(type, attributes)
                    .forEach(predicate -> model.with(performanceIri, predicate, artistIri));
          }

        return model;
//                        relation.getBegin();
//                        relation.getEnd();
//                        relation.getEnded();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<IRI> predicatesForArtists (final @Nonnull String type, final @Nonnull List<Attribute> attributes)
      {
        if (attributes.isEmpty())
          {
            return singletonList(predicateFor(type));
          }
        else
          {
            return attributes.stream().map(attribute ->
              {
                String role = type;

                if (type.equals("vocal") || type.equals("instrument"))
                  {
                    role += "/" + attribute.getContent();
                  }

                return predicateFor(role);
              }).collect(toList());
          }
      }

    /*******************************************************************************************************************
     *
     * Given a list of {@link ReleaseGroup}s, navigates into it and extract all CD {@link Medium}s that match the
     * given CDDB track offsets.
     *
     * @param   releaseGroups   the {@code ReleaseGroup}s
     * @param   cddb            the track offsets
     * @param   validation      how the results must be validated
     * @return                  a collection of filtered {@code Medium}s
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<ReleaseMediumDisk> findReleases (final @Nonnull List<ReleaseGroup> releaseGroups,
                                                        final @Nonnull Cddb cddb,
                                                        final @Nonnull Validation validation)
      {
        return releaseGroups.stream()
                            .parallel()
                            .filter(releaseGroup -> scoreOf(releaseGroup) >= releaseGroupScoreThreshold)
                            .peek(this::logArtists)
                            .map(releaseGroup -> releaseGroup.getReleaseList())
                            .flatMap(releaseList -> findReleases(releaseList, cddb, validation).stream())
                            .collect(toList());
      }

    /*******************************************************************************************************************
     *
     * Given a {@link ReleaseList}, navigates into it and extract all CD {@link Medium}s that match the given CDDB track
     * offsets.
     *
     * @param   releaseList     the {@code ReleaseList}
     * @param   cddb            the track offsets to match
     * @param   validation      how the results must be validated
     * @return                  a collection of filtered {@code Medium}s
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<ReleaseMediumDisk> findReleases (final @Nonnull ReleaseList releaseList,
                                                        final @Nonnull Cddb cddb,
                                                        final @Nonnull Validation validation)
      {
        return releaseList.getRelease().stream()
            .parallel()
//            .peek(this::logArtists)
            .peek(release -> log.info(">>>>>>>> release: {} {}", release.getId(), release.getTitle()))
            .flatMap(_f(release -> mbMetadataProvider.getResource(RELEASE, release.getId(), RELEASE_INCLUDES).get()
                            .getMediumList().getMedium()
                            .stream()
                            .map(medium -> new ReleaseMediumDisk(release, medium))))
            .filter(rmd -> matchesFormat(rmd))
            .flatMap(rmd -> rmd.getMedium().getDiscList().getDisc().stream().map(disc -> rmd.withDisc(disc)))
            .filter(rmd -> matchesTrackOffsets(rmd, cddb, validation))
            .peek(rmd -> log.info(">>>>>>>> FOUND {} - with score {}", rmd.getMediumAndDiscString(), 0 /* scoreOf(releaseGroup) FIXME */))
            .collect(toMap(rmd -> rmd.getRelease().getId(), rmd -> rmd, (u, v) -> v, TreeMap::new))
            .values();
      }

    /*******************************************************************************************************************
     *
     *
     *
     *
     ******************************************************************************************************************/
    public static int similarity (final @Nonnull String a, final @Nonnull String b)
      {
        int score = StringUtils.getFuzzyDistance(a.toLowerCase(), b.toLowerCase(), Locale.UK);
        //
        // While this is a hack, it isn't so ugly as it might appear. The idea is to give a lower score to
        // collections and records with a generic title, hoping that a better one is picked.
        // FIXME: put into a map and then into an external resource with the delta score associated.
        // FIXME: with the filtering on collection size, this might be useless?
        //
        if (a.matches("^Great Violin Concertos.*")
         || a.matches("^CBS Great Performances.*"))
          {
            score -= 50;
          }

        if (a.matches("^Piano Concertos$")
         || a.matches("^Klavierkonzerte$"))
          {
            score -= 30;
          }

        return score;
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} if the given {@link Medium} is of a meaningful type (that is, a CD) or it's not set.
     *
     * @param   medium  the {@code Medium}
     * @return          {@code true} if there is a match
     *
     ******************************************************************************************************************/
    private static boolean matchesFormat (final @Nonnull ReleaseMediumDisk rmd)
      {
        final String format = rmd.getMedium().getFormat();

        if ((format != null) && !"CD".equals(format))
          {
            log.info(">>>>>>>> discarded {} because not a CD ({})", rmd.getMediumAndDiscString(), format);
            return false;
          }

        return true;
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} if the given {@link ReleaseMediumDisk} matches the track offsets in the given {@link Cddb}.
     *
     * @param   rmd             the {@code ReleaseMediumDisk}
     * @param   requestedCddb   the track offsets to match
     * @param   validation      how the results must be validated
     * @return                  {@code true} if there is a match
     *
     ******************************************************************************************************************/
    private boolean matchesTrackOffsets (final @Nonnull ReleaseMediumDisk rmd,
                                         final @Nonnull Cddb requestedCddb,
                                         final @Nonnull Validation validation)
      {
        final Cddb cddb = rmd.getCddb();

        if ((cddb == null) && (validation == Validation.TRACK_OFFSETS_MATCH_NOT_REQUIRED))
          {
            log.info(">>>>>>>> no track offsets, but not required");
            return true;
          }

        final boolean matches = requestedCddb.matches(cddb, trackOffsetsMatchThreshold);

        if (!matches)
          {
            synchronized (log) // keep log lines together
              {
                log.info(">>>>>>>> discarded {} because track offsets don't match", rmd.getMediumAndDiscString());
                log.debug(">>>>>>>> iTunes offsets: {}", requestedCddb.getTrackFrameOffsets());
                log.debug(">>>>>>>> found offsets:  {}", cddb.getTrackFrameOffsets());
              }
          }

        return matches;
      }

    /*******************************************************************************************************************
     *
     * Searches for an alternate title of a record by querying the embedded title against the CDDB. The CDDB track
     * offsets are checked to validate the result.
     *
     * @param   metadata    the {@code Metadata}
     * @return              the title, if found
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<String> cddbAlternateTitleOf (final @Nonnull Metadata metadata)
      throws IOException, InterruptedException
      {
        final RestResponse<CddbAlbum> optionalAlbum = cddbMetadataProvider.findCddbAlbum(metadata);

        if (!optionalAlbum.isPresent())
          {
            return Optional.empty();
          }

        final CddbAlbum album = optionalAlbum.get();
        final Cddb albumCddb = album.getCddb();
        final Cddb requestedCddb = metadata.get(ITUNES_COMMENT).get().getCddb();
        final Optional<String> dTitle = album.getProperty("DTITLE");

        if (!albumCddb.matches(requestedCddb, trackOffsetsMatchThreshold))
          {
            synchronized (log) // keep log lines together
              {
                log.info(">>>> discarded alternate title because of mismatching track offsets: {}", dTitle);
                log.debug(">>>>>>>> found track offsets:    {}", albumCddb.getTrackFrameOffsets());
                log.debug(">>>>>>>> searched track offsets: {}", requestedCddb.getTrackFrameOffsets());
                log.debug(">>>>>>>> ppm                     {}", albumCddb.computeDifference(requestedCddb));
              }

            return Optional.empty();
          }

        return dTitle;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<Attribute> getAttributes (final @Nonnull Relation relation)
      {
        final List<Attribute> attributes = new ArrayList<>();

        if (relation.getAttributeList() != null)
          {
            attributes.addAll(relation.getAttributeList().getAttribute());
          }

        return attributes;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static ModelBuilder createModelBuilder()
      {
        return new ModelBuilder(SOURCE_MUSICBRAINZ);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI artistIriOf (final @Nonnull String id)
      {
        return BMMO.artistIriFor(createSha1IdNew(musicBrainzIriFor("artist", id).stringValue()));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI trackIriOf (final @Nonnull String id)
      {
        return BMMO.trackIriFor(createSha1IdNew(musicBrainzIriFor("track", id).stringValue()));
      }

    /*******************************************************************************************************************
     *
     * FIXME: DUPLICATED FROM EmbbededAudioMetadataImporter
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI recordIriOf (final @Nonnull Metadata metadata, final @Nonnull String recordTitle)
      {
        final Optional<Cddb> cddb = metadata.get(CDDB);
        return BMMO.recordIriFor((cddb.isPresent()) ? createSha1IdNew(cddb.get().getToc())
                                                    : createSha1IdNew("RECORD:" + recordTitle));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI signalIriFor (final @Nonnull Cddb cddb, final @Nonnegative int trackNumber)
      {
        return BMMO.signalIriFor(createSha1IdNew(cddb.getToc() + "/" + trackNumber));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI performanceIriFor (final @Nonnull String id)
      {
        return BMMO.performanceIriFor(createSha1IdNew(musicBrainzIriFor("performance", id).stringValue()));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI musicBrainzIriFor (final @Nonnull String resourceType, final @Nonnull String id)
      {
        return FACTORY.createIRI(String.format("http://musicbrainz.org/%s/%s", resourceType, id));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI predicateFor (final @Nonnull String role)
      {
        return Objects.requireNonNull(PERFORMER_MAP.get(role.toLowerCase()), "Cannot map role: " + role);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private static int scoreOf (final @Nonnull ReleaseGroup releaseGroup)
      {
        return Integer.parseInt(releaseGroup.getOtherAttributes().get(QNAME_SCORE));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void logArtists (final @Nonnull ReleaseGroup releaseGroup)
      {
        log.debug(">>>> {} {} {} artist: {}",
                  releaseGroup.getOtherAttributes().get(QNAME_SCORE),
                  releaseGroup.getId(),
                  releaseGroup.getTitle(),
                  releaseGroup.getArtistCredit().getNameCredit().stream().map(nc -> nc.getArtist().getName()).collect(toList()));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Optional<Integer> emptyIfOne (final @Nonnull Optional<Integer> number)
      {
        return number.flatMap(n -> (n == 1) ? Optional.empty() : Optional.of(n));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String toString (final @Nonnull Attribute attribute)
      {
        return String.format("%s %s (%s)", attribute.getContent(), attribute.getCreditedAs(), attribute.getValue());
      }
  }
