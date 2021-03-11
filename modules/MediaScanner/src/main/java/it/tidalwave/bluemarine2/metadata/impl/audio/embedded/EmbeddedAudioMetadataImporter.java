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
package it.tidalwave.bluemarine2.metadata.impl.audio.embedded;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import it.tidalwave.util.ConcurrentHashMapWithOptionals;
import it.tidalwave.util.Id;
import it.tidalwave.util.InstantProvider;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.util.ModelBuilder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.vocabulary.MO;
import it.tidalwave.bluemarine2.model.vocabulary.DbTune;
import it.tidalwave.bluemarine2.model.vocabulary.Purl;
import it.tidalwave.bluemarine2.model.vocabulary.BMMO;
import it.tidalwave.bluemarine2.mediascanner.impl.MediaItemImportRequest;
import it.tidalwave.bluemarine2.mediascanner.impl.ProgressHandler;
import it.tidalwave.bluemarine2.mediascanner.impl.StatementManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.util.Formatters.*;

/***********************************************************************************************************************
 *
 * This class generates RDF triples out of the {@link Metadata} embedded in an audio file.
 *
 * <pre>
 * mo:AudioFile
 *      IRI                     computed from the fingerprint of the contents
 *      bm:importedFrom         http://bluemarine.tidalwave.it/source#embedded
 *      rdfs:label              the display name
 *      dc:title                the title
 *      mo:encodes              points to the signal
 *      bm:latestIndexingTime   the latest import time
 *      bm:path                 the path of the file
 *      bm:fileSize             the file size
 *      foaf:sha1               the fingerprint of the file
 *
 * mo:DigitalSignal
 *      IRI                     computed from the fingerprint of related file
 *      bm:importedFrom         http://bluemarine.tidalwave.it/source#embedded
 *      mo:bitsPerSample        the bits per sample
 *      mo:duration             the duration
 *      mo:sample_rate          the sample rate
 *      mo:published_as         points to the Track
 *      MISSING mo:channels
 *      MISSING mo:time
 *      MISSING mo:trmid
 *
 * mo:Track
 *      IRI                     computed from the fingerprint of related file
 *      bm:importedFrom         http://bluemarine.tidalwave.it/source#embedded
 *      rdfs:label              the display name
 *      dc:title                the title
 *      mo:track_number         the track number in the record
 *      bm:discCount            the number of disks in a collection
 *      bm:discNumber           the index of the disk in a collection
 *      bm:iTunesCddb1          the CDDB1 attribute encoded by iTunes plus the track index
 *      foaf:maker              points to the MusicArtists
 *
 * mo:Record
 *      IRI                     computed from the fingerprint of the name
 *      bm:importedFrom         http://bluemarine.tidalwave.it/source#embedded
 *      rdfs:label              the display name (ALBUM from audiofile metadata, or the name of the folder)
 *      dc:title                the title (see above)
 *      mo:mediaType            CD
 *      mo:track                points to the Tracks
 *      bm:iTunesCddb1          the CDDB1 attribute encoded by iTunes
 *      foaf:maker              points to the MusicArtists (union of the makers of Tracks)
 *      MISSING dc:date
 *      MISSING dc:language
 *      MISSING mo:release      TODO points to the Label (EMI, etc...)
 *
 * mo:MusicArtist
 *      IRI                     computed from the fingerprint of the name
 *      bm:importedFrom         http://bluemarine.tidalwave.it/source#embedded
 *      rdfs:label              the display name
 *      foaf:name               the name
 *      (in case of a group also the predicates below)
 *      dbtune:artist_type      2, which means a group
 *      purl:collaborates_with  the MusicArtists in the group
 * </pre>
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class EmbeddedAudioMetadataImporter
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Immutable @RequiredArgsConstructor @Getter @ToString
    static class Pair
      {
        @Nonnull
        private final IRI iri;

        @Nonnull
        private final String name;
      }

    @Inject
    private StatementManager statementManager;

    @Inject
    private InstantProvider timestampProvider;

    @Inject
    private ProgressHandler progress;

    // Set would suffice, but there's no ConcurrentSet
    private final ConcurrentHashMapWithOptionals<IRI, Optional<String>> seenArtistUris =
            new ConcurrentHashMapWithOptionals<>();

    private final ConcurrentHashMapWithOptionals<IRI, Boolean> seenRecordUris = new ConcurrentHashMapWithOptionals<>();

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void reset()
      {
        // FIXME: should load existing URIs from the Persistence
        seenArtistUris.clear();
        seenRecordUris.clear();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onMediaItemImportRequest (final @ListensTo MediaItemImportRequest request)
      {
        request.getSha1().ifPresent(sha1 ->
          {
            try
              {
                log.info("onMediaItemImportRequest({})", request);
                statementManager.requestAdd(importMediaItem(request.getMediaItem(), sha1));
              }
            finally
              {
                progress.incrementImportedMediaItems();
              }
          });
      }

    /*******************************************************************************************************************
     *
     * Processes a {@link MediaItem}.
     *
     * @param   mediaItem   the item
     * @param   sha2        the fingerprint of the file
     * @return              the model
     *
     ******************************************************************************************************************/
    @Nonnull
    private Model importMediaItem (final @Nonnull MediaItem mediaItem, final @Nonnull byte[] sha1)
      {
        log.debug("importMediaItem({})", mediaItem);

        final Metadata metadata = mediaItem.getMetadata();

        final Optional<String> trackTitle  = metadata.get(TITLE);
        final Optional<String> makerName   = metadata.get(ARTIST);
        final PathAwareEntity parent       = mediaItem.getParent().get();
        final String recordTitle           = metadata.get(ALBUM).orElse(parent.getPath().toFile().getName());
        final Optional<Integer> diskCount  = emptyIfOne(metadata.get(DISK_COUNT));
        final Optional<Integer> diskNumber = diskCount.flatMap(dc -> metadata.get(DISK_NUMBER));
        final Id uniqueId                  = uniqueTrackId(metadata, toBase64String(sha1));
        final IRI audioFileIri             = BMMO.audioFileIriFor(toBase64String(sha1));
        final IRI signalIri                = BMMO.signalIriFor(uniqueId);
        final IRI trackIri                 = BMMO.trackIriFor(uniqueId);
        final IRI recordIri                = recordIriOf(metadata, recordTitle);
        final Optional<IRI> newRecordIri   = seenRecordUris.putIfAbsentAndGetNewKey(recordIri, true);

        final List<IRI> makerUris = makerName.map(name -> asList(artistIriOf(name))).orElse(emptyList());
        final List<Pair> artists = makerName.map(name -> Stream.of(name.split("[;]")).map(String::trim)).orElse(Stream.empty())
                           .map(name -> new Pair(artistIriOf(name), name))
                           .collect(toList());

        final List<Pair> newArtists   = artists.stream().filter(
                p -> seenArtistUris.putIfAbsentAndGetNewKey(p.getIri(), Optional.empty()).isPresent())
                .collect(toList());
        final List<IRI> newArtistIris       = newArtists.stream().map(Pair::getIri).collect(toList());
        final List<Value> newArtistLiterals = newArtists.stream().map(p -> literalFor(p.getName())).collect(toList());

        final Optional<IRI> newGroupIri = (artists.size() <= 1) ? Optional.empty()
                : seenArtistUris.putIfAbsentAndGetNewKey(makerUris.get(0), Optional.empty()); // FIXME: only first one?

        return new ModelBuilder()
            .with(        audioFileIri,  RDF.TYPE,                    MO.C_AUDIO_FILE)
            .with(        audioFileIri,  BMMO.P_IMPORTED_FROM,        BMMO.O_SOURCE_EMBEDDED)
            .with(        audioFileIri,  FOAF.SHA1,                   literalFor(toHexString(sha1)))
            .with(        audioFileIri,  MO.P_ENCODES,                signalIri)
            .with(        audioFileIri,  BMMO.P_PATH,                 literalFor(mediaItem.getRelativePath()))
            .with(        audioFileIri,  BMMO.P_LATEST_INDEXING_TIME, literalFor(getLastModifiedTime(mediaItem.getPath())))
            .withOptional(audioFileIri,  BMMO.P_FILE_SIZE,            literalForLong(metadata.get(FILE_SIZE)))

            .with(        signalIri,     RDF.TYPE,                    MO.C_DIGITAL_SIGNAL)
            .with(        signalIri,     BMMO.P_IMPORTED_FROM,        BMMO.O_SOURCE_EMBEDDED)
            .with(        signalIri,     MO.P_PUBLISHED_AS,           trackIri)
            .withOptional(signalIri,     MO.P_SAMPLE_RATE,            literalForInt(metadata.get(SAMPLE_RATE)))
            .withOptional(signalIri,     MO.P_BITS_PER_SAMPLE,        literalForInt(metadata.get(BIT_RATE)))
            .withOptional(signalIri,     MO.P_DURATION,               literalForFloat(metadata.get(DURATION)
                                                                                              .map(Duration::toMillis)
                                                                                              .map(l -> (float)l)))
            .with(        trackIri,      RDF.TYPE,                    MO.C_TRACK)
            .with(        trackIri,      BMMO.P_IMPORTED_FROM,        BMMO.O_SOURCE_EMBEDDED)
            .withOptional(trackIri,      BMMO.P_ITUNES_CDDB1,         literalFor(metadata.get(ITUNES_COMMENT)
                                                                                       .map(c -> c.getTrackId())))
            .withOptional(trackIri,      MO.P_TRACK_NUMBER,           literalForInt(metadata.get(TRACK_NUMBER)))
            .withOptional(trackIri,      RDFS.LABEL,                  literalFor(trackTitle))
            .withOptional(trackIri,      DC.TITLE,                    literalFor(trackTitle))
            .with(        trackIri,      FOAF.MAKER,                  makerUris.stream())

            .withOptional(newRecordIri,  RDF.TYPE,                    MO.C_RECORD)
            .withOptional(newRecordIri,  BMMO.P_IMPORTED_FROM,        BMMO.O_SOURCE_EMBEDDED)
            .withOptional(newRecordIri,  MO.P_MEDIA_TYPE,             MO.C_CD)
            .withOptional(newRecordIri,  RDFS.LABEL,                  literalFor(recordTitle))
            .withOptional(newRecordIri,  DC.TITLE,                    literalFor(recordTitle))
            .withOptional(newRecordIri,  MO.P_TRACK_COUNT,            literalForInt(metadata.get(CDDB)
                                                                                            .map(cddb -> cddb.getTrackCount())))
            .withOptional(newRecordIri,  BMMO.P_DISK_NUMBER,          literalForInt(diskNumber))
            .withOptional(newRecordIri,  BMMO.P_DISK_COUNT,           literalForInt(diskCount))
            .withOptional(newRecordIri,  BMMO.P_ITUNES_CDDB1,         literalFor(metadata.get(ITUNES_COMMENT)
                                                                                         .map(c -> c.getCddb1())))
            .with(        recordIri,     MO.P_TRACK,                  trackIri)
            .with(        recordIri,     FOAF.MAKER,                  makerUris.stream())

            .with(        newArtistIris, RDF.TYPE,                    MO.C_MUSIC_ARTIST)
            .with(        newArtistIris, BMMO.P_IMPORTED_FROM,        BMMO.O_SOURCE_EMBEDDED)
            .with(        newArtistIris, RDFS.LABEL,                  newArtistLiterals)
            .with(        newArtistIris, FOAF.NAME,                   newArtistLiterals)

            .withOptional(newGroupIri,   RDF.TYPE,                    MO.C_MUSIC_ARTIST)
            .withOptional(newGroupIri,   BMMO.P_IMPORTED_FROM,        BMMO.O_SOURCE_EMBEDDED)
            .withOptional(newGroupIri,   RDFS.LABEL,                  literalFor(makerName))
            .withOptional(newGroupIri,   FOAF.NAME,                   literalFor(makerName))
            .withOptional(newGroupIri,   DbTune.P_ARTIST_TYPE,        literalFor((short)2))
            .withOptional(newGroupIri,   Purl.P_COLLABORATES_WITH,    artists.stream().map(Pair::getIri))
            .toModel();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Instant getLastModifiedTime (final @Nonnull Path path)
      {
        try
          {
            return Files.getLastModifiedTime(path).toInstant();
          }
        catch (IOException e) // should never happen
          {
            log.warn("Cannot get last modified time for {}: assuming now", path);
            return timestampProvider.getInstant();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static IRI recordIriOf (final @Nonnull Metadata metadata, final @Nonnull String recordTitle)
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
    private Id uniqueTrackId (final @Nonnull Metadata metadata, final @Nonnull String default_)
      {
        final Optional<Cddb> cddb = metadata.get(CDDB);
        final Optional<Integer> trackNumber = metadata.get(TRACK_NUMBER);

        return (cddb.isPresent() && trackNumber.isPresent())
                ? createSha1IdNew(cddb.get().getToc() + "/" + trackNumber.get())
                : new Id(default_);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI artistIriOf (final @Nonnull String name)
      {
        return BMMO.artistIriFor(createSha1IdNew("ARTIST:" + name));
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
  }
