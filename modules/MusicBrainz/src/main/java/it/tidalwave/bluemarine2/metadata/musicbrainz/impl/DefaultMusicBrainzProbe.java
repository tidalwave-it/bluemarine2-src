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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import java.io.IOException;
import org.musicbrainz.ns.mmd_2.Medium;
import org.musicbrainz.ns.mmd_2.Release;
import org.musicbrainz.ns.mmd_2.ReleaseGroup;
import org.musicbrainz.ns.mmd_2.ReleaseGroupList;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.Cddb;
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
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.metadata.cddb.impl.MusicBrainzUtilities.cddbsOf;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;

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
    public List<ReleaseAndMedium> probe (final @Nonnull Metadata metadata)
      throws InterruptedException, IOException
      {
        final Optional<String> albumTitle = metadata.get(TITLE);
        final Optional<Cddb> cddb = metadata.get(CDDB);

        if (!albumTitle.isPresent() || albumTitle.get().trim().isEmpty() || !cddb.isPresent())
          {
            return Collections.emptyList();
          }

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

        return probe(releaseGroups, cddb.get());
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

        releaseGroups.stream().filter(releaseGroup -> score(releaseGroup) >= releaseGroupScoreThreshold)
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

                    log.info(">>>>>>>> FOUND {} - from score {}", medium.getTitle(), score(releaseGroup));
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
    private static int score (final @Nonnull ReleaseGroup releaseGroup)
      {
        return Integer.parseInt(releaseGroup.getOtherAttributes().get(QNAME_SCORE));
      }
  }
