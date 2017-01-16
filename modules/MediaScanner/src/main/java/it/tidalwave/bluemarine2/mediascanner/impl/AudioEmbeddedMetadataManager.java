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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import it.tidalwave.util.ConcurrentHashMapWithOptionals;
import it.tidalwave.util.Id;
import it.tidalwave.util.InstantProvider;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.vocabulary.BM;
import it.tidalwave.bluemarine2.model.vocabulary.MO;
import it.tidalwave.bluemarine2.model.vocabulary.DbTune;
import it.tidalwave.bluemarine2.model.vocabulary.Purl;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

/***********************************************************************************************************************
 *
 * FIXME: this doc must be updated
 *
 * mo:AudioFile
 *      IRI                                                     computed from the fingerprint
 *      foaf:sha1           the fingerprint of the file         locally computed
 *      bm:latestInd.Time   the latest import time              locally computed
 *      bm:path             the path of the file                locally computed
 *      dc:title            the title                           locally computed    WRONG: USELESS?
 *      rdfs:label          the display name                    locally computed    WRONG: should be the file name without path?
 *      mo:encodes          points to the signal                locally computed
 *
 * mo:DigitalSignal
 *      IRI                                                     computed from the fingerprint
 *      mo:bitsPerSample    the bits per sample                 locally extracted from the file
 *      mo:duration         the duration                        locally extracted from the file
 *      mo:sample_rate      the sample rate                     locally extracted from the file
 *      mo:published_as     points to the Track                 locally computed
 *      MISSING mo:channels
 *      MISSING? mo:time
 *      MISSING? mo:trmid
 *
 * mo:Track
 *      IRI                                                     the DbTune one if available, else computed from SHA1
 *      mo:musicbrainz      the MusicBrainz IRI                 locally extracted from the file
 *      dc:title            the title                           taken from DbTune
 *      rdfs:label          the display name                    taken from DbTune
 *      foaf:maker          points to the MusicArtist           taken from DbTune
 *      mo:track_number     the track number in the record      taken from DbTune
 *
 * mo:Record
 *      IRI                                                     taken from DbTune
 *      dc:date
 *      dc:language
 *      dc:title            the title                           taken from DbTune
 *      rdfs:label          the display name                    taken from DbTune
 *      mo:release          TODO points to the Label (EMI, etc...)
 *      mo:musicbrainz      the MusicBrainz IRI                 locally extracted from the file
 *      mo:track            points to the Tracks                taken from DbTune
 *      foaf:maker          points to the MusicArtist           taken from DbTune
 *      owl:sameAs          point to external resources         taken from DbTune
 *
 * mo:Artist
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class AudioEmbeddedMetadataManager
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Immutable @RequiredArgsConstructor @Getter @ToString
    static class Entry
      {
        @Nonnull
        private final IRI iri;

        @Nonnull
        private final String name;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Immutable @RequiredArgsConstructor @ToString
    static class Pair
      {
        @Nonnull
        private final IRI predicate;

        @Nonnull
        private final Value object;

        @Nonnull
        public Statement createStatementWithSubject (final @Nonnull IRI subjectIri)
          {
            return SimpleValueFactory.getInstance().createStatement(subjectIri, predicate, object);
          }
      }

    /*******************************************************************************************************************
     *
     * Facility that creates a request to add statements for the giving {@link Metadata} and {@code subjectURi}. It
     * maps metadata items to the proper statement predicate and literal.
     *
     ******************************************************************************************************************/
    static class Mapper extends HashMap<Key<?>, Function<Object, Pair>>
      {
        private static final long serialVersionUID = 9180433348240275721L;

        @Nonnull
        public List<Statement> statementsFor (final @Nonnull Metadata metadata, final @Nonnull IRI subjectIri)
          {
            return metadata.getEntries().stream()
                                        .filter(e -> containsKey(e.getKey()))
                                        .map(e -> forEntry(e).createStatementWithSubject(subjectIri))
                                        .collect(toList());
          }

        @Nonnull
        private Pair forEntry (final @Nonnull Map.Entry<Key<?>, ?> entry)
          {
            return get(entry.getKey()).apply(entry.getValue());
          }
      }

    @Inject
    private StatementManager statementManager;

    @Inject
    private IdCreator idCreator;

    @Inject
    private InstantProvider timestampProvider;

    @Inject
    private ProgressHandler progress;

    private static final Mapper SIGNAL_MAPPER = new Mapper();
    private static final Mapper TRACK_MAPPER = new Mapper();

    // Set would suffice, but there's no ConcurrentSet
    private final ConcurrentHashMapWithOptionals<IRI, Optional<String>> seenArtistUris =
            new ConcurrentHashMapWithOptionals<>();

    private final ConcurrentHashMapWithOptionals<IRI, Boolean> seenRecordUris = new ConcurrentHashMapWithOptionals<>();

    static
      {
        TRACK_MAPPER. put(Metadata.TRACK_NUMBER, v -> new Pair(MO.P_TRACK_NUMBER,    literalFor((int)v)));
        TRACK_MAPPER. put(Metadata.DISK_NUMBER,  v -> new Pair(BM.DISK_NUMBER,       literalFor((int)v)));
        TRACK_MAPPER. put(Metadata.DISK_COUNT,   v -> new Pair(BM.DISK_COUNT,        literalFor((int)v)));

        SIGNAL_MAPPER.put(Metadata.SAMPLE_RATE,  v -> new Pair(MO.P_SAMPLE_RATE,     literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.BIT_RATE,     v -> new Pair(MO.P_BITS_PER_SAMPLE, literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.DURATION,     v -> new Pair(MO.P_DURATION,
                                                            literalFor((float)((Duration)v).toMillis())));
      }

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
    /* VisibleForTesting */ void onMediaItemImportRequest (final @ListensTo @Nonnull MediaItemImportRequest request)
      throws InterruptedException
      {
        try
          {
            log.info("onMediaItemImportRequest({})", request);
            importMediaItem(request.getMediaItem());
          }
        finally
          {
            progress.incrementImportedMediaItems();
          }
      }

    /*******************************************************************************************************************
     *
     * Processes a {@link MediaItem}.
     *
     * @param                           audioFile   the item
     *
     ******************************************************************************************************************/
    private void importMediaItem (final @Nonnull MediaItem audioFile)
      {
        log.debug("importMediaItem({})", audioFile);
        final Id sha1 = idCreator.createSha1Id(audioFile.getPath());
        final Metadata metadata = audioFile.getMetadata();

        final IRI audioFileIri = BM.audioFileIriFor(sha1);
        final IRI signalIri = BM.signalIriFor(sha1);
        final IRI trackIri = createIriForEmbeddedTrack(metadata, sha1);

        statementManager.requestAddStatements()
            .with(audioFileIri,         RDF.TYPE,                MO.C_AUDIO_FILE)
            .with(audioFileIri,         FOAF.SHA1,               literalFor(sha1))
            .with(audioFileIri,         MO.P_ENCODES,            signalIri) // FIXME: this is path's SHA1, not contents'
            .with(audioFileIri,         BM.PATH,                 literalFor(audioFile.getRelativePath()))
            .with(audioFileIri,         BM.LATEST_INDEXING_TIME, literalFor(getLastModifiedTime(audioFile.getPath())))
            // TODO why optional? Isn't file size always available?
            .withOptional(audioFileIri, BM.FILE_SIZE,            metadata.get(FILE_SIZE).map(s -> literalFor(s)))

            .with(        trackIri,     RDF.TYPE,                MO.C_TRACK)
            .withOptional(trackIri,     BM.ITUNES_CDDB1,         literalFor(metadata.get(ITUNES_COMMENT)
                                                                                    .map(c -> c.getTrackId())))

            .with(signalIri,            RDF.TYPE,                MO.C_DIGITAL_SIGNAL)
            .with(signalIri,            MO.P_PUBLISHED_AS,       trackIri)
            .publish();

        importAudioFileMetadata(audioFile, signalIri, trackIri);
        importFallbackTrackMetadata(audioFile, trackIri);
      }

    /*******************************************************************************************************************
     *
     * Imports the metadata embedded in a file for the given {@link MediaItem}. It only processes the portion of
     * metadata which are never superseded by external catalogs (such as sample rate, duration, etc...).
     *
     * @param   mediaItem               the {@code MediaItem}.
     * @param   signalUri               the IRI of the signal
     * @param   trackUri                the IRI of the track
     *
     ******************************************************************************************************************/
    private void importAudioFileMetadata (final @Nonnull MediaItem mediaItem,
                                          final @Nonnull IRI signalUri,
                                          final @Nonnull IRI trackUri)
      {
        log.debug("importAudioFileMetadata({}, {}, {})", mediaItem, signalUri, trackUri);
        final Metadata metadata = mediaItem.getMetadata();
        statementManager.requestAdd(SIGNAL_MAPPER.statementsFor(metadata, signalUri));
        statementManager.requestAdd(TRACK_MAPPER.statementsFor(metadata, trackUri));
      }

    /*******************************************************************************************************************
     *
     * Imports all the  metadata embedded in a track for the given {@link MediaItem}.
     *
     * @param   mediaItem               the {@code MediaItem}.
     * @param   trackIri                the IRI of the track
     *
     ******************************************************************************************************************/
    private void importFallbackTrackMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull IRI trackIri)
      {
        log.debug("importFallbackTrackMetadata({}, {})", mediaItem, trackIri);

        final Metadata metadata           = mediaItem.getMetadata();
        log.debug(">>>> metadata of {}: {}", trackIri, metadata);

        final Optional<String> title      = metadata.get(TITLE);
        final Optional<String> makerName  = metadata.get(ARTIST);

        final List<IRI> makerUris = makerName.map(name -> asList(createIriForEmbeddedArtist(name))).orElse(emptyList());
        final List<Entry> artists = makerName.map(name -> Stream.of(name.split("[;]")).map(String::trim)).orElse(Stream.empty())
                           .map(name -> new Entry(createIriForEmbeddedArtist(name), name))
                           .collect(toList());

        final List<Entry> newArtists   = artists.stream().filter(
                e -> seenArtistUris.putIfAbsentAndGetNewKey(e.getIri(), Optional.empty()).isPresent())
                .collect(toList());
        final List<IRI> newArtistIris       = newArtists.stream().map(Entry::getIri).collect(toList());
        final List<Value> newArtistLiterals = newArtists.stream().map(e -> literalFor(e.getName())).collect(toList());

        final Optional<IRI> newGroupIri = (artists.size() <= 1) ? Optional.empty()
                : seenArtistUris.putIfAbsentAndGetNewKey(makerUris.get(0), Optional.empty()); // FIXME: only first one?

        final PathAwareEntity parent     = mediaItem.getParent().get();
        final String recordTitle         = metadata.get(ALBUM).orElse(parent.getPath().toFile().getName());
        final IRI recordIri              = createIriForEmbeddedRecord(recordTitle);
        final Optional<IRI> newRecordIri = seenRecordUris.putIfAbsentAndGetNewKey(recordIri, true);

        statementManager.requestAddStatements()
            .withOptional(trackIri,      RDFS.LABEL,                literalFor(title))
            .withOptional(trackIri,      DC.TITLE,                  literalFor(title))
            .with(        trackIri,      FOAF.MAKER,                makerUris.stream())

            .with(        recordIri,     MO.P_TRACK,                trackIri)
            .with(        recordIri,     FOAF.MAKER,                makerUris.stream())

            .withOptional(newRecordIri,  RDF.TYPE,                  MO.C_RECORD)
            .withOptional(newRecordIri,  MO.P_MEDIA_TYPE,           MO.C_CD)
            .withOptional(newRecordIri,  RDFS.LABEL,                literalFor(recordTitle))
            .withOptional(newRecordIri,  DC.TITLE,                  literalFor(recordTitle))
            .withOptional(newRecordIri,  BM.ITUNES_CDDB1,           literalFor(metadata.get(ITUNES_COMMENT)
                                                                                       .map(c -> c.getCddb1())))

            .with(        newArtistIris, RDF.TYPE,                  MO.C_MUSIC_ARTIST)
            .with(        newArtistIris, RDFS.LABEL,                newArtistLiterals)
            .with(        newArtistIris, FOAF.NAME,                 newArtistLiterals)

            .withOptional(newGroupIri,   RDF.TYPE,                  MO.C_MUSIC_ARTIST)
            .withOptional(newGroupIri,   RDFS.LABEL,                literalFor(makerName))
            .withOptional(newGroupIri,   FOAF.NAME,                 literalFor(makerName))
            .withOptional(newGroupIri,   DbTune.ARTIST_TYPE,        literalFor((short)2))
            .withOptional(newGroupIri,   Purl.COLLABORATES_WITH,    artists.stream().map(Entry::getIri))
            .publish();
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
    private IRI createIriForEmbeddedRecord (final @Nonnull String recordTitle)
      {
        return BM.localRecordIriFor(idCreator.createSha1("RECORD:" + recordTitle));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI createIriForEmbeddedTrack (final @Nonnull Metadata metadata, final @Nonnull Id sha1)
      {
        // FIXME: the same contents in different places will give the same sha1. Disambiguates by hashing the path too?
        return BM.localTrackIriFor(sha1);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI createIriForEmbeddedArtist (final @Nonnull String name)
      {
        return BM.localArtistIriFor(idCreator.createSha1("ARTIST:" + name));
      }
  }
