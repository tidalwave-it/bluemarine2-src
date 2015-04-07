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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import it.tidalwave.util.Id;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.persistence.AddStatementsRequest;
import it.tidalwave.bluemarine2.vocabulary.MO;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class EmbeddedMetadataManager 
  {
    @Inject
    private MessageBus messageBus;
    
    @Inject
    private Md5IdCreator md5IdCreator;

    private final Set<Id> seenArtistIds = Collections.synchronizedSet(new HashSet<Id>());
    
    private final Set<String> seenCdNames = new HashSet<>();
    
    /*******************************************************************************************************************
     *
     * Imports the embedded metadata for the given {@link MediaItem}.
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the URI of the item
     * 
     ******************************************************************************************************************/
    public void importMediaItemEmbeddedMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI mediaItemUri)
      {
        log.info("importMediaItemEmbeddedMetadata({}, {})", mediaItem, mediaItemUri);
        
        final MediaItem.Metadata metadata = mediaItem.getMetadata();
        final Optional<Integer> trackNumber = metadata.get(MediaItem.Metadata.TRACK);
        final Optional<Integer> sampleRate = metadata.get(MediaItem.Metadata.SAMPLE_RATE);
        final Optional<Integer> bitRate = metadata.get(MediaItem.Metadata.BIT_RATE);
        final Optional<Duration> duration = metadata.get(MediaItem.Metadata.DURATION);

        AddStatementsRequest.Builder builder = AddStatementsRequest.build();
        
        if (sampleRate.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.SAMPLE_RATE, literalFor(sampleRate.get()));
          }

        if (bitRate.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.BITS_PER_SAMPLE, literalFor(bitRate.get()));
          }

        if (trackNumber.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.TRACK_NUMBER, literalFor(trackNumber.get()));
          }

        if (duration.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.DURATION, literalFor((float)duration.get().toMillis()));
          }
        
        messageBus.publish(builder.create());
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public void importFallbackEmbeddedMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI mediaItemUri) 
      {
        log.info("importFallbackEmbeddedMetadata({}, {})", mediaItem, mediaItemUri);
        
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
            final Id artistId = md5IdCreator.createMd5Id("ARTIST:" + artist.get());
            final URI artistUri = uriFor(artistId);
            
            // FIXME: concurrent access
            if (!seenArtistIds.contains(artistId))
              {
                seenArtistIds.add(artistId);
                builder = builder.with(artistUri, RDF.TYPE, MO.MUSIC_ARTIST)
                                 .with(artistUri, FOAF.NAME, literalFor(artist.get()));
              }
            
            builder = builder.with(artistUri, FOAF.MAKER, mediaItemUri);
          }
        
        final MediaFolder parent = mediaItem.getParent();
        final String cdTitle = parent.getPath().toFile().getName();
        final URI cdUri = uriFor(md5IdCreator.createMd5Id("CD:" + cdTitle));
                
        // FIXME: concurrent
        if (!seenCdNames.contains(cdTitle))
          {
            seenCdNames.add(cdTitle);
            builder = builder.with(cdUri, RDF.TYPE, MO.RECORD)
                             .with(cdUri, MO.MEDIA_TYPE, MO.CD)
                             .with(cdUri, DC.TITLE, literalFor(cdTitle))
                             .with(cdUri, MO.TRACK_COUNT, literalFor(parent.findChildren().count()));
          }
        
        messageBus.publish(builder.with(cdUri, MO._TRACK, mediaItemUri).create());
      }
  }
