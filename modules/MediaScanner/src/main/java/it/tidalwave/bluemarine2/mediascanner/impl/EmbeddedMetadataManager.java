/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.SimpleComposite8.SimpleComposite8;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;
import it.tidalwave.bluemarine2.model.MediaFolder;
import java.nio.file.Files;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class EmbeddedMetadataManager 
  {
    // Set would suffice, but there's no ConcurrentSet
    private final ConcurrentMap<URI, Boolean> seenArtistUris = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<URI, Boolean> seenRecordUris = new ConcurrentHashMap<>();
    
    @Inject
    private StatementManager statementManager;
    
    @Inject
    private IdCreator idCreator;
    
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
    
        SIGNAL_MAPPER.put(Metadata.SAMPLE_RATE, v -> new Pair(MO.P_SAMPLE_RATE,     literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.BIT_RATE,    v -> new Pair(MO.P_BITS_PER_SAMPLE, literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.DURATION,    v -> new Pair(MO.P_DURATION,        
                                                           literalFor((float)((Duration)v).toMillis())));
      }
    
    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    public void reset()
      {
        // FIXME: should load existing URIs from the Persistence
        seenArtistUris.clear();
        seenRecordUris.clear();
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
        log.debug("importFallbackTrackMetadata({}, {})", mediaItem, trackUri);
        
        final Metadata metadata           = mediaItem.getMetadata();  
        final Optional<String> title      = metadata.get(Metadata.TITLE);
        final Optional<String> artistName = metadata.get(Metadata.ARTIST);
        final Entity parent               = mediaItem.getParent();
        final String recordTitle          = metadata.get(Metadata.ALBUM)
                                                    .orElse(((MediaFolder)parent).getPath().toFile().getName()); // FIXME
//                                                    .orElse(parent.as(Displayable).getDisplayName());
        
        final Optional<URI> artistUri     = artistName.map(name -> createUriForLocalArtist(name));
        final Optional<URI> recordUri     = Optional.of(createUriForLocalRecord(recordTitle));

        final Optional<URI> newArtistUri  = putIfAbsentAndGetNewKey(seenArtistUris, artistUri, true);
        final Optional<URI> newRecordUri  = putIfAbsentAndGetNewKey(seenRecordUris, recordUri, true);
        
        statementManager.requestAddStatements()
                         .with(trackUri,     RDFS.LABEL,       literalFor(title))
                         .with(trackUri,     DC.TITLE,         literalFor(title))
                         .with(trackUri,     FOAF.MAKER,       artistUri)

                         .with(recordUri,    MO.P_TRACK,       trackUri)
        
                         .with(newArtistUri, RDF.TYPE,         MO.C_MUSIC_ARTIST)
                         .with(newArtistUri, RDFS.LABEL,       literalFor(artistName))
                         .with(newArtistUri, FOAF.NAME,        literalFor(artistName))
        
                         .with(newRecordUri, RDF.TYPE,         MO.C_RECORD)
                         .with(newRecordUri, RDFS.LABEL,       literalFor(recordTitle))
                         .with(newRecordUri, DC.TITLE,         literalFor(recordTitle))
                         .with(newRecordUri, FOAF.MAKER,       artistUri)
                         .with(newRecordUri, MO.P_MEDIA_TYPE,  MO.C_CD)
                         .with(newRecordUri, MO.P_TRACK_COUNT, literalFor(parent.as(SimpleComposite8).findChildren()
                                                                                                     .count()))
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
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    // FIXME: rename (semantics is slightly different than the original putIfAbsent())
    private static <K, V> Optional<K> putIfAbsentAndGetNewKey (final @Nonnull ConcurrentMap<K, V> cMap, 
                                                   final @Nonnull Optional<K> key,
                                                   final @Nonnull V value)
      {
        return (key.isPresent() && (cMap.putIfAbsent(key.get(), value) == null)) ? key : Optional.empty();
      }
  }
