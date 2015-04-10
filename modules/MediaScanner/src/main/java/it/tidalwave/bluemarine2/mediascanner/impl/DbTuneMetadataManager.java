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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.vocabulary.DbTune;
import it.tidalwave.bluemarine2.vocabulary.MO;
import it.tidalwave.bluemarine2.vocabulary.Purl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.downloader.DownloadRequest.Option.FOLLOW_REDIRECT;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DbTuneMetadataManager 
  {
    // Set would suffice, but there's no ConcurrentSet
    private final ConcurrentMap<URI, Boolean> seenArtistUris = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<URI, Boolean> seenRecordUris = new ConcurrentHashMap<>();
    
    @Inject
    private ProgressHandler progress;
    
    @Inject
    private MessageBus messageBus;
    
    @Inject
    private AddStatementManager statementManager;
    
    private static final List<URI> VALID_TRACK_PREDICATES_FOR_SUBJECT = Arrays.asList(
            RDF.TYPE, RDFS.LABEL, DC.TITLE, FOAF.MAKER);
    
    // Skip foaf:maker: it would include all the items in the database, not only in our collection
    // anyway, the required statements have been already added when importing tracks
    private static final List<URI> VALID_ARTIST_PREDICATES_FOR_SUBJECT = Arrays.asList(
            DbTune.ARTIST_TYPE, DbTune.SORT_NAME, Purl.EVENT, Purl.COLLABORATES_WITH, RDF.TYPE, RDFS.LABEL, FOAF.NAME);

//  TODO: extract only GUID?       mo:musicbrainz <http://musicbrainz.org/artist/1f9df192-a621-4f54-8850-2c5373b7eac9> ;
// TODO: download bio event details
                
// TODO      =       <http://www.bbc.co.uk/music/artists/83e71a21-caf7-4e48-8ff7-6512d51e88a3#artist> , <http://dbpedia.org/resource/Henry_Mancini> ;
                /*
                <http://dbtune.org/musicbrainz/resource/performance/98398> <http://purl.org/NET/c4dm/event.owl#agent> <http://dbtune.org/musicbrainz/resource/artist/86e2e2ad-6d1b-44fd-9463-b6683718a1cc> ;
                mo:performer <http://dbtune.org/musicbrainz/resource/artist/86e2e2ad-6d1b-44fd-9463-b6683718a1cc> .
                mo:orchestra <http://dbtune.org/musicbrainz/resource/artist/98e4313e-dfb0-4084-805c-3e42ef9301d0> ;
                mo:symphony_orchestra <http://dbtune.org/musicbrainz/resource/artist/98e4313e-dfb0-4084-805c-3e42ef9301d0> .
                mo:soprano <http://dbtune.org/musicbrainz/resource/artist/361fcd46-41c5-4503-aa43-a87937583909> .
                mo:orchestra <http://dbtune.org/musicbrainz/resource/artist/5c8fd1e4-574d-495f-9a24-2dfaadf2e8c0> .
                mo:conductor <http://dbtune.org/musicbrainz/resource/artist/fa39bc82-9b27-4bbb-9425-d719a72e09ac> .
                mo:lead_singer <http://dbtune.org/musicbrainz/resource/artist/7cce3b8e-623c-4078-b079-837cbcf638c4> ;
                mo:singer <http://dbtune.org/musicbrainz/resource/artist/7cce3b8e-623c-4078-b079-837cbcf638c4> .
                mo:choir <http://dbtune.org/musicbrainz/resource/artist/8f169b84-95d6-4797-bc00-4cd601fb631e> ;
                mo:chamber_orchestra <http://dbtune.org/musicbrainz/resource/artist/3a9f0e21-5796-4f04-bd4c-3deafd59ad80> ;
                mo:background_singer <http://dbtune.org/musicbrainz/resource/artist/86e2e2ad-6d1b-44fd-9463-b6683718a1cc> ;
                
                escludi sparql
                
                <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Ludwig_van_Beethoven> , <http://de.wikipedia.org/wiki/Ludwig_van_Beethoven> , <http://www.bbc.co.uk/music/artists/1f9df192-a621-4f54-8850-2c5373b7eac9#artist> ;
            */         
        
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor
    static class TrackStatementFilter implements Predicate<Statement>  
      {
        @Nonnull
        private final URI trackUri;
        
        @Override
        public boolean test (final @Nonnull Statement statement) 
          {
            final URI predicate = statement.getPredicate();
            return (statement.getSubject().equals(trackUri) && VALID_TRACK_PREDICATES_FOR_SUBJECT.contains(predicate));
          }
      }
            
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor
    static class ArtistStatementFilter implements Predicate<Statement>  
      {
        @Nonnull
        private final URI artistUri;
        
        @Override
        public boolean test (final @Nonnull Statement statement) 
          {
            final URI predicate = statement.getPredicate();
            return (statement.getSubject().equals(artistUri) && VALID_ARTIST_PREDICATES_FOR_SUBJECT.contains(predicate));
          }
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
    
    private final ConcurrentMap<URI, MediaItem> m = new ConcurrentHashMap<>();
    
    /*******************************************************************************************************************
     *
     * Imports the DbTune.org metadata for the given track.
     * 
     * @param   trackUri            the URI of the item
     * @param   mbGuid              the MusicBrainz id
     * 
     ******************************************************************************************************************/
    public void importTrackMetadata (final @Nonnull MediaItem mediaItem,
                                     final @Nonnull URI trackUri, 
                                     final @Nonnull String mbGuid)
      { 
        try 
          {
            log.debug("importTrackMetadata({})", trackUri);
            m.put(trackUri, mediaItem);
            statementManager.requestAddStatement(trackUri, MO.P_MUSICBRAINZ_GUID, literalFor(mbGuid));
            requestDownload(urlFor(trackUri));
          }
        catch (MalformedURLException e) // shoudn't never happen
          {
            log.error("Cannot parse track URL: {}", e.toString());
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void onTrackMetadataDownloadComplete (final @Nonnull DownloadComplete message) 
      {
        final URI trackUri = uriFor(message.getUrl());
        final MediaItem mediaItem = m.remove(trackUri);
        
        assert mediaItem != null : "Null mediaItem for " + trackUri;
        
        if (message.getStatusCode() == 200) // FIXME
          {
            try 
              {
                log.debug("onTrackMetadataDownloadComplete({})", message);
                final Model model = parseModel(message);
                statementManager.requestAddStatements(model.stream().filter(new TrackStatementFilter(trackUri))
                                                           .collect(toList()));
                model.filter(trackUri, FOAF.MAKER, null)
                     .forEach(statement -> requestArtistMetadata((URI)statement.getObject()));
                model.filter(null, MO.P_TRACK, trackUri)
                     .forEach(statement -> requestRecordMetadata((URI)statement.getSubject()));
              }   
            catch (IOException | RDFHandlerException | RDFParseException e)
              {
                log.error("Cannot parse track: {}", e.toString());
                log.error("Cannot parse track: {}", new String(message.getBytes()));
              }
          }
        else
          {
            embeddedMetadataManager.importFallbackTrackMetadata(mediaItem, uriFor(message.getUrl())); // CORRECT URI?
          }
      }
    
    @Inject
    private EmbeddedMetadataManager embeddedMetadataManager;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void onArtistMetadataDownloadComplete (final @Nonnull DownloadComplete message) 
      {
        try 
          {
            log.debug("onArtistMetadataDownloadComplete({})", message);
            final URI artistUri = uriFor(message.getUrl());
            final Model model = parseModel(message);
            statementManager.requestAddStatements(model.stream().filter(new ArtistStatementFilter(artistUri))
                                                       .collect(toList()));
            model.filter(artistUri, Purl.COLLABORATES_WITH, null)
                 .forEach(statement -> requestArtistMetadata((URI)statement.getObject()));
          }   
        catch (IOException | RDFHandlerException | RDFParseException e)
          {
            log.error("Cannot parse artist: {}", e.toString());
            log.error("Cannot parse artist: {}", new String(message.getBytes()));
          }
        finally
          {
            progress.incrementImportedArtists();
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void onRecordMetadataDownloadComplete (final @Nonnull DownloadComplete message)
      {
        try 
          {
            log.debug("onRecordMetadataDownloadComplete({})", message);
            final URI recordUri = uriFor(message.getUrl());
            final Model model = parseModel(message);
             // FIXME: filter away some more stuff
            statementManager.requestAddStatements(model.filter(recordUri, null, null).stream().collect(toList()));
          }   
        catch (IOException | RDFHandlerException | RDFParseException e)
          {
            log.error("Cannot parse record: {}", e.toString());
            log.error("Cannot parse record: {}", new String(message.getBytes()));
          }
        finally
          {
            progress.incrementImportedRecords();
          }
      }
    
   /*******************************************************************************************************************
     *
     * Posts a requesto to download metadata for the given {@code artistUri}, if not available yet.
     * 
     * @param   artistUri   the URI of the artist
     *
     ******************************************************************************************************************/
    private void requestArtistMetadata (final @Nonnull URI artistUri)
      {
        try
          {
            log.debug("requestArtistMetadata({})", artistUri);
            
            if (seenArtistUris.putIfAbsent(artistUri, true) == null)
              {
                progress.incrementTotalArtists();
                requestDownload(urlFor(artistUri));
              }
          }
        catch (MalformedURLException e)
          {
            log.error("Malformed URL: {}", e);
          }
      }
    
   /*******************************************************************************************************************
     *
     * Posts a requesto to download metadata for the given {@code artistUri}, if not available yet.
     * 
     * @param   recordUri   the URI of the artist
     *
     ******************************************************************************************************************/
    private void requestRecordMetadata (final @Nonnull URI recordUri)
      {
        try
          {
            log.debug("requestRecordMetadata({})", recordUri);
            
            if (seenRecordUris.putIfAbsent(recordUri, true) == null)
              {
                progress.incrementTotalRecords();
                requestDownload(urlFor(recordUri));
              }
          }
        catch (MalformedURLException e)
          {
            log.error("Malformed URL: {}", e);
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void requestDownload (final @Nonnull URL url)
      {
        progress.incrementTotalDownloads();
        messageBus.publish(new DownloadRequest(url, FOLLOW_REDIRECT));
      }
  }
