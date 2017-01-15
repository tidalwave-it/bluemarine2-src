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
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
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
import org.musicbrainz.ns.mmd_2.Target;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
@RequiredArgsConstructor
public class DefaultMusicBrainzProbe
  {
    private static final QName QNAME_SCORE = new QName("http://musicbrainz.org/ns/ext#-2.0", "score");

    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

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

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    static
      {
//instrument [alto saxophone
//instrument [bass clarinet
//instrument [bass
//instrument [cello
//instrument [clarinet
//instrument [double bass
//instrument [drums
//instrument [guitar
//instrument [harp
//instrument [harpsichord
//instrument [horn
//instrument [piano
//instrument [tenor saxophone
//instrument [trombone
//instrument [trumpet
//instrument [trumpet
//mo:orchestra
//mo:symphony_orchestra
//mo:chamber_orchestra
        PERFORMER_MAP.put("arranger", MO.P_ARRANGER);
        PERFORMER_MAP.put("balance", MO.P_BALANCE);
        PERFORMER_MAP.put("chorus master", MO.P_CONDUCTOR);
        PERFORMER_MAP.put("conductor", MO.P_CONDUCTOR);
        PERFORMER_MAP.put("engineer", MO.P_ENGINEER);
        PERFORMER_MAP.put("instrument", MO.P_PERFORMER); // FIXME: doesn't map the instrument
        PERFORMER_MAP.put("instrument arranger", MO.P_ARRANGER);
        PERFORMER_MAP.put("mastering", MO.P_MIX);
        PERFORMER_MAP.put("mix", MO.P_MIX);
        PERFORMER_MAP.put("remixer", MO.P_MIX);
        PERFORMER_MAP.put("orchestrator", MO.P_ORCHESTRATOR);
        PERFORMER_MAP.put("performer", MO.P_PERFORMER);
        PERFORMER_MAP.put("performing orchestra", MO.P_PERFORMER);
        PERFORMER_MAP.put("producer", MO.P_PRODUCER);
        PERFORMER_MAP.put("programming", MO.P_PROGRAMMING);
        PERFORMER_MAP.put("recording", MO.P_RECORDING);
        PERFORMER_MAP.put("sound", MO.P_ENGINEER);
        PERFORMER_MAP.put("vocal", MO.P_SINGER);
        PERFORMER_MAP.put("vocal/additional", MO.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/background vocals", MO.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/choir vocals", MO.P_CHOIR);
        PERFORMER_MAP.put("vocal/guest", MO.P_SINGER);
        PERFORMER_MAP.put("vocal/lead vocals", MO.P_LEAD_SINGER);
        PERFORMER_MAP.put("vocal/mezzo-soprano vocals", MO.P_MEZZO_SOPRANO);
        PERFORMER_MAP.put("vocal/other vocals", MO.P_BACKGROUND_SINGER);
        PERFORMER_MAP.put("vocal/solo", MO.P_LEAD_SINGER);
        PERFORMER_MAP.put("vocal/soprano vocals", MO.P_SOPRANO);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter
    public static class ReleaseAndMedium
      {
        @Nonnull
        private final Release release;

        @Nonnull
        private final Medium medium;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Model probe (final @Nonnull Metadata metadata)
      throws InterruptedException, IOException
      {
        final ModelBuilder model = createModelBuilder();
        final Optional<String> albumTitle = metadata.get(TITLE);
        final Optional<Cddb> cddb = metadata.get(CDDB);

        if (albumTitle.isPresent() && !albumTitle.get().trim().isEmpty() && cddb.isPresent())
          {
            log.info("============ PROBING METADATA FOR {}", albumTitle);
            final List<ReleaseGroup> releaseGroups = new ArrayList<>();
            releaseGroups.addAll(mbMetadataProvider.findReleaseGroupByTitle(albumTitle.get())
                                                   .map(ReleaseGroupList::getReleaseGroup)
                                                   .orElse(emptyList()));

            final Optional<String> cddbTitle = cddbTitle(metadata);
            cddbTitle.map(_f(mbMetadataProvider::findReleaseGroupByTitle)).ifPresent(response ->
              {
                log.info("======== ALSO USING ALTERNATE TITLE: {}", cddbTitle.get());
                releaseGroups.addAll(response.get().getReleaseGroup());
              });

            // What about this? http://musicbrainz.org//ws/2/discid/-?toc=1+12+267257+150+22767+41887+58317+72102+91375+104652+115380+132165+143932+159870+174597
            model.merge(findReleases(releaseGroups, cddb.get()).stream()
                                                               .parallel()
                                                               .map(_f(this::handleRelease))
                                                               .collect(toList()));
          }

        return model.toModel();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleRelease (final @Nonnull ReleaseAndMedium ram)
      throws IOException, InterruptedException
      {
        final List<DefTrackData> tracks = ram.getMedium().getTrackList().getDefTrack();
        final Release release = ram.getRelease();
        final String recordTitle = release.getTitle();
        final IRI recordIri = musicBrainzIriFor("record", release.getId());
        return createModelBuilder()
            .with(recordIri, RDF.TYPE,         MO.C_RECORD)
            .with(recordIri, MO.P_MEDIA_TYPE,  MO.C_CD)
            .with(recordIri, RDFS.LABEL,       literalFor(recordTitle))
            .with(recordIri, DC.TITLE,         literalFor(recordTitle))
            .with(recordIri, MO.P_TRACK_COUNT, literalFor(tracks.size()))
            .merge(tracks.stream().parallel()
                                  .map(_f(track -> handleTrack(recordIri, track)))
                                  .collect(toList()));
        // TODO: medium discId
        // TODO: <barcode>093624763222</barcode>
        // TODO: <asin>B000046S1F</asin>
        // TODO: record producer - requires inc=artist-rels
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrack (final @Nonnull IRI recordIri, final @Nonnull DefTrackData track)
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

//                http://musicbrainz.org/ws/2/recording/7e5766ea-c4a7-4091-a915-74208710d409?inc=aliases%2Bartist-credits%2Breleases%2bartist-rels
        return createModelBuilder()
            .with(recordIri, MO.P_TRACK,         trackIri)
            .with(trackIri,  RDF.TYPE,           MO.C_TRACK)
            .with(trackIri,  RDFS.LABEL,         literalFor(trackTitle))
            .with(trackIri,  DC.TITLE,           literalFor(trackTitle))
            .with(trackIri,  MO.P_TRACK_NUMBER,  literalFor(track.getPosition().intValue()))
            .merge(handleTrackRelations(recording, trackIri));
//        bmmo:diskCount "1"^^xs:int ;
//        bmmo:diskNumber "1"^^xs:int ;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrackRelations (final @Nonnull Recording recording, final @Nonnull IRI trackIri)
      {
        final ModelBuilder model = createModelBuilder();

        for (final RelationList relationList : recording.getRelationList())
          {
            final String targetType = relationList.getTargetType();
            model.merge(relationList.getRelation()
                                    .stream()
                                    .parallel()
                                    .map(relation ->  handleTrackRelation(targetType, trackIri, recording, relation))
                                    .collect(toList()));
          }

        return model;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ModelBuilder handleTrackRelation (final @Nonnull String targetType,
                                              final @Nonnull IRI trackIri,
                                              final @Nonnull Recording recording,
                                              final @Nonnull Relation relation)
      {
        final List<Attribute> attributes = getAttributes(relation);
        final Target target = relation.getTarget();
        final String type   = relation.getType();
        final Artist artist = relation.getArtist();
        final IRI performanceIri = musicBrainzIriFor("performance", recording.getId()); // FIXME: MB namespace?
        final IRI artistIri      = musicBrainzIriFor("artist", artist.getId());
        final ModelBuilder model = createModelBuilder()
            .with(performanceIri,  RDF.TYPE,         MO.C_PERFORMANCE)
            .with(performanceIri,  MO.P_RECORDED_AS, trackIri) // FIXME: Signal, not Track
            .with(artistIri,       RDF.TYPE,         MO.C_MUSIC_ARTIST)
            .with(artistIri,       RDFS.LABEL,       literalFor(artist.getName()))
            .with(artistIri,       FOAF.NAME,        literalFor(artist.getName()));

        log.info(">>>>>>>>>>>> {} {} {} {} ({})", targetType,
                                                  type,
                                                  attributes.stream().map(a -> toString(a)).collect(toList()),
                                                  artist.getName(),
                                                  artist.getId());
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

                if (type.equals("vocal"))
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
     * @return                  a collection of filtered {@code Medium}s
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<ReleaseAndMedium> findReleases (final @Nonnull List<ReleaseGroup> releaseGroups,
                                                       final @Nonnull Cddb cddb)
      {
        return releaseGroups.stream()
            .parallel()
            .filter(releaseGroup -> scoreOf(releaseGroup) >= releaseGroupScoreThreshold)
            .peek(releaseGroup -> logArtists(releaseGroup))
            .flatMap(releaseGroup -> releaseGroup.getReleaseList().getRelease().stream())
            .peek(release -> log.info(">>>>>>>> release: {} {}", release.getId(), release.getTitle()))
            .flatMap(_f(release -> mbMetadataProvider.getResource(RELEASE, release.getId(), RELEASE_INCLUDES).get()
                            .getMediumList().getMedium()
                            .stream()
                            .map(medium -> new ReleaseAndMedium(release, medium))))
            .filter(ram -> matchesFormat(ram.getMedium()))
            .filter(ram -> matchesTrackOffsets(ram.getMedium(), cddb))
            .peek(ram -> log.info(">>>>>>>> FOUND {} - with score {}", ram.getMedium().getTitle(), 0 /* scoreOf(releaseGroup) FIXME */))
            // FIXME: should stop at the first found?
            .collect(toMap(ram -> ram.getRelease().getId(), ram -> ram, (u, v) -> v, TreeMap::new))
            .values();
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
    private static boolean matchesFormat (final @Nonnull Medium medium)
      {
        if (!"CD".equals(medium.getFormat()))
          {
            log.info(">>>>>>>> discarded {} because not a CD ({})", medium.getTitle(), medium.getFormat());
            return false;
          }

        return true;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private boolean matchesTrackOffsets (final @Nonnull Medium medium, final @Nonnull Cddb cddb)
      {
        final List<Cddb> cddbs = cddbsOf(medium);
        final boolean matches = cddbs.stream().anyMatch(c -> cddb.matches(c, trackOffsetsMatchThreshold));

//                    if (!cddbs.isEmpty() && !matches)
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
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Optional<String> cddbTitle (final @Nonnull Metadata metadata)
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
            log.info(">>>> discarded alternate title because mismatching track offsets: {}", dTitle);
            log.debug(">>>>>>>> found track offsets:    {}", albumCddb.getTrackFrameOffsets());
            log.debug(">>>>>>>> searched track offsets: {}", requestedCddb.getTrackFrameOffsets());
            log.debug(">>>>>>>> ppm                     {}", albumCddb.computeDifference(requestedCddb));
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
    private static IRI predicateFor (final @Nonnull String role)
      {
        return Objects.requireNonNull(PERFORMER_MAP.get(role), "Cannot map role: " + role);
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
    @Nonnull
    public static IRI musicBrainzIriFor (final @Nonnull String resourceType, final @Nonnull String id)
      {
        return FACTORY.createIRI(String.format("http://musicbrainz.org/%s/%s", resourceType, id));
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
