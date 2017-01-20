/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.impl.audio.musicbrainz;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import java.io.IOException;
import org.musicbrainz.ns.mmd_2.Artist;
import org.musicbrainz.ns.mmd_2.DefTrackData;
import org.musicbrainz.ns.mmd_2.Medium;
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
import it.tidalwave.bluemarine2.util.ModelBuilder;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.Cddb;
import it.tidalwave.bluemarine2.model.vocabulary.*;
import it.tidalwave.bluemarine2.rest.RestResponse;
import it.tidalwave.bluemarine2.metadata.cddb.CddbAlbum;
import it.tidalwave.bluemarine2.metadata.cddb.CddbMetadataProvider;
import it.tidalwave.bluemarine2.metadata.musicbrainz.MusicBrainzMetadataProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.metadata.cddb.impl.MusicBrainzUtilities.*;
import static it.tidalwave.bluemarine2.metadata.musicbrainz.MusicBrainzMetadataProvider.*;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
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

    private static final IRI SOURCE_MUSICBRAINZ = FACTORY.createIRI(BM.PREFIX, "source#musicbrainz");

    @Nonnull
    private final CddbMetadataProvider cddbMetadataProvider;

    @Nonnull
    private final MusicBrainzMetadataProvider mbMetadataProvider;

    @Getter @Setter
    private int trackOffsetsMatchThreshold = 2500;

    @Getter @Setter
    private int releaseGroupScoreThreshold = 50;

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
        PERFORMER_MAP.put("chorus master",                      MO.P_CONDUCTOR);
        PERFORMER_MAP.put("conductor",                          MO.P_CONDUCTOR);
        PERFORMER_MAP.put("performer",                          MO.P_PERFORMER);
        PERFORMER_MAP.put("performing orchestra",               MO.P_PERFORMER); // FIXME: should be P_ORCHESTRA

        PERFORMER_MAP.put("arranger",                           BM.P_ARRANGER);
        PERFORMER_MAP.put("balance",                            BM.P_BALANCE);
        PERFORMER_MAP.put("editor",                             BM.P_EDITOR);
        PERFORMER_MAP.put("engineer",                           BM.P_ENGINEER);
        PERFORMER_MAP.put("instrument arranger",                BM.P_ARRANGER);
        PERFORMER_MAP.put("mastering",                          BM.P_MIX);
        PERFORMER_MAP.put("mix",                                BM.P_MIX);
        PERFORMER_MAP.put("remixer",                            BM.P_MIX);
        PERFORMER_MAP.put("orchestrator",                       BM.P_ORCHESTRATOR);
        PERFORMER_MAP.put("producer",                           BM.P_PRODUCER);
        PERFORMER_MAP.put("programming",                        BM.P_PROGRAMMING);
        PERFORMER_MAP.put("recording",                          BM.P_RECORDING);
        PERFORMER_MAP.put("sound",                              BM.P_ENGINEER);

        PERFORMER_MAP.put("vocal",                              MO.P_SINGER);
        PERFORMER_MAP.put("vocal/additional",                   BM.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/background vocals",            BM.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/baritone vocals",              BM.P_BARITONE);
        PERFORMER_MAP.put("vocal/bass vocals",                  BM.P_BASS);
        PERFORMER_MAP.put("vocal/choir vocals",                 MO.P_CHOIR);
        PERFORMER_MAP.put("vocal/guest",                        MO.P_SINGER);
        PERFORMER_MAP.put("vocal/lead vocals",                  MO.P_LEAD_SINGER);
        PERFORMER_MAP.put("vocal/mezzo-soprano vocals",         BM.P_MEZZO_SOPRANO);
        PERFORMER_MAP.put("vocal/other vocals",                 BM.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/solo",                         MO.P_LEAD_SINGER);
        PERFORMER_MAP.put("vocal/soprano vocals",               MO.P_SOPRANO);
        PERFORMER_MAP.put("vocal/spoken vocals",                MO.P_SINGER);

        PERFORMER_MAP.put("instrument",                         MO.P_PERFORMER);
        PERFORMER_MAP.put("instrument/accordion",               BM.P_PERFORMER_ACCORDION);
        PERFORMER_MAP.put("instrument/acoustic guitar",         BM.P_PERFORMER_ACOUSTIC_GUITAR);
        PERFORMER_MAP.put("instrument/acoustic bass guitar",    BM.P_PERFORMER_ACOUSTIC_BASS_GUITAR);
        PERFORMER_MAP.put("instrument/agogô",                   BM.P_PERFORMER_AGOGO);
        PERFORMER_MAP.put("instrument/alto saxophone",          BM.P_PERFORMER_ALTO_SAX);
        PERFORMER_MAP.put("instrument/banjo",                   BM.P_PERFORMER_BANJO);
        PERFORMER_MAP.put("instrument/baritone guitar",         BM.P_PERFORMER_BARITONE_GUITAR);
        PERFORMER_MAP.put("instrument/bass",                    BM.P_PERFORMER_BASS);
        PERFORMER_MAP.put("instrument/bass clarinet",           BM.P_PERFORMER_BASS_CLARINET);
        PERFORMER_MAP.put("instrument/bass drum",               BM.P_PERFORMER_BASS_DRUM);
        PERFORMER_MAP.put("instrument/bass guitar",             BM.P_PERFORMER_BASS_GUITAR);
        PERFORMER_MAP.put("instrument/bass trombone",           BM.P_PERFORMER_BASS_TROMBONE);
        PERFORMER_MAP.put("instrument/bassoon",                 BM.P_PERFORMER_BASSOON);
        PERFORMER_MAP.put("instrument/bells",                   BM.P_PERFORMER_BELLS);
        PERFORMER_MAP.put("instrument/berimbau",                BM.P_PERFORMER_BERIMBAU);
        PERFORMER_MAP.put("instrument/brass",                   BM.P_PERFORMER_BRASS);
        PERFORMER_MAP.put("instrument/brushes",                 BM.P_PERFORMER_BRUSHES);
        PERFORMER_MAP.put("instrument/cello",                   BM.P_PERFORMER_CELLO);
        PERFORMER_MAP.put("instrument/clarinet",                BM.P_PERFORMER_CLARINET);
        PERFORMER_MAP.put("instrument/classical guitar",        BM.P_PERFORMER_CLASSICAL_GUITAR);
        PERFORMER_MAP.put("instrument/congas",                  BM.P_PERFORMER_CONGAS);
        PERFORMER_MAP.put("instrument/cornet",                  BM.P_PERFORMER_CORNET);
        PERFORMER_MAP.put("instrument/cymbals",                 BM.P_PERFORMER_CYMBALS);
        PERFORMER_MAP.put("instrument/double bass",             BM.P_PERFORMER_DOUBLE_BASS);
        PERFORMER_MAP.put("instrument/drums",                   BM.P_PERFORMER_DRUMS);
        PERFORMER_MAP.put("instrument/electric bass guitar",    BM.P_PERFORMER_ELECTRIC_BASS_GUITAR);
        PERFORMER_MAP.put("instrument/electric guitar",         BM.P_PERFORMER_ELECTRIC_GUITAR);
        PERFORMER_MAP.put("instrument/electric piano",          BM.P_PERFORMER_ELECTRIC_PIANO);
        PERFORMER_MAP.put("instrument/electric sitar",          BM.P_PERFORMER_ELECTRIC_SITAR);
        PERFORMER_MAP.put("instrument/electronic drum set",     BM.P_PERFORMER_ELECTRONIC_DRUM_SET);
        PERFORMER_MAP.put("instrument/english horn",            BM.P_PERFORMER_ENGLISH_HORN);
        PERFORMER_MAP.put("instrument/flugelhorn",              BM.P_PERFORMER_FLUGELHORN);
        PERFORMER_MAP.put("instrument/flute",                   BM.P_PERFORMER_FLUTE);
        PERFORMER_MAP.put("instrument/frame drum",              BM.P_PERFORMER_FRAME_DRUM);
        PERFORMER_MAP.put("instrument/french horn",             BM.P_PERFORMER_FRENCH_HORN);
        PERFORMER_MAP.put("instrument/glockenspiel",            BM.P_PERFORMER_GLOCKENSPIEL);
        PERFORMER_MAP.put("instrument/grand piano",             BM.P_PERFORMER_GRAND_PIANO);
        PERFORMER_MAP.put("instrument/guest",                   BM.P_PERFORMER_GUEST);
        PERFORMER_MAP.put("instrument/guitar",                  BM.P_PERFORMER_GUITAR);
        PERFORMER_MAP.put("instrument/guitar synthesizer",      BM.P_PERFORMER_GUITAR_SYNTHESIZER);
        PERFORMER_MAP.put("instrument/guitars",                 BM.P_PERFORMER_GUITARS);
        PERFORMER_MAP.put("instrument/handclaps",               BM.P_PERFORMER_HANDCLAPS);
        PERFORMER_MAP.put("instrument/hammond organ",           BM.P_PERFORMER_HAMMOND_ORGAN);
        PERFORMER_MAP.put("instrument/harmonica",               BM.P_PERFORMER_HARMONICA);
        PERFORMER_MAP.put("instrument/harp",                    BM.P_PERFORMER_HARP);
        PERFORMER_MAP.put("instrument/harpsichord",             BM.P_PERFORMER_HARPSICHORD);
        PERFORMER_MAP.put("instrument/hi-hat",                  BM.P_PERFORMER_HIHAT);
        PERFORMER_MAP.put("instrument/horn",                    BM.P_PERFORMER_HORN);
        PERFORMER_MAP.put("instrument/keyboard",                BM.P_PERFORMER_KEYBOARD);
        PERFORMER_MAP.put("instrument/koto",                    BM.P_PERFORMER_KOTO);
        PERFORMER_MAP.put("instrument/lute",                    BM.P_PERFORMER_LUTE);
        PERFORMER_MAP.put("instrument/maracas",                 BM.P_PERFORMER_MARACAS);
        PERFORMER_MAP.put("instrument/marimba",                 BM.P_PERFORMER_MARIMBA);
        PERFORMER_MAP.put("instrument/mellophone",              BM.P_PERFORMER_MELLOPHONE);
        PERFORMER_MAP.put("instrument/melodica",                BM.P_PERFORMER_MELODICA);
        PERFORMER_MAP.put("instrument/oboe",                    BM.P_PERFORMER_OBOE);
        PERFORMER_MAP.put("instrument/organ",                   BM.P_PERFORMER_ORGAN);
        PERFORMER_MAP.put("instrument/percussion",              BM.P_PERFORMER_PERCUSSION);
        PERFORMER_MAP.put("instrument/piano",                   BM.P_PERFORMER_PIANO);
        PERFORMER_MAP.put("instrument/piccolo trumpet",         BM.P_PERFORMER_PICCOLO_TRUMPET);
        PERFORMER_MAP.put("instrument/pipe organ",              BM.P_PERFORMER_PIPE_ORGAN);
        PERFORMER_MAP.put("instrument/psaltery",                BM.P_PERFORMER_PSALTERY);
        PERFORMER_MAP.put("instrument/recorder",                BM.P_PERFORMER_RECORDER);
        PERFORMER_MAP.put("instrument/rhodes piano",            BM.P_PERFORMER_RHODES_PIANO);
        PERFORMER_MAP.put("instrument/santur",                  BM.P_PERFORMER_SANTUR);
        PERFORMER_MAP.put("instrument/saxophone",               BM.P_PERFORMER_SAXOPHONE);
        PERFORMER_MAP.put("instrument/shakers",                 BM.P_PERFORMER_SHAKERS);
        PERFORMER_MAP.put("instrument/sitar",                   BM.P_PERFORMER_SITAR);
        PERFORMER_MAP.put("instrument/slide guitar",            BM.P_PERFORMER_SLIDE_GUITAR);
        PERFORMER_MAP.put("instrument/snare drum",              BM.P_PERFORMER_SNARE_DRUM);
        PERFORMER_MAP.put("instrument/solo",                    BM.P_PERFORMER_SOLO);
        PERFORMER_MAP.put("instrument/soprano saxophone",       BM.P_PERFORMER_SOPRANO_SAX);
        PERFORMER_MAP.put("instrument/spanish acoustic guitar", BM.P_PERFORMER_SPANISH_ACOUSTIC_GUITAR);
        PERFORMER_MAP.put("instrument/synclavier",              BM.P_PERFORMER_SYNCLAVIER);
        PERFORMER_MAP.put("instrument/synthesizer",             BM.P_PERFORMER_SYNTHESIZER);
        PERFORMER_MAP.put("instrument/tambourine",              BM.P_PERFORMER_TAMBOURINE);
        PERFORMER_MAP.put("instrument/tenor saxophone",         BM.P_PERFORMER_TENOR_SAX);
        PERFORMER_MAP.put("instrument/timpani",                 BM.P_PERFORMER_TIMPANI);
        PERFORMER_MAP.put("instrument/tiple",                   BM.P_PERFORMER_TIPLE);
        PERFORMER_MAP.put("instrument/trombone",                BM.P_PERFORMER_TROMBONE);
        PERFORMER_MAP.put("instrument/trumpet",                 BM.P_PERFORMER_TRUMPET);
        PERFORMER_MAP.put("instrument/tuba",                    BM.P_PERFORMER_TUBA);
        PERFORMER_MAP.put("instrument/tubular bells",           BM.P_PERFORMER_TUBULAR_BELLS);
        PERFORMER_MAP.put("instrument/tuned percussion",        BM.P_PERFORMER_TUNED_PERCUSSION);
        PERFORMER_MAP.put("instrument/ukulele",                 BM.P_PERFORMER_UKULELE);
        PERFORMER_MAP.put("instrument/vibraphone",              BM.P_PERFORMER_VIBRAPHONE);
        PERFORMER_MAP.put("instrument/viola",                   BM.P_PERFORMER_VIOLA);
        PERFORMER_MAP.put("instrument/viola da gamba",          BM.P_PERFORMER_VIOLA_DA_GAMBA);
        PERFORMER_MAP.put("instrument/violin",                  BM.P_PERFORMER_VIOLIN);
        PERFORMER_MAP.put("instrument/whistle",                 BM.P_PERFORMER_WHISTLE);
        PERFORMER_MAP.put("instrument/xylophone",               BM.P_PERFORMER_XYLOPHONE);
      }

    /*******************************************************************************************************************
     *
     * Aggregate of a {@link Release} and {@link Medium}.
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter
    static class ReleaseAndMedium
      {
        @Nonnull
        private final Release release;

        @Nonnull
        private final Medium medium;
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
            return relationList.getRelation().stream().map(relation -> new RelationAndTargetType(relation, relationList.getTargetType()));
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
    public Model handleMetadata (final @Nonnull Metadata metadata)
      throws InterruptedException, IOException
      {
        final ModelBuilder model = createModelBuilder();
        final Optional<String> albumTitle = metadata.get(TITLE);
        final Optional<Cddb> cddb = metadata.get(CDDB);

        if (albumTitle.isPresent() && !albumTitle.get().trim().isEmpty() && cddb.isPresent())
          {
            log.info("============ PROBING TOC FOR {}", albumTitle);
            final List<ReleaseAndMedium> rams = new ArrayList<>();
            final RestResponse<ReleaseList> releaseList = mbMetadataProvider.findReleaseListByToc(cddb.get().getToc(), TOC_INCLUDES);
            // even though we're querying by TOC, matching offsets is required to kill many false results
            releaseList.ifPresent(releases -> rams.addAll(findReleases(releases, cddb.get(), Validation.TRACK_OFFSETS_MATCH_REQUIRED)));

            if (rams.isEmpty())
              {
                log.info("============ PROBING METADATA FOR {}", albumTitle);
                final List<ReleaseGroup> releaseGroups = new ArrayList<>();
                releaseGroups.addAll(mbMetadataProvider.findReleaseGroupByTitle(albumTitle.get())
                                                       .map(ReleaseGroupList::getReleaseGroup)
                                                       .orElse(emptyList()));

                final Optional<String> cddbTitle = cddbTitleOf(metadata);
                cddbTitle.map(_f(mbMetadataProvider::findReleaseGroupByTitle)).ifPresent(response ->
                  {
                    log.info("======== ALSO USING ALTERNATE TITLE: {}", cddbTitle.get());
                    releaseGroups.addAll(response.get().getReleaseGroup());
                  });

                rams.addAll(findReleases(releaseGroups, cddb.get(), Validation.TRACK_OFFSETS_MATCH_REQUIRED));
              }

            model.with(rams.stream()
                           .parallel()
                           .map(_f(ram -> handleRelease(metadata, ram)))
                           .collect(toList()));
          }

        return model.toModel();
      }

    /*******************************************************************************************************************
     *
     * Extracts data from the given release.
     *
     * @param   metadata                the {@code Metadata}
     * @param   ram                     the release
     * @return                          the RDF triples
     * @throws  InterruptedException    in case of I/O error
     * @throws  IOException             in case of I/O error
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleRelease (final @Nonnull Metadata metadata,
                                        final @Nonnull ReleaseAndMedium ram)
      throws IOException, InterruptedException
      {
        final List<DefTrackData> tracks = ram.getMedium().getTrackList().getDefTrack();
        final Release release = ram.getRelease();
        final String recordTitle = release.getTitle();
        final IRI recordIri = musicBrainzIriFor("record", release.getId());
        return createModelBuilder()
            .with(recordIri, RDF.TYPE,           MO.C_RECORD)
            .with(recordIri, BM.P_IMPORTED_FROM, BM.O_MUSICBRAINZ)
            .with(recordIri, MO.P_MEDIA_TYPE,    MO.C_CD)
            .with(recordIri, RDFS.LABEL,         literalFor(recordTitle))
            .with(recordIri, DC.TITLE,           literalFor(recordTitle))
            .with(recordIri, MO.P_TRACK_COUNT,   literalFor(tracks.size()))
            .with(recordIri, MO.P_AMAZON_ASIN,   literalFor(Optional.ofNullable(release.getAsin())))
            .with(recordIri, MO.P_GTIN,          literalFor(Optional.ofNullable(release.getBarcode())))

            .with(tracks.stream().parallel()
                                 .map(_f(track -> handleTrack(metadata.get(CDDB).get(), recordIri, track)))
                                 .collect(toList()));

        // TODO: release.getLabelInfoList();
        // TODO: medium discId
        // TODO: record producer - requires inc=artist-rels
      }

    /*******************************************************************************************************************
     *
     * Extracts data from the given {@link DefTrackData}.
     *
     * @param   cddb                    the CDDB of the track we're handling
     * @param   track                   the track
     * @return                          the RDF triples
     * @throws  InterruptedException    in case of I/O error
     * @throws  IOException             in case of I/O error
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrack (final @Nonnull Cddb cddb,
                                      final @Nonnull IRI recordIri,
                                      final @Nonnull DefTrackData track)
      throws IOException, InterruptedException
      {
        final IRI trackIri = musicBrainzIriFor("track", track.getId());
        final int position = track.getPosition().intValue();
        final String recordingId = track.getRecording().getId();
//                final Recording recording = track.getRecording();
        final Recording recording = mbMetadataProvider.getResource(RECORDING, recordingId, RECORDING_INCLUDES).get();
        final String trackTitle = recording.getTitle();
//                    track.getRecording().getAliasList().getAlias().get(0).getSortName();
        log.info(">>>>>>>> {}. {}", position, trackTitle);
        final IRI signalIri    = signalIriFor(cddb, track.getPosition().intValue());

        return createModelBuilder()
            .with(recordIri, MO.P_TRACK,          trackIri)

            .with(signalIri, MO.P_PUBLISHED_AS,   trackIri)

            .with(trackIri,  RDF.TYPE,            MO.C_TRACK)
            .with(trackIri,  BM.P_IMPORTED_FROM,  BM.O_MUSICBRAINZ)
            .with(trackIri,  RDFS.LABEL,          literalFor(trackTitle))
            .with(trackIri,  DC.TITLE,            literalFor(trackTitle))
            .with(trackIri,  MO.P_TRACK_NUMBER,   literalFor(track.getPosition().intValue()))

            .with(handleTrackRelations(signalIri, recording));
//        bmmo:diskCount "1"^^xs:int ;
//        bmmo:diskNumber "1"^^xs:int ;
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
    private ModelBuilder handleTrackRelations (final @Nonnull IRI signalIri, final @Nonnull Recording recording)
      {
        return createModelBuilder().with(recording.getRelationList()
                                                  .stream()
                                                  .parallel()
                                                  .flatMap(RelationAndTargetType::toStream)
                                                  .map(ratt ->  handleTrackRelation(signalIri, recording, ratt))
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
        final IRI artistIri      = musicBrainzIriFor("artist", artist.getId());

        final ModelBuilder model = createModelBuilder()
            .with(performanceIri,  RDF.TYPE,            MO.C_PERFORMANCE)
            .with(performanceIri,  BM.P_IMPORTED_FROM,  BM.O_MUSICBRAINZ)
            .with(performanceIri,  MO.P_RECORDED_AS,    signalIri)

            .with(artistIri,       RDF.TYPE,            MO.C_MUSIC_ARTIST)
            .with(artistIri,       BM.P_IMPORTED_FROM,  BM.O_MUSICBRAINZ)
            .with(artistIri,       RDFS.LABEL,          literalFor(artist.getName()))
            .with(artistIri,       FOAF.NAME,           literalFor(artist.getName()));

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
    private Collection<ReleaseAndMedium> findReleases (final @Nonnull List<ReleaseGroup> releaseGroups,
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
     * @param   cddb            the track offsets
     * @param   validation      how the results must be validated
     * @return                  a collection of filtered {@code Medium}s
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<ReleaseAndMedium> findReleases (final @Nonnull ReleaseList releaseList,
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
                            .map(medium -> new ReleaseAndMedium(release, medium))))
            .filter(ram -> matchesFormat(ram.getMedium()))
            .filter(ram -> matchesTrackOffsets(ram.getMedium(), cddb, validation))
            .peek(ram -> log.info(">>>>>>>> FOUND {} - with score {}", ram.getMedium().getTitle(), 0 /* scoreOf(releaseGroup) FIXME */))
            // FIXME: should stop at the first found?
            .collect(toMap(ram -> ram.getRelease().getId(), ram -> ram, (u, v) -> v, TreeMap::new))
            .values();
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} if the given {@link Medium} is of a meaningful type (that is, a CD) or it's not set.
     *
     * @param   medium  the {@code Medium}
     * @return          {@code true} if there is a match
     *
     ******************************************************************************************************************/
    private static boolean matchesFormat (final @Nonnull Medium medium)
      {
        final String format = medium.getFormat();

        if ((format != null) && !"CD".equals(format))
          {
            log.info(">>>>>>>> discarded {} because not a CD ({})", medium.getTitle(), format);
            return false;
          }

        return true;
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} i the given {@link Medium} matches the track offsets in the given {@link Cddb}.
     *
     * @param   medium      the {@code Medium}
     * @param   cddb        the track offsets
     * @param   validation  how the results must be validated
     * @return              {@code true} if there is a match
     *
     ******************************************************************************************************************/
    private boolean matchesTrackOffsets (final @Nonnull Medium medium,
                                         final @Nonnull Cddb cddb,
                                         final @Nonnull Validation validation)
      {
        final List<Cddb> cddbs = cddbsOf(medium);

        if (cddbs.isEmpty() && (validation == Validation.TRACK_OFFSETS_MATCH_NOT_REQUIRED))
          {
            log.info(">>>>>>>> no track offsets, but not required");
            return true;
          }

        final boolean matches = cddbs.stream().anyMatch(c -> cddb.matches(c, trackOffsetsMatchThreshold));

        if (!matches)
          {
            synchronized (log) // keep log lines together
              {
                log.info(">>>>>>>> discarded {} because track offsets don't match", medium.getTitle());
                log.debug(">>>>>>>> iTunes offsets: {}", cddb.getTrackFrameOffsets());
                cddbs.forEach(c -> log.debug(">>>>>>>> found offsets:  {}", c.getTrackFrameOffsets()));
              }
          }

        return matches;
      }

    /*******************************************************************************************************************
     *
     * Returns the CDDB title extracted from the given {@link Metadata}.
     *
     * @param   metadata    the {@code Metadata}
     * @return              the title
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<String> cddbTitleOf (final @Nonnull Metadata metadata)
      throws IOException, InterruptedException
      {
        final RestResponse<CddbAlbum> album2 = cddbMetadataProvider.findCddbAlbum(metadata);

        if (!album2.isPresent())
          {
            return Optional.empty();
          }

        final CddbAlbum album = album2.get();
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
     ******************************************************************************************************************/
    @Nonnull
    private IRI signalIriFor (final @Nonnull Cddb cddb, final @Nonnegative int trackNumber)
      {
        return BM.signalIriFor(createSha1IdNew(cddb.getToc() + "/" + trackNumber));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static IRI performanceIriFor (final @Nonnull String id)
      {
        return FACTORY.createIRI(String.format("urn:bluemarine:performance:%s", id));
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
    private static String toString (final @Nonnull Attribute attribute)
      {
        return String.format("%s %s (%s)", attribute.getContent(), attribute.getCreditedAs(), attribute.getValue());
      }
  }