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
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.Instant;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.musicbrainz.ns.mmd_2.Artist;
import org.musicbrainz.ns.mmd_2.ArtistCredit;
import org.musicbrainz.ns.mmd_2.NameCredit;
import org.musicbrainz.ns.mmd_2.Recording;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.n3.N3ParserFactory;
import it.tidalwave.util.Id;
import it.tidalwave.util.InstantProvider;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.persistence.AddStatementsRequest;
import it.tidalwave.bluemarine2.vocabulary.BM;
import it.tidalwave.bluemarine2.vocabulary.MO;
import it.tidalwave.bluemarine2.mediascanner.ScanCompleted;
import it.tidalwave.bluemarine2.musicbrainz.impl.DefaultMusicBrainzApi;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import it.tidalwave.bluemarine2.downloader.DownloadRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.ToString;
import static java.util.stream.Collectors.joining;
import static it.tidalwave.bluemarine2.downloader.DownloadRequest.Option.*;
import it.tidalwave.bluemarine2.vocabulary.DbTune;
import it.tidalwave.bluemarine2.vocabulary.Purl;
import java.util.Arrays;
import java.util.stream.Collectors;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMediaScanner 
  {
    private final Set<Id> seenArtistIds = Collections.synchronizedSet(new HashSet<Id>());
    
    // FIXME: inject
    private final DefaultMusicBrainzApi mbApi = new DefaultMusicBrainzApi();
    
    private final ValueFactory factory = ValueFactoryImpl.getInstance(); // FIXME
    
    @Inject
    private MessageBus messageBus;
    
    @Inject
    private InstantProvider timestampProvider;
    
    // FIXME: inject
    private Md5IdCreator md5IdCreator = new Md5IdCreator();

    @ToString
    class Progress
      {
        private volatile int totalFolders;
        private volatile int scannedFolders;
        private volatile int totalMediaItems;
        private volatile int importedMediaItems;
        private volatile int totalArtists;
        private volatile int importedArtists;
        private volatile int totalDownloads;
        private volatile int completedDownloads;
        
        public synchronized void reset()
          {
            totalFolders = scannedFolders =
            totalMediaItems = importedMediaItems =
            totalDownloads = completedDownloads = 0;  
          }
        
        public synchronized void incrementTotalFolders()
          {
            totalFolders++;  
            check();
          }
        
        public synchronized void incrementScannedFolders()
          {
            scannedFolders++;  
            check();
          }
        
        public synchronized void incrementTotalMediaItems()
          {
            totalMediaItems++;  
            check();
          }
        
        public synchronized void incrementImportedMediaItems()
          {
            importedMediaItems++;  
            check();
          }

        public synchronized void incrementTotalArtists() 
          {
            totalArtists++;  
            check();
          }

        public synchronized void incrementImportedArtists()
          {
            importedArtists++;  
            check();
          }

        public synchronized void incrementTotalDownloads() 
          {
            totalDownloads++;  
            check();
          }

        public synchronized void incrementCompletedDownloads() 
          {
            completedDownloads++;  
            check();
          }
        
        private void check()
          {
            log.debug("{}", this); // FIXME: is called from sync block
            
            if (isCompleted())
              {
                messageBus.publish(new ScanCompleted());
              }
          }
        
        public synchronized boolean isCompleted()
          {
            return (scannedFolders == totalFolders) 
                && (importedMediaItems == totalMediaItems)
                && (importedArtists == totalArtists)
                && (completedDownloads == totalDownloads);
          }
      }
    
    private final Progress progress = new Progress();
    
    /*******************************************************************************************************************
     *
     * Processes a folder of {@link MediaItem}s.
     * 
     * @param   folder      the folder
     *
     ******************************************************************************************************************/
    public void process (final @Nonnull MediaFolder folder)
      {
        log.info("process({})", folder);
        progress.reset();
        progress.incrementTotalFolders();
        messageBus.publish(new InternalMediaFolderScanRequest(folder));
      }
    
    /*******************************************************************************************************************
     *
     * Scans a folder of {@link MediaItem}s.
     * 
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onInternalMediaFolderScanRequest (final @ListensTo @Nonnull InternalMediaFolderScanRequest request)
      {
        try
          {
            log.info("onInternalMediaFolderScanRequest({})", request);

            request.getFolder().findChildren().stream().forEach(item -> 
              {
                if (item instanceof MediaItem)
                  {
                    progress.incrementTotalMediaItems();
                    messageBus.publish(new MediaItemImportRequest((MediaItem)item));
                  }

                else if (item instanceof MediaFolder)
                  {
                    progress.incrementTotalFolders();
                    messageBus.publish(new InternalMediaFolderScanRequest((MediaFolder)item));
                  }
              });
          }
        finally
          {
            progress.incrementScannedFolders();
          }
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
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onArtistImportRequest (final @ListensTo @Nonnull ArtistImportRequest request) 
      throws InterruptedException
      {
        try
          {
            log.info("onArtistImportRequest({})", request);
            final Id artistId = request.getArtistId();
            final Resource artistUri = uriFor("http://musicmusicbrainz.org/ws/2/artist/" + artistId);
            final Artist artist = request.getArtist();
            final Value nameLiteral = literalFor(artist.getName());
            messageBus.publish(AddStatementsRequest.build()
                                               .with(artistUri, RDF.TYPE, MO.MUSIC_ARTIST)
                                               .with(artistUri, RDFS.LABEL, nameLiteral)
                                               .with(artistUri, FOAF.NAME, nameLiteral)
                                               .with(artistUri, MO.MUSICBRAINZ_GUID, literalFor(artistId.stringValue()))
                                               .create());
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
    /* VisibleForTesting */ void onDownloadComplete (final @ListensTo @Nonnull DownloadComplete message) 
      throws InterruptedException, IOException
      {
        // FIXME: check if it's a expected download
        try
          {
            log.info("onDownloadComplete({})", message);
            
            if (message.getStatusCode() == 200) // FIXME
              {
                // TODO: better way to route? Perhaps passing a token in DownloadRequest/DownloadComplete?
                if (message.getUrl().toString().contains("/track/"))
                  {
                    onTrackDownloadComplete(message);
                  }
                else if (message.getUrl().toString().contains("/artist/"))
                  {
                    onArtistDownloadComplete(message);
                  }
              }
          }
        finally 
          {
            progress.incrementCompletedDownloads();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void onTrackDownloadComplete (final @Nonnull DownloadComplete message) 
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
    private void onArtistDownloadComplete (final @Nonnull DownloadComplete message) 
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
     *
     ******************************************************************************************************************/
    @Nonnull
    private Model parseModel (final @Nonnull DownloadComplete message)
        throws RDFHandlerException, RDFParseException, IOException
      {
        final N3ParserFactory n3ParserFactory = new N3ParserFactory();
        final RDFParser parser = n3ParserFactory.getParser();
        final Model model = new LinkedHashModel();
        
        parser.setRDFHandler(new RDFHandlerBase()
          {
            @Override
            public void handleStatement (final @Nonnull Statement statement)
              throws RDFHandlerException
              {
                model.add(statement);
              }
          });


        final byte[] bytes = new String(message.getBytes()).replaceAll(" = ", "owl:sameAs").getBytes(); // FIXME
        final String uri = message.getUrl().toString();          
        parser.parse(new ByteArrayInputStream(bytes), uri);
//        parser.parse(new ByteArrayInputStream(bytes), uri);

        return model;
      }
    
    /*******************************************************************************************************************
     *
     * Processes a {@link MediaItem}.
     * 
     * @param                           mediaItem   the item
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
    private void importMediaItem (final @Nonnull MediaItem mediaItem)
      throws InterruptedException
      { 
        log.debug("importMediaItem({})", mediaItem);
        final Id md5 = md5IdCreator.createMd5Id(mediaItem.getPath());
        URI mediaItemUri = uriFor(md5);
        
        try 
          {
            final Metadata metadata = mediaItem.getMetadata();
            final Optional<Id> musicBrainzTrackId = metadata.get(Metadata.MBZ_TRACK_ID);
            log.debug(">>>> musicBrainzTrackId: {}", musicBrainzTrackId);
            
            if (musicBrainzTrackId.isPresent())
              {
                mediaItemUri = uriFor("http://dbtune.org/musicbrainz/resource/track/" + 
                        musicBrainzTrackId.get().stringValue().replaceAll("^mbz:", ""));
                messageBus.publish(new AddStatementsRequest(mediaItemUri, 
                                                            BM.MD5, 
                                                            literalFor(md5.stringValue().replaceAll("^md5id:", ""))));
              }
            
            final Instant lastModifiedTime = Files.getLastModifiedTime(mediaItem.getPath()).toInstant();
            messageBus.publish(AddStatementsRequest.build()
                            .with(mediaItemUri, RDF.TYPE, MO.TRACK)
                            .with(mediaItemUri, MO.AUDIOFILE, literalFor(mediaItem.getRelativePath()))
                            .with(mediaItemUri, BM.LATEST_INDEXING_TIME, literalFor(lastModifiedTime))
                            .create());
            importMediaItemMetadata(mediaItem, mediaItemUri);
            
//            log.debug(">>>> artistId: {}",         artistIds);
            
            if (musicBrainzTrackId.isPresent())
              {
                importMediaItemDbTuneMetadata(mediaItem, mediaItemUri);
//                importMediaItemMusicBrainzMetadata(mediaItem, mediaItemUri);
              }
            else
              {
                importFallbackMetadata(mediaItem, mediaItemUri);
              } 
          }
        catch (JAXBException | MalformedURLException e) 
          {
            log.error("Failed processing {}", mediaItem);
          }
        catch (IOException e)
          {
            log.warn("Cannot retrieve MusicBrainz metadata {} ... - {}", mediaItem, e.toString());
            importFallbackMetadata(mediaItem, mediaItemUri);
            messageBus.publish(new AddStatementsRequest(mediaItemUri, BM.FAILED_MB_METADATA, literalFor(timestampProvider.getInstant())));

            if (e.getMessage().contains("503")) // throttling error
              {
//                log.warn("Resubmitting {} ... - {}", mediaItem, e.toString());
//                pendingMediaItems.add(mediaItem);
              }
          } 
      }
    
    /*******************************************************************************************************************
     *
     * Imports the embedded metadata for the given {@link MediaItem}.
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the URI of the item
     * 
     ******************************************************************************************************************/
    private void importMediaItemMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI mediaItemUri)
      {
        log.info("importMediaItemMetadata({}, {})", mediaItem, mediaItemUri);
        
        final Metadata metadata = mediaItem.getMetadata();
        final Optional<Integer> trackNumber = metadata.get(Metadata.TRACK);
        final Optional<Integer> sampleRate = metadata.get(Metadata.SAMPLE_RATE);
        final Optional<Integer> bitRate = metadata.get(Metadata.BIT_RATE);
        final Optional<Duration> duration = metadata.get(Metadata.DURATION);

        AddStatementsRequest.Builder builder = AddStatementsRequest.build();
        
        if (sampleRate.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.SAMPLE_RATE, factory.createLiteral(sampleRate.get()));
          }

        if (bitRate.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.BITS_PER_SAMPLE, factory.createLiteral(bitRate.get()));
          }

        if (trackNumber.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.TRACK_NUMBER, factory.createLiteral(trackNumber.get()));
          }

        if (duration.isPresent())
          {
            builder = builder.with(mediaItemUri, MO.DURATION, factory.createLiteral((float)duration.get().toMillis()));
          }
        
        messageBus.publish(builder.create());
      }
    
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
    private void importMediaItemDbTuneMetadata (final @Nonnull MediaItem mediaItem, 
                                                final @Nonnull URI mediaItemUri)
      throws IOException, JAXBException, InterruptedException 
      { 
        log.info("importMediaItemDbTuneMetadata({}, {})", mediaItem, mediaItemUri);
        
        final Metadata metadata = mediaItem.getMetadata();
        final String mbGuid = metadata.get(Metadata.MBZ_TRACK_ID).get().stringValue().replaceAll("^mbz:", "");
        messageBus.publish(new AddStatementsRequest(mediaItemUri, MO.MUSICBRAINZ_GUID, literalFor(mbGuid)));
        messageBus.publish(new DownloadRequest(new URL(mediaItemUri.toString()), FOLLOW_REDIRECT));
        progress.incrementTotalDownloads();
      }
    
    /*******************************************************************************************************************
     *
     * Imports the MusicBrainz metadata for the given {@link MediaItem}.
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the URI of the item
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
    private void importMediaItemMusicBrainzMetadata (final @Nonnull MediaItem mediaItem, 
                                                     final @Nonnull URI mediaItemUri)
      throws IOException, JAXBException, InterruptedException 
      { 
        log.info("importMediaItemMusicBrainzMetadata({}, {})", mediaItem, mediaItemUri);
        
        final Metadata metadata = mediaItem.getMetadata();
        final String mbGuid = metadata.get(Metadata.MBZ_TRACK_ID).get().stringValue().replaceAll("^mbz:", "");
        messageBus.publish(new AddStatementsRequest(mediaItemUri, MO.MUSICBRAINZ_GUID, literalFor(mbGuid)));
        
        if (true)
          {
            throw new IOException("fake"); // FIXME  
          }
        
        final org.musicbrainz.ns.mmd_2.Metadata m = mbApi.getMusicBrainzEntity("recording", mbGuid, "?inc=artists");
        final Recording recording = m.getRecording();
        final String title = recording.getTitle();
        final ArtistCredit artistCredit = recording.getArtistCredit();
        final List<NameCredit> nameCredits = artistCredit.getNameCredit();
        final String fullCredits = nameCredits.stream()
                                              .map(c -> c.getArtist().getName() + emptyWhenNull(c.getJoinphrase()))
                                              .collect(joining());
        nameCredits.forEach(nameCredit -> addArtist(nameCredit.getArtist()));

        final Literal createLiteral = factory.createLiteral(title);
        messageBus.publish(AddStatementsRequest.build()
                                           .with(mediaItemUri, BM.LATEST_MB_METADATA, literalFor(timestampProvider.getInstant()))
                                           .with(mediaItemUri, DC.TITLE, createLiteral)
                                           .with(mediaItemUri, RDFS.LABEL, createLiteral)
                                           .with(mediaItemUri, BM.FULL_CREDITS, factory.createLiteral(fullCredits))
                                           .create());
        // TODO: MO.CHANNELS
        // TODO: MO.COMPOSER
        // TODO: MO.CONDUCTOR
        // TODO: MO.ENCODING "MP3 CBR @ 128kbps", "OGG @ 160kbps", "FLAC",
        // TODO: MO.GENRE
        // TODO: MO.INTERPRETER
        // TODO: MO.LABEL
        // TODO: MO.MEDIA_TYPE (MIME)
        // TODO: MO.OPUS
        // TOOD: MO.RECORD_NUMBER
        // TODO: MO.SINGER
        
// FIXME       nameCredits.forEach(credit -> addStatement(mediaItemUri, FOAF.MAKER, uriFor(credit.getArtist().getId())));
      }

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private void importFallbackMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI mediaItemUri) 
      {
        log.info("importFallbackMetadata({}, {})", mediaItem, mediaItemUri);
        
        AddStatementsRequest.Builder builder = AddStatementsRequest.build();
        final Metadata metadata = mediaItem.getMetadata();  
        final Optional<String> title = metadata.get(Metadata.TITLE);
        final Optional<String> artist = metadata.get(Metadata.ARTIST);
        
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
    
    private Set<String> seenCdNames = new HashSet<>();
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private void addArtist (final @Nonnull Artist artist)
      {
        synchronized (seenArtistIds)
          {
            final Id artistId = new Id("http://musicbrainz.org/artist/" + artist.getId());
            
            if (!seenArtistIds.contains(artistId))
              {
                seenArtistIds.add(artistId);
                progress.incrementTotalArtists();
                messageBus.publish(new ArtistImportRequest(artistId, artist));
              }
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
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Value literalFor (final Path path) 
      {
        return factory.createLiteral(path.toString());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Value literalFor (final String string) 
      {
        return factory.createLiteral(string);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Value literalFor (final int value) 
      {
        return factory.createLiteral(value);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Value literalFor (final @Nonnull Instant instant) 
      {
        return factory.createLiteral(new Date(instant.toEpochMilli()));
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private URI uriFor (final @Nonnull Id id)
      {
        return uriFor(id.stringValue());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private URI uriFor (final @Nonnull String id)
      {
        return factory.createURI(id);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String emptyWhenNull (final @Nullable String string)
      {
        return (string != null) ? string : "";  
      }
  }
