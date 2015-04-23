/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
import org.openrdf.model.URI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import it.tidalwave.util.Key;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.vocabulary.BM;
import it.tidalwave.bluemarine2.vocabulary.MO;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.vocabulary.DbTune;
import it.tidalwave.bluemarine2.vocabulary.Purl;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import java.util.stream.Stream;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class EmbeddedMetadataManager 
  {
    @RequiredArgsConstructor @Getter @ToString
    static class Entry
      {
        @Nonnull
        private final URI uri;
        
        @Nonnull
        private final String name;
      }

    @Inject
    private DbTuneMetadataManager dbTuneMetadataManager;
    
    @Inject
    private StatementManager statementManager;
    
    @Inject
    private IdCreator idCreator;
    
    @Inject
    private Shared shared;
    
    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    @Immutable @RequiredArgsConstructor @ToString
    static class Pair
      {
        @Nonnull
        private final URI predicate;
        
        @Nonnull
        private final Value object;
        
        @Nonnull
        public Statement createStatementWithSubject (final @Nonnull URI subject)
          {
            return ValueFactoryImpl.getInstance().createStatement(subject, predicate, object);
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
        @Nonnull
        public List<Statement> statementsFor (final @Nonnull Metadata metadata, final @Nonnull URI subjectUri)
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

    private static final Mapper SIGNAL_MAPPER = new Mapper();
    private static final Mapper TRACK_MAPPER = new Mapper();

    static
      {
        TRACK_MAPPER. put(Metadata.TRACK,       v -> new Pair(MO.P_TRACK_NUMBER,    literalFor((int)v)));
        TRACK_MAPPER. put(Metadata.DISK_NUMBER, v -> new Pair(BM.DISK_NUMBER,       literalFor((int)v)));
        TRACK_MAPPER. put(Metadata.DISK_COUNT,  v -> new Pair(BM.DISK_COUNT,       literalFor((int)v)));
    
        SIGNAL_MAPPER.put(Metadata.SAMPLE_RATE, v -> new Pair(MO.P_SAMPLE_RATE,     literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.BIT_RATE,    v -> new Pair(MO.P_BITS_PER_SAMPLE, literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.DURATION,    v -> new Pair(MO.P_DURATION,        
                                                           literalFor((float)((Duration)v).toMillis())));
      }
    
    /*******************************************************************************************************************
     *
     * Imports the metadata embedded in a file for the given {@link MediaItem}. It only processes the portion of 
     * metadata which are never superseded by external catalogs (such as sample rate, duration, etc...).
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   signalUri               the URI of the signal
     * @param   trackUri                the URI of the track
     * 
     ******************************************************************************************************************/
    public void importAudioFileMetadata (final @Nonnull MediaItem mediaItem, 
                                         final @Nonnull URI signalUri,
                                         final @Nonnull URI trackUri)
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
     * @param   trackUri                the URI of the track
     *
     ******************************************************************************************************************/
    public void importFallbackTrackMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI trackUri) 
      {
        // TODO: consider the MBZ COMPOSER.
        log.debug("importFallbackTrackMetadata({}, {})", mediaItem, trackUri);
        
        final Metadata metadata           = mediaItem.getMetadata();  
        final Entity parent               = mediaItem.getParent();
        log.debug(">>>> metadata of {}: {}", trackUri, metadata);
        
        final Optional<String> title      = metadata.get(Metadata.TITLE);
        final Optional<String> makerName  = metadata.get(Metadata.ARTIST);
        
        List<URI> makerUris               = null;
        List<Entry> artists  = metadata.getAll(Metadata.MBZ_ARTIST_ID).stream()
                .map(id -> new Entry(BM.musicBrainzUriFor("artist", id), makerName.orElse("???")))
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
            makerUris =  makerName.map(name -> asList(createUriForLocalArtist(name))).orElse(emptyList());
            artists = makerName.map(name -> Stream.of(name.split("[;]")).map(String::trim)).orElse(Stream.empty())
                               .map(name -> new Entry(createUriForLocalArtist(name), name))
                               .collect(toList());
          }
        
        final List<Entry> newArtists   = artists.stream().filter(
                e -> shared.seenArtistUris.putIfAbsentAndGetNewKey(e.getUri(), Optional.empty()).isPresent())
                .collect(toList());
        final List<URI> newArtistUris       = newArtists.stream().map(Entry::getUri).collect(toList());
        final List<Value> newArtistLiterals = newArtists.stream().map(e -> literalFor(e.getName())).collect(toList());
        
        final Optional<URI> newGroupUri = (artists.size() <= 1) ? Optional.empty()
                : shared.seenArtistUris.putIfAbsentAndGetNewKey(makerUris.get(0), Optional.empty()); // FIXME: onlt first one?

        final String recordTitle          = metadata.get(Metadata.ALBUM)
                                                    .orElse(((MediaFolder)parent).getPath().toFile().getName()); // FIXME
//                                                    .orElse(parent.as(Displayable).getDisplayName());
        
        final URI recordUri               = createUriForLocalRecord(recordTitle);
        final Optional<URI> newRecordUri  = shared.seenRecordUris.putIfAbsentAndGetNewKey(recordUri, true);
        
        statementManager.requestAddStatements()
            .withOptional(trackUri,      RDFS.LABEL,                literalFor(title))
            .withOptional(trackUri,      DC.TITLE,                  literalFor(title))
            .with(        trackUri,      FOAF.MAKER,                makerUris.stream())

            .with(        recordUri,     MO.P_TRACK,                trackUri)

            .withOptional(newRecordUri,  RDF.TYPE,                  MO.C_RECORD)
            .withOptional(newRecordUri,  RDFS.LABEL,                literalFor(recordTitle))
            .withOptional(newRecordUri,  DC.TITLE,                  literalFor(recordTitle))
            .withOptional(newRecordUri,  MO.P_MEDIA_TYPE,           MO.C_CD)
            .withOptional(newRecordUri,  FOAF.MAKER,                makerUris.stream())
                
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
    private URI createUriForLocalRecord (final @Nonnull String recordTitle) 
      {
        return BM.localRecordUriFor(idCreator.createSha1("RECORD:" + recordTitle));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private URI createUriForLocalArtist (final @Nonnull String name) 
      {
        return BM.localArtistUriFor(idCreator.createSha1("ARTIST:" + name));
      }
  }
