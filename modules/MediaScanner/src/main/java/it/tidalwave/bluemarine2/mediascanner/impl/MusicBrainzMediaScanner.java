/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
import java.io.IOException;
import org.musicbrainz.ns.mmd_2.Artist;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import it.tidalwave.util.Id;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.bluemarine2.model.vocabulary.MO;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;

/***********************************************************************************************************************
 *
 * NOT CURRENTLY USED
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class MusicBrainzMediaScanner
  {
//    // FIXME: inject
//    private final DefaultMusicBrainzApi mbApi = new DefaultMusicBrainzApi();

    @Inject
    private ProgressHandler progress;

    @Inject
    private StatementManager statementManager;

    @Inject
    private DbTuneMetadataManager dbTuneMetadataManager;

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
            statementManager.requestAddStatements()
                            .with(artistUri, RDF.TYPE, MO.C_MUSIC_ARTIST)
                            .with(artistUri, RDFS.LABEL, nameLiteral)
                            .with(artistUri, FOAF.NAME, nameLiteral)
                            .with(artistUri, MO.P_MUSICBRAINZ_GUID, literalFor(artistId.stringValue()))
                            .publish();
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
        finally
          {
            progress.incrementCompletedDownloads();
          }
      }

    /*******************************************************************************************************************
     *
     * Imports the MusicBrainz metadata for the given {@link MediaItem}.
     *
     * @param   mediaItem               the {@code MediaItem}.
     * @param   mediaItemUri            the IRI of the item
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
//    private void importMediaItemMusicBrainzMetadata (final @Nonnull MediaItem mediaItem,
//                                                     final @Nonnull IRI mediaItemUri)
//      throws IOException, JAXBException, InterruptedException
//      {
//        log.info("importMediaItemMusicBrainzMetadata({}, {})", mediaItem, mediaItemUri);
//
//        final Metadata metadata = mediaItem.getMetadata();
//        final String mbGuid = metadata.get(Metadata.MBZ_TRACK_ID).get().stringValue().replaceAll("^mbz:", "");
//        messageBus.publish(new AddStatementsRequest(mediaItemUri, MO.P_MUSICBRAINZ_GUID, literalFor(mbGuid)));
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
//        messageBus.publish(AddStatementsRequest.newAddStatementsRequest()
//                                           .withOptional(mediaItemUri, BM.LATEST_MB_METADATA, literalFor(timestampProvider.getInstant()))
//                                           .withOptional(mediaItemUri, DC.TITLE, createLiteral)
//                                           .withOptional(mediaItemUri, RDFS.LABEL, createLiteral)
//                                           .withOptional(mediaItemUri, BM.FULL_CREDITS, literalFor(fullCredits))
//                                           .publish());
//        // TODO: MO.CHANNELS
//        // TODO: MO.COMPOSER
//        // TODO: MO.CONDUCTOR
//        // TODO: MO.ENCODING "MP3 CBR @ 128kbps", "OGG @ 160kbps", "FLAC",
//        // TODO: MO.GENRE
//        // TODO: MO.INTERPRETER
//        // TODO: MO.LABEL
//        // TODO: MO.P_MEDIA_TYPE (MIME)
//        // TODO: MO.OPUS
//        // TOOD: MO.RECORD_NUMBER
//        // TODO: MO.SINGER
//
//// FIXME       nameCredits.forEach(credit -> addStatement(mediaItemUri, FOAF.MAKER, uriFor(credit.getArtist().getId())));
//      }

//        catch (IOException e)
//          {
//            log.warn("Cannot retrieve MusicBrainz metadata {} ... - {}", mediaItem, e.toString());
//            embeddedMetadataManager.importFallbackTrackMetadata(mediaItem, mediaItemUri);
//            messageBus.publish(new AddStatementsRequest(mediaItemUri, BM.FAILED_MB_METADATA, literalFor(timestampProvider.getInstant())));
//
//            if (e.getMessage().contains("503")) // throttling error
//              {
////                log.warn("Resubmitting {} ... - {}", mediaItem, e.toString());
////                pendingMediaItems.requestAddStatements(mediaItem);
//              }
//          }

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
//                seenArtistIds.requestAddStatements(artistId);
//                progress.incrementTotalArtists();
//                messageBus.publish(new ArtistImportRequest(artistId, artist));
//              }
//          }
//      }
  }
