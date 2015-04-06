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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import it.tidalwave.util.Id;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.persistence.AddStatementsRequest;
import it.tidalwave.bluemarine2.vocabulary.DbTune;
import it.tidalwave.bluemarine2.vocabulary.MO;
import it.tidalwave.bluemarine2.vocabulary.Purl;
import lombok.extern.slf4j.Slf4j;
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
    private final Set<Id> seenArtistIds = Collections.synchronizedSet(new HashSet<Id>());
    
    @Inject
    private Progress progress;
    
    @Inject
    private MessageBus messageBus;
    
    /*******************************************************************************************************************
     *
     * Imports the DbTune.org metadata for the given {@link MediaItem}.
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the URI of the item
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
    public void importMediaItemDbTuneMetadata (final @Nonnull MediaItem mediaItem, 
                                                final @Nonnull URI mediaItemUri)
      throws IOException, JAXBException, InterruptedException 
      { 
        log.info("importMediaItemDbTuneMetadata({}, {})", mediaItem, mediaItemUri);
        
        final MediaItem.Metadata metadata = mediaItem.getMetadata();
        final String mbGuid = metadata.get(MediaItem.Metadata.MBZ_TRACK_ID).get().stringValue().replaceAll("^mbz:", "");
        messageBus.publish(new AddStatementsRequest(mediaItemUri, MO.MUSICBRAINZ_GUID, literalFor(mbGuid)));
        messageBus.publish(new DownloadRequest(new URL(mediaItemUri.toString()), FOLLOW_REDIRECT));
        progress.incrementTotalDownloads();
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void onDbTuneTrackMetadataDownloadComplete (final @Nonnull DownloadComplete message) 
      throws InterruptedException, IOException
      {
        try 
          {
            log.info("onTrackDownloadComplete({})", message);

            final Model model = parseModel(message);
            AddStatementsRequest.Builder builder = AddStatementsRequest.build();

            model.filter(null, FOAF.MAKER, null).forEach((statement) ->
              {
                try
                  {
                    final URI artistUri = (URI)statement.getObject();
                    //                      // FIXME: should be builder = builder.with()
                    builder.with(statement.getSubject(), statement.getPredicate(), artistUri);
                    requestDbTuneArtistMetadata(artistUri);
                  }
                catch (MalformedURLException e)
                  {
                    throw new RuntimeException(e);
                  }
              });

            messageBus.publish(builder.create());
          }   
        catch (RDFHandlerException | RDFParseException ex)
          {
            log.error("Cannot parse track: {}", ex.toString());
            log.error("Cannot parse track: {}", new String(message.getBytes()));
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void onDbTuneArtistMetadataDownloadComplete (final @Nonnull DownloadComplete message) 
      throws InterruptedException, IOException
      {
        List<URI> validPredicates = Arrays.asList(
                DbTune.ARTIST_TYPE, DbTune.SORT_NAME, Purl.EVENT, RDFS.LABEL, FOAF.NAME);
        try 
          {
            log.info("onArtistDownloadComplete({})", message);
            final URI artistUri = uriFor(message.getUrl().toString());
            final Model model = parseModel(message);
            AddStatementsRequest.Builder builder = AddStatementsRequest.build();

            model.forEach((statement) -> 
              {
                final Resource subject = statement.getSubject();
                final URI predicate = statement.getPredicate();
                final Value object = statement.getObject();
                
                // foaf:maker would include all the items in the database, not only in our collection
                // anyway, the required statements have been already added when importing tracks
                if (predicate.equals(FOAF.MAKER))
                  {
                    return;
                  }
                
                else if (subject.equals(artistUri))
                  {
                    if (validPredicates.contains(predicate))
                      {
                        // FIXME: should be builder = builder.with()
                        builder.with(subject, predicate, object);
                      }
//  TODO: extract only GUID?       mo:musicbrainz <http://musicbrainz.org/artist/1f9df192-a621-4f54-8850-2c5373b7eac9> ;
// TODO: download bio event details
                  }
                
                else
                  {
                    return;
                  }
                
// TODO: rel:collaboratesWith
// TODO:<http://dbpedia.org/resource/Frank_Sinatra>
//      rdfs:seeAlso <http://dbtune.org/musicbrainz/sparql?query=DESCRIBE+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FFrank_Sinatra%3E> .
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
              });

            messageBus.publish(builder.create());
          }   
        catch (RDFHandlerException | RDFParseException ex)
          {
            log.error("Cannot parse artist: {}", ex.toString());
            log.error("Cannot parse artist: {}", new String(message.getBytes()));
          }
        finally
          {
            progress.incrementImportedArtists();
          }
      }
    
   /*******************************************************************************************************************
     *
     * Posts a requesto to download metadata for the given {@code artistUri}, if not available yet.
     * 
     * @param   artistUri   the URI of the artist
     *
     ******************************************************************************************************************/
    private void requestDbTuneArtistMetadata (final @Nonnull URI artistUri)
      throws MalformedURLException
      {
        synchronized (seenArtistIds)
          {
            final Id artistId = new Id(artistUri.stringValue());
            
            if (!seenArtistIds.contains(artistId))
              {
                seenArtistIds.add(artistId);
                progress.incrementTotalArtists();
                progress.incrementTotalDownloads();
                messageBus.publish(new DownloadRequest(new URL(artistUri.toString()), FOLLOW_REDIRECT));
              }
          }
      }
  }
