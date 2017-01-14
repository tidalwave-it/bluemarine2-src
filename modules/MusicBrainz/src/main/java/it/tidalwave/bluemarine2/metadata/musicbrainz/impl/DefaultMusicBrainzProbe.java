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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import java.io.IOException;
import org.musicbrainz.ns.mmd_2.Medium;
import org.musicbrainz.ns.mmd_2.DefTrackData;
import org.musicbrainz.ns.mmd_2.Recording;
import org.musicbrainz.ns.mmd_2.Release;
import org.musicbrainz.ns.mmd_2.ReleaseGroup;
import org.musicbrainz.ns.mmd_2.ReleaseGroupList;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.eclipse.rdf4j.model.impl.TreeModel;
import it.tidalwave.util.Id;
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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.model.vocabulary.BM.musicBrainzIriFor;
import static it.tidalwave.bluemarine2.metadata.cddb.impl.MusicBrainzUtilities.cddbsOf;

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
        final Model model = new TreeModel();
        final Optional<String> albumTitle = metadata.get(TITLE);
        final Optional<Cddb> cddb = metadata.get(CDDB);

        if (albumTitle.isPresent() && !albumTitle.get().trim().isEmpty() && cddb.isPresent())
          {
            log.info("============ PROBING METADATA FOR {}", albumTitle);
            final List<ReleaseGroup> releaseGroups = new ArrayList<>();
            releaseGroups.addAll(mbMetadataProvider.findReleaseGroup(albumTitle.get())
                                                   .map(ReleaseGroupList::getReleaseGroup)
                                                   .orElse(emptyList()));

            final Optional<String> cddbTitle = cddbTitle(metadata);
            cddbTitle.map(_f(mbMetadataProvider::findReleaseGroup)).ifPresent(response ->
              {
                log.info("======== ALSO USING ALTERNATE TITLE: {}", cddbTitle.get());
                releaseGroups.addAll(response.get().getReleaseGroup());
              });

            // What about this? http://musicbrainz.org//ws/2/discid/-?toc=1+12+267257+150+22767+41887+58317+72102+91375+104652+115380+132165+143932+159870+174597
            final List<ReleaseAndMedium> rams = probe(releaseGroups, cddb.get());
            toModel(rams).stream().forEach(statement -> model.add(statement));
            // TODO: also accumulate to a global repository
            // TODO: authors etc go to the global repository
          }

        return model;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Model toModel (final @Nonnull List<ReleaseAndMedium> rams)
      {
        final Model model = new TreeModel();

        // TODO: how to manage multiple rams
        for (final ReleaseAndMedium ram : rams)
          {
            final List<DefTrackData> tracks = ram.getMedium().getTrackList().getDefTrack();
            final Release release = ram.getRelease();
            final String recordTitle = release.getTitle();

            final IRI recordIri = musicBrainzIriFor("record", new Id(release.getId()));
            model.add(recordIri, RDF.TYPE,          MO.C_RECORD);
            model.add(recordIri, MO.P_MEDIA_TYPE,   MO.C_CD);
            model.add(recordIri, RDFS.LABEL,        literalFor(recordTitle));
            model.add(recordIri, DC.TITLE,          literalFor(recordTitle));
            model.add(recordIri, MO.P_TRACK_COUNT,  literalFor(tracks.size()));
            // TODO: medium discId
            // TODO: <barcode>093624763222</barcode>
            // TODO: <asin>B000046S1F</asin>
            // TODO: producer - requires inc=artist-rels
            log.info(">>>> TRACKS");

            for (final DefTrackData track : tracks)
              {
                final IRI trackIri = musicBrainzIriFor("track", new Id(track.getId()));
                final int position = track.getPosition().intValue();
                final Recording recording = track.getRecording();
                final String trackTitle = recording.getTitle();
//                    track.getRecording().getTitle();
//                    track.getRecording().getAliasList().getAlias().get(0).getSortName();
                log.info(">>>>>>>> {}. {}", position, trackTitle);

                model.add(trackIri,  RDF.TYPE,           MO.C_TRACK);
                model.add(trackIri,  RDFS.LABEL,         literalFor(trackTitle));
                model.add(trackIri,  DC.TITLE,           literalFor(trackTitle));
                model.add(trackIri,  MO.P_TRACK_NUMBER,  literalFor(track.getPosition().intValue()));
//        bmmo:diskCount "1"^^xs:int ;
//        bmmo:diskNumber "1"^^xs:int ;
                model.add(recordIri, MO.P_TRACK,         trackIri);
              }
          }

        return model;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private List<ReleaseAndMedium> probe (final @Nonnull List<ReleaseGroup> releaseGroups, final @Nonnull Cddb cddb)
      throws IOException, InterruptedException
      {
        final Map<String, ReleaseAndMedium> found = new TreeMap<>();

        releaseGroups.stream().filter(releaseGroup -> scoreOf(releaseGroup) >= releaseGroupScoreThreshold)
                              .forEach(releaseGroup ->
          {
            log.debug(">>>> {} {} {} artist: {}",
                    releaseGroup.getOtherAttributes().get(QNAME_SCORE),
                    releaseGroup.getId(),
                    releaseGroup.getTitle(),
                    releaseGroup.getArtistCredit().getNameCredit().stream().map(nc -> nc.getArtist().getName()).collect(toList()));

            releaseGroup.getReleaseList().getRelease().forEach(_c(release ->
              {
                log.info(">>>>>>>> release: {} {}", release.getId(), release.getTitle());
                final Release release2 = mbMetadataProvider.findRelease(release.getId()).get();

                for (final Medium medium : release2.getMediumList().getMedium())
                  {
                    if (!"CD".equals(medium.getFormat()))
                      {
                        log.info(">>>>>>>> discarded {} because not a CD ({})", medium.getTitle(), medium.getFormat());
                        continue;
                      }

                    final List<Cddb> cddbs = cddbsOf(medium);
                    final boolean matches = cddbs.stream().anyMatch(c -> cddb.matches(c, trackOffsetsMatchThreshold));

//                    if (!cddbs.isEmpty() && !matches)
                    if (!matches)
                      {
                        log.info(">>>>>>>> discarded {} because track offsets don't match", medium.getTitle());
                        log.debug(">>>>>>>> iTunes offsets: {}", cddb.getTrackFrameOffsets());
                        cddbs.forEach(c -> log.debug(">>>>>>>> found offsets:  {}", c.getTrackFrameOffsets()));
                        continue;
                      }

                    log.info(">>>>>>>> FOUND {} - from score {}", medium.getTitle(), scoreOf(releaseGroup));
                    found.put(release.getId(), new ReleaseAndMedium(release, medium));
                    // FIXME: should break, not only this loop, but also the one on releaseGroup
                  }
              }));
          });

        return new ArrayList<>(found.values());
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
    private static int scoreOf (final @Nonnull ReleaseGroup releaseGroup)
      {
        return Integer.parseInt(releaseGroup.getOtherAttributes().get(QNAME_SCORE));
      }
  }
