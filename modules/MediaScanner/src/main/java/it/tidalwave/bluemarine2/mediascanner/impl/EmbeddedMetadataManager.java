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
import javax.inject.Inject;
import java.time.Duration;
import java.util.HashMap;
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
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.persistence.AddStatementsRequest;
import it.tidalwave.bluemarine2.vocabulary.MO;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;
import static it.tidalwave.bluemarine2.persistence.AddStatementsRequest.*;
import it.tidalwave.bluemarine2.vocabulary.BM;

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
    private MessageBus messageBus;
    
    @Inject
    private IdCreator idCreator;

    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    @RequiredArgsConstructor @ToString
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
     * 
     * 
     ******************************************************************************************************************/
    private static final Map<Key<?>, Function<Object, Pair>> SIGNAL_MAPPER = new HashMap<>();
    private static final Map<Key<?>, Function<Object, Pair>> TRACK_MAPPER = new HashMap<>();

    static
      {
        TRACK_MAPPER. put(Metadata.TRACK,       v -> new Pair(MO.P_TRACK_NUMBER,    literalFor((int)v)));
    
        SIGNAL_MAPPER.put(Metadata.SAMPLE_RATE, v -> new Pair(MO.P_SAMPLE_RATE,     literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.BIT_RATE,    v -> new Pair(MO.P_BITS_PER_SAMPLE, literalFor((int)v)));
        SIGNAL_MAPPER.put(Metadata.DURATION,    v -> new Pair(MO.P_DURATION,        literalFor((float)((Duration)v).toMillis())));
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
     * @param   signalUri            the URI of the item
     * 
     ******************************************************************************************************************/
    public void importAudioFileMetadata (final @Nonnull MediaItem mediaItem, 
                                         final @Nonnull URI signalUri,
                                         final @Nonnull URI trackUri)
      {
        log.debug("importAudioFileMetadata({}, {})", mediaItem, signalUri);
        messageBus.publish(mediaItem.getMetadata().getEntries().stream()
                        .filter(e -> SIGNAL_MAPPER.containsKey(e.getKey()))
                        .map(e -> SIGNAL_MAPPER.get(e.getKey()).apply(e.getValue()).createStatementWithSubject(signalUri))
                        .collect(toAddStatementsRequest()));
        messageBus.publish(mediaItem.getMetadata().getEntries().stream()
                        .filter(e -> TRACK_MAPPER.containsKey(e.getKey()))
                        .map(e -> TRACK_MAPPER.get(e.getKey()).apply(e.getValue()).createStatementWithSubject(trackUri))
                        .collect(toAddStatementsRequest()));
      }
    
    /*******************************************************************************************************************
     *
     * Imports all the remaining metadata embedded in a track for the given {@link MediaItem}. This method is called
     * when we failed to match a track to an external catalog.
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the URI of the item
     *
     ******************************************************************************************************************/
    public void importFallbackTrackMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI mediaItemUri) 
      {
        log.debug("importFallbackTrackMetadata({}, {})", mediaItem, mediaItemUri);
        
        AddStatementsRequest.Builder builder = AddStatementsRequest.build();
        final MediaItem.Metadata metadata = mediaItem.getMetadata();  
        final Optional<String> title = metadata.get(MediaItem.Metadata.TITLE);
        final Optional<String> artist = metadata.get(MediaItem.Metadata.ARTIST);
        
        if (title.isPresent())
          {
            final Value titleLiteral = literalFor(title.get());
            builder = builder.with(mediaItemUri, DC.TITLE, titleLiteral)
                             .with(mediaItemUri, RDFS.LABEL, titleLiteral);
          }
        
        if (artist.isPresent())
          {
            final URI artistUri = BM.localArtistUriFor(idCreator.createSha1("ARTIST:" + artist.get()));
            
            if (seenArtistUris.putIfAbsent(artistUri, true) == null)
              {
                final Value nameLiteral = literalFor(artist.get());
                builder = builder.with(artistUri, RDF.TYPE, MO.C_MUSIC_ARTIST)
                                 .with(artistUri, FOAF.NAME, nameLiteral)
                                 .with(artistUri, RDFS.LABEL, nameLiteral);
              }
            
            builder = builder.with(artistUri, FOAF.MAKER, mediaItemUri);
          }
        
        final MediaFolder parent = mediaItem.getParent();
        final String recordTitle = parent.getPath().toFile().getName();
        final URI recordUri = BM.localRecordUriFor(idCreator.createSha1("RECORD:" + recordTitle));
                
        if (seenRecordUris.putIfAbsent(recordUri, true) == null)
          {
            final Value titleLiteral = literalFor(recordTitle);
            builder = builder.with(recordUri, RDF.TYPE, MO.C_RECORD)
                             .with(recordUri, MO.P_MEDIA_TYPE, MO.C_CD)
                             .with(recordUri, DC.TITLE, titleLiteral)
                             .with(recordUri, RDFS.LABEL, titleLiteral)
                             .with(recordUri, MO.P_TRACK_COUNT, literalFor(parent.findChildren().count()));
          }
        
        messageBus.publish(builder.with(recordUri, MO.P_TRACK, mediaItemUri).create());
      }
  }
