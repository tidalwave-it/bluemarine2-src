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
import javax.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import it.tidalwave.util.Id;
import it.tidalwave.util.InstantProvider;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.vocabulary.BM;
import it.tidalwave.bluemarine2.model.vocabulary.MO;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.mediascanner.impl.Utilities.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.ITUNES_COMMENT;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.ITunesComment;
import it.tidalwave.util.Key;
import java.util.List;

/***********************************************************************************************************************
 *
 *
 * mo:AudioFile
 *      IRI                                                     computed from the fingerprint
 *      foaf:sha1           the fingerprint of the file         locally computed
 *      bm:latestInd.Time   the latest import time              locally computed
 *      bm:path             the path of the file                locally computed
 *      dc:title            the title                           locally computed    WRONG: USELESS?
 *      rdfs:label          the display name                    locally computed    WRONG: should be the file name without path?
 *      mo:encodes          points to the signal                locally computed
 *
 * mo:DigitalSignal
 *      IRI                                                     computed from the fingerprint
 *      mo:bitsPerSample    the bits per sample                 locally extracted from the file
 *      mo:duration         the duration                        locally extracted from the file
 *      mo:sample_rate      the sample rate                     locally extracted from the file
 *      mo:published_as     points to the Track                 locally computed
 *      MISSING mo:channels
 *      MISSING? mo:time
 *      MISSING? mo:trmid
 *
 * mo:Track
 *      IRI                                                     the DbTune one if available, else computed from SHA1
 *      mo:musicbrainz      the MusicBrainz IRI                 locally extracted from the file
 *      dc:title            the title                           taken from DbTune
 *      rdfs:label          the display name                    taken from DbTune
 *      foaf:maker          points to the MusicArtist           taken from DbTune
 *      mo:track_number     the track number in the record      taken from DbTune
 *
 * mo:Record
 *      IRI                                                     taken from DbTune
 *      dc:date
 *      dc:language
 *      dc:title            the title                           taken from DbTune
 *      rdfs:label          the display name                    taken from DbTune
 *      mo:release          TODO points to the Label (EMI, etc...)
 *      mo:musicbrainz      the MusicBrainz IRI                 locally extracted from the file
 *      mo:track            points to the Tracks                taken from DbTune
 *      foaf:maker          points to the MusicArtist           taken from DbTune
 *      owl:sameAs          point to external resources         taken from DbTune
 *
 * mo:Artist
 *
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultMediaScanner
  {
//    @Inject // FIXME: refactor Db Tune support in a different class
//    private DbTuneMetadataManager dbTuneMetadataManager;

    @Inject
    private EmbeddedMetadataManager embeddedMetadataManager;

    @Inject
    private ProgressHandler progress;

    @Inject
    private MessageBus messageBus;

    @Inject
    private StatementManager statementManager;

    @Inject
    private InstantProvider timestampProvider;

    @Inject
    private IdCreator idCreator;

    @Inject
    private Shared shared;

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
        shared.reset();
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
        catch (Exception e)
          {
            log.error("", e);
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
     * Processes a {@link MediaItem}.
     *
     * @param                           audioFile   the item
     *
     ******************************************************************************************************************/
    private void importMediaItem (final @Nonnull MediaItem audioFile)
      {
        log.debug("importMediaItem({})", audioFile);
        final Id sha1 = idCreator.createSha1Id(audioFile.getPath());
        final Metadata metadata = audioFile.getMetadata();

        final IRI audioFileUri = BM.audioFileUriFor(sha1);
        // FIXME: DbTune has got Signals. E.g. http://dbtune.org/musicbrainz/page/signal/0900f0cb-230f-4632-bd87-650801e5fdba
        // FIXME: Try to use them. It seems there is no extra information, but use their Uri.
        final IRI signalUri = BM.signalUriFor(sha1);
        final IRI trackUri = createTrackUri(metadata, sha1);

        final Instant lastModifiedTime = getLastModifiedTime(audioFile.getPath());
        statementManager.requestAddStatements()
                        .with(audioFileUri,     RDF.TYPE,                MO.C_AUDIO_FILE)
                        .with(audioFileUri,     FOAF.SHA1,               literalFor(sha1))
                        .with(audioFileUri,     MO.P_ENCODES,            signalUri)
                        .with(audioFileUri,     BM.PATH,                 literalFor(audioFile.getRelativePath()))
                        .with(audioFileUri,     BM.LATEST_INDEXING_TIME, literalFor(lastModifiedTime))

                        .with(        trackUri, RDF.TYPE,                MO.C_TRACK)
                        .withOptional(trackUri, BM.ITUNES_CDDB1,       literalFor(metadata.get(ITUNES_COMMENT)
                                                                                            .map(c -> c.getTrackId())))

                        .with(signalUri,        RDF.TYPE,                MO.C_DIGITAL_SIGNAL)
                        .with(signalUri,        MO.P_PUBLISHED_AS,       trackUri)
                        .publish();

        embeddedMetadataManager.importAudioFileMetadata(audioFile, signalUri, trackUri);

        // FIXME: use a Chain of Responsibility
//        if (musicBrainzTrackId.isPresent())
//          {
//            dbTuneMetadataManager.importTrackMetadata(audioFile, trackUri, musicBrainzTrackId.get());
////                importMediaItemMusicBrainzMetadata(mediaItem, mediaItemUri);
//          }
//        else
//          {
            embeddedMetadataManager.importFallbackTrackMetadata(audioFile, trackUri);
//          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private IRI createTrackUri (final @Nonnull Metadata metadata, final @Nonnull Id sha1)
      {
        // FIXME: use a chain of responsibility
        final Optional<Id> musicBrainzTrackId = metadata.get(Metadata.MBZ_TRACK_ID);
        log.debug(">>>> musicBrainzTrackId: {}", musicBrainzTrackId);
        // FIXME: the same contents in different places will give the same sha1. Disambiguates by hashing the path too?
        return !musicBrainzTrackId.isPresent() ? BM.localTrackUriFor(sha1)
                                               : BM.musicBrainzUriFor("track", musicBrainzTrackId.get());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Instant getLastModifiedTime (final @Nonnull Path path)
      {
        try
          {
            return Files.getLastModifiedTime(path).toInstant();
          }
        catch (IOException e) // should never happen
          {
            log.warn("Cannot get last modified time for {}: assuming now", path);
            return timestampProvider.getInstant();
          }
      }
  }
