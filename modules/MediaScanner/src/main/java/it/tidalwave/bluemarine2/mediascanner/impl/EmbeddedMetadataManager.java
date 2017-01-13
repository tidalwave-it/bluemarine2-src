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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import it.tidalwave.util.Key;
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
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.ITUNES_COMMENT;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class EmbeddedMetadataManager
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter @ToString
    static class Entry
      {
        @Nonnull
        private final IRI uri;

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
        public Statement createStatementWithSubject (final @Nonnull IRI subject)
          {
            return SimpleValueFactory.getInstance().createStatement(subject, predicate, object);
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
        public List<Statement> statementsFor (final @Nonnull Metadata metadata, final @Nonnull IRI subjectUri)
          {
            return metadata.getEntries().stream()
                                        .filter(e -> containsKey(e.getKey()))
                                        .map(e -> forEntry(e).createStatementWithSubject(subjectUri))
                                        .collect(toList());
          }

        @Nonnull
        private Pair forEntry (final @Nonnull Map.Entry<Key<?>, ?> entry)
          {
            return get(entry.getKey()).apply(entry.getValue());
          }
      }

    @Inject
    private DbTuneMetadataManager dbTuneMetadataManager;

    @Inject
    private StatementManager statementManager;

    @Inject
    private IdCreator idCreator;

    @Inject
    private Shared shared;

    private static final Mapper SIGNAL_MAPPER = new Mapper();
    private static final Mapper TRACK_MAPPER = new Mapper();

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
     * Imports the metadata embedded in a file for the given {@link MediaItem}. It only processes the portion of
     * metadata which are never superseded by external catalogs (such as sample rate, duration, etc...).
     *
     * @param   mediaItem               the {@code MediaItem}.
     * @param   signalUri               the IRI of the signal
     * @param   trackUri                the IRI of the track
     *
     ******************************************************************************************************************/
    public void importAudioFileMetadata (final @Nonnull MediaItem mediaItem,
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
     * Imports all the remaining metadata embedded in a track for the given {@link MediaItem}. This method is called
     * when we failed to match a track to an external catalog.
     *
     * @param   mediaItem               the {@code MediaItem}.
     * @param   trackUri                the IRI of the track
     *
     ******************************************************************************************************************/
    public void importFallbackTrackMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull IRI trackUri)
      {
        // TODO: consider the MBZ COMPOSER.
        log.debug("importFallbackTrackMetadata({}, {})", mediaItem, trackUri);

        final Metadata metadata           = mediaItem.getMetadata();
        log.debug(">>>> metadata of {}: {}", trackUri, metadata);

        final Optional<String> title      = metadata.get(Metadata.TITLE);
        final Optional<String> makerName  = metadata.get(Metadata.ARTIST);

        List<IRI> makerUris               = null;
        List<Entry> artists  = metadata.getAll(Metadata.MBZ_ARTIST_ID).stream()
                .map(id -> new Entry(BM.musicBrainzIriFor("artist", id), makerName.orElse("???")))
                .collect(toList());
        //
        // Even though we're in fallback mode, we could have a MusicBrainz artist id. Actually, fallback mode can be
        // triggered by any error while retrieving the track resource; it not implies a problem with the artist
        // resource. That's why it makes sense to try to retrieve an artist resource here.
        //
        // FIXME: Still missing:
        //      Anonyme Grèce
        //      Anonymus (País Vasco)
        //      Berliner Philharmoniker & Rafael Kubelík
        //      etc...
        //
        if (!artists.isEmpty())
          {
            makerUris = artists.stream().map(Entry::getUri).collect(toList());
            makerUris.forEach(uri -> dbTuneMetadataManager.requestArtistMetadata(uri, makerName)); // FIXME: all with the same maker name?
          }
        else  // no MusicBrainz artist
          {
            makerUris =  makerName.map(name -> asList(createIRIForLocalArtist(name))).orElse(emptyList());
            artists = makerName.map(name -> Stream.of(name.split("[;]")).map(String::trim)).orElse(Stream.empty())
                               .map(name -> new Entry(createIRIForLocalArtist(name), name))
                               .collect(toList());
          }

        final List<Entry> newArtists   = artists.stream().filter(
                e -> shared.seenArtistUris.putIfAbsentAndGetNewKey(e.getUri(), Optional.empty()).isPresent())
                .collect(toList());
        final List<IRI> newArtistUris       = newArtists.stream().map(Entry::getUri).collect(toList());
        final List<Value> newArtistLiterals = newArtists.stream().map(e -> literalFor(e.getName())).collect(toList());

        final Optional<IRI> newGroupUri = (artists.size() <= 1) ? Optional.empty()
                : shared.seenArtistUris.putIfAbsentAndGetNewKey(makerUris.get(0), Optional.empty()); // FIXME: only first one?

        final PathAwareEntity parent = mediaItem.getParent().map(p -> p).orElseThrow(() -> new RuntimeException());
        final String recordTitle     = metadata.get(Metadata.ALBUM)
                                               .orElse(parent.getPath().toFile().getName());
//                                             .orElse(parent.as(Displayable).getDisplayName());

        final IRI recordUri              = createIRIForLocalRecord(recordTitle);
        final Optional<IRI> newRecordUri = shared.seenRecordUris.putIfAbsentAndGetNewKey(recordUri, true);

        statementManager.requestAddStatements()
            .withOptional(trackUri,      RDFS.LABEL,                literalFor(title))
            .withOptional(trackUri,      DC.TITLE,                  literalFor(title))
            .with(        trackUri,      FOAF.MAKER,                makerUris.stream())

            .with(        recordUri,     MO.P_TRACK,                trackUri)
            .with(        recordUri,     FOAF.MAKER,                makerUris.stream())

            .withOptional(newRecordUri,  RDF.TYPE,                  MO.C_RECORD)
            .withOptional(newRecordUri,  RDFS.LABEL,                literalFor(recordTitle))
            .withOptional(newRecordUri,  DC.TITLE,                  literalFor(recordTitle))
            .withOptional(newRecordUri,  MO.P_MEDIA_TYPE,           MO.C_CD)
            .withOptional(newRecordUri,  BM.ITUNES_CDDB1,           literalFor(metadata.get(ITUNES_COMMENT)
                                                                                       .map(c -> c.getCddb1())))

            .with(        newArtistUris, RDF.TYPE,                  MO.C_MUSIC_ARTIST)
            .with(        newArtistUris, RDFS.LABEL,                newArtistLiterals)
            .with(        newArtistUris, FOAF.NAME,                 newArtistLiterals)

            .withOptional(newGroupUri,   RDF.TYPE,                  MO.C_MUSIC_ARTIST)
            .withOptional(newGroupUri,   RDFS.LABEL,                literalFor(makerName))
            .withOptional(newGroupUri,   FOAF.NAME,                 literalFor(makerName))
            .withOptional(newGroupUri,   DbTune.ARTIST_TYPE,        literalFor((short)2))
            .withOptional(newGroupUri,   Purl.COLLABORATES_WITH,    artists.stream().map(Entry::getUri))
            .publish();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI createIRIForLocalRecord (final @Nonnull String recordTitle)
      {
        return BM.localRecordIriFor(idCreator.createSha1("RECORD:" + recordTitle));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI createIRIForLocalArtist (final @Nonnull String name)
      {
        return BM.localArtistIriFor(idCreator.createSha1("ARTIST:" + name));
      }
  }
