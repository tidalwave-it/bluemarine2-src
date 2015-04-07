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
import java.time.Instant;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;
import org.musicbrainz.ns.mmd_2.Artist;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
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
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMediaScanner 
  {
//    // FIXME: inject
//    private final DefaultMusicBrainzApi mbApi = new DefaultMusicBrainzApi();
    
    @Inject
    private DbTuneMetadataManager dbTuneMetadataManager;
    
    @Inject
    private EmbeddedMetadataManager embeddedMetadataManager;
    
    @Inject
    private Progress progress;

    @Inject
    private MessageBus messageBus;
    
    @Inject
    private InstantProvider timestampProvider;
    
    @Inject
    private Md5IdCreator md5IdCreator;

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
    /* VisibleForTesting */ void onInternalMediaFolderScanRequest 
                                    (final @ListensTo @Nonnull InternalMediaFolderScanRequest request)
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
                final String url = message.getUrl().toString();

                if (url.matches("http://dbtune.org/.*/resource/track/.*"))
                  {
                    dbTuneMetadataManager.onTrackMetadataDownloadComplete(message);
                  }
                else if (url.matches("http://dbtune.org/.*/resource/artist/.*"))
                  {
                    dbTuneMetadataManager.onArtistMetadataDownloadComplete(message);
                  }
                else if (url.matches("http://dbtune.org/.*/resource/record/.*"))
                  {
                    dbTuneMetadataManager.onRecordMetadataDownloadComplete(message);
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
            embeddedMetadataManager.importMediaItemEmbeddedMetadata(mediaItem, mediaItemUri);
            
//            log.debug(">>>> artistId: {}",         artistIds);
            
            if (musicBrainzTrackId.isPresent())
              {
                dbTuneMetadataManager.importTrackMetadata(mediaItem, mediaItemUri);
//                importMediaItemMusicBrainzMetadata(mediaItem, mediaItemUri);
              }
            else
              {
                embeddedMetadataManager.importFallbackEmbeddedMetadata(mediaItem, mediaItemUri);
              } 
          }
        catch (JAXBException | MalformedURLException e) 
          {
            log.error("Failed processing {}", mediaItem);
          }
        catch (IOException e)
          {
            log.warn("Cannot retrieve MusicBrainz metadata {} ... - {}", mediaItem, e.toString());
            embeddedMetadataManager.importFallbackEmbeddedMetadata(mediaItem, mediaItemUri);
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
     * Imports the MusicBrainz metadata for the given {@link MediaItem}.
     * 
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the URI of the item
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
//    private void importMediaItemMusicBrainzMetadata (final @Nonnull MediaItem mediaItem, 
//                                                     final @Nonnull URI mediaItemUri)
//      throws IOException, JAXBException, InterruptedException 
//      { 
//        log.info("importMediaItemMusicBrainzMetadata({}, {})", mediaItem, mediaItemUri);
//        
//        final Metadata metadata = mediaItem.getMetadata();
//        final String mbGuid = metadata.get(Metadata.MBZ_TRACK_ID).get().stringValue().replaceAll("^mbz:", "");
//        messageBus.publish(new AddStatementsRequest(mediaItemUri, MO.MUSICBRAINZ_GUID, literalFor(mbGuid)));
//        
//        if (true)
//          {
//            throw new IOException("fake"); // FIXME  
//          }
//        
//        final org.musicbrainz.ns.mmd_2.Metadata m = mbApi.getMusicBrainzEntity("recording", mbGuid, "?inc=artists");
//        final Recording recording = m.getRecording();
//        final String title = recording.getTitle();
//        final ArtistCredit artistCredit = recording.getArtistCredit();
//        final List<NameCredit> nameCredits = artistCredit.getNameCredit();
//        final String fullCredits = nameCredits.stream()
//                                              .map(c -> c.getArtist().getName() + emptyWhenNull(c.getJoinphrase()))
//                                              .collect(joining());
//        nameCredits.forEach(nameCredit -> addArtist(nameCredit.getArtist()));
//
//        final Value createLiteral = literalFor(title);
//        messageBus.publish(AddStatementsRequest.build()
//                                           .with(mediaItemUri, BM.LATEST_MB_METADATA, literalFor(timestampProvider.getInstant()))
//                                           .with(mediaItemUri, DC.TITLE, createLiteral)
//                                           .with(mediaItemUri, RDFS.LABEL, createLiteral)
//                                           .with(mediaItemUri, BM.FULL_CREDITS, literalFor(fullCredits))
//                                           .create());
//        // TODO: MO.CHANNELS
//        // TODO: MO.COMPOSER
//        // TODO: MO.CONDUCTOR
//        // TODO: MO.ENCODING "MP3 CBR @ 128kbps", "OGG @ 160kbps", "FLAC",
//        // TODO: MO.GENRE
//        // TODO: MO.INTERPRETER
//        // TODO: MO.LABEL
//        // TODO: MO.MEDIA_TYPE (MIME)
//        // TODO: MO.OPUS
//        // TOOD: MO.RECORD_NUMBER
//        // TODO: MO.SINGER
//        
//// FIXME       nameCredits.forEach(credit -> addStatement(mediaItemUri, FOAF.MAKER, uriFor(credit.getArtist().getId())));
//      }

    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
//    private void addArtist (final @Nonnull Artist artist)
//      {
//        synchronized (seenArtistIds)
//          {
//            final Id artistId = new Id("http://musicbrainz.org/artist/" + artist.getId());
//            
//            if (!seenArtistIds.contains(artistId))
//              {
//                seenArtistIds.add(artistId);
//                progress.incrementTotalArtists();
//                messageBus.publish(new ArtistImportRequest(artistId, artist));
//              }
//          }
//      }
  }
