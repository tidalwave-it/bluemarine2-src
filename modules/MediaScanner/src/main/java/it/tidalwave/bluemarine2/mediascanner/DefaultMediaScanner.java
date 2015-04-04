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
package it.tidalwave.bluemarine2.mediascanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.musicbrainz.ns.mmd_2.Artist;
import org.musicbrainz.ns.mmd_2.ArtistCredit;
import org.musicbrainz.ns.mmd_2.NameCredit;
import org.musicbrainz.ns.mmd_2.Recording;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.joining;
import lombok.Cleanup;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMediaScanner 
  {
    private final Queue<MediaItem> pendingMediaItems = new ConcurrentLinkedQueue<>();
    
    private final Set<Id> seenArtistIds = Collections.synchronizedSet(new HashSet<Id>());
    
    private final Queue<Id> pendingArtistsId = new ConcurrentLinkedQueue<>();
    
    private long latestMusicBrainzAccessTime = 0;
            
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
        scan(folder);
        
        // TODO: use a pool of parallel consumers
        consumePendingMediaItems();
        consumePendingArtistsId();
      }
    
    /*******************************************************************************************************************
     *
     * Scans a folder of {@link MediaItem}s.
     * 
     * @param   folder      the folder
     *
     ******************************************************************************************************************/
    public void scan (final @Nonnull MediaFolder folder)
      {
        log.info("scan({})", folder);
        
        folder.findChildren().stream().forEach(item -> 
          {
            if (item instanceof MediaFolder)
              {
                process((MediaFolder)item);  
              }
            else
              {
                pendingMediaItems.add((MediaItem)item);
              }
          });
      }
    
    /*******************************************************************************************************************
     *
     * Consume and process all the pending {@link MediaItem}s.
     *
     ******************************************************************************************************************/
    private void consumePendingMediaItems()
      {
        while (!pendingMediaItems.isEmpty())
          {
            try
              {
                processMediaItem(pendingMediaItems.remove());
              }
            catch (NoSuchElementException e)
              {
                // ok  
              } 
            catch (InterruptedException e)
              {
                log.info("Interrupted");
                return;
              }
          }
      }
    
    /*******************************************************************************************************************
     *
     * Consume and process all the pending artistIds.
     *
     ******************************************************************************************************************/
    private void consumePendingArtistsId() 
      {
        while (!pendingArtistsId.isEmpty())
          {
            try
              {
                downloadRecordingMetadata(pendingArtistsId.remove());
              }
            catch (NoSuchElementException e)
              {
                // ok  
              }
            catch (IOException | JAXBException | InterruptedException e) 
              {
                e.printStackTrace(); // FIXME
              }
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
    private void processMediaItem (final @Nonnull MediaItem mediaItem)
      throws InterruptedException
      { 
        try 
          {
            log.info("processMediaItem({})", mediaItem);

            final Metadata metadata = mediaItem.getMetadata();
            final Optional<Id> trackId = metadata.get(Metadata.MBZ_TRACK_ID);
//            final Optional<Id> workId = metadata.get(Metadata.MBZ_WORK_ID);
//            final Optional<Id> discId = metadata.get(Metadata.MBZ_DISC_ID);
            final List<Id> artistIds = metadata.getAll(Metadata.MBZ_ARTIST_ID);

            log.debug(">>>> trackId:  {}",         trackId);
    //        log.info(">>>> workId:   {}", workId);
    //        log.info(">>>> discId:   {}", discId);
            log.debug(">>>> artistId: {}",         artistIds);

            if (trackId.isPresent())
              {
                downloadRecordingMetadata(trackId.get());
              }
          }
        catch (JAXBException | MalformedURLException e) 
          {
            log.error("Failed processing {}", mediaItem);
          }
        catch (IOException e)
          {
            if (e.getMessage().contains("503")) // throttling error
              {
                log.warn("Resubmitting {} ... - {}", mediaItem, e.toString());
                pendingMediaItems.add(mediaItem);
              }
          } 
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private void addArtist (final @Nonnull Artist artist)
      {
        synchronized (seenArtistIds)
          {
            final Id artistId = new Id(artist.getId());
            
            if (!seenArtistIds.contains(artistId))
              {
                // FIXME: put Artist in a map, so you have all the info
                seenArtistIds.add(artistId);
                pendingArtistsId.add(artistId);
              }
          }
      }

    /*******************************************************************************************************************
     *
     * Downloads the metadata for a recording.
     * 
     * @param   recordingId             the id of the recording
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
    private void downloadRecordingMetadata (final @Nonnull Id recordingId)
      throws IOException, JAXBException, InterruptedException 
      { 
        log.info("downloadRecordingMetadata({})", recordingId);
        final org.musicbrainz.ns.mmd_2.Metadata m = getMusicBrainzEntity("recording", recordingId, "?inc=artists");
        final Recording recording = m.getRecording();
        final String title = recording.getTitle();
        final ArtistCredit artistCredit = recording.getArtistCredit();
        final List<NameCredit> nameCredits = artistCredit.getNameCredit();
        final String fullCredits = nameCredits.stream()
                                              .map(c -> c.getArtist().getName() + emptyWhenNull(c.getJoinphrase()))
                                              .collect(joining());
        log.info("TITLE: {} - {}", title, fullCredits);
        nameCredits.forEach(nameCredit -> addArtist(nameCredit.getArtist()));
      }
    
    /*******************************************************************************************************************
     *
     * Downloads metadata for a MusicBrainz entity.
     * 
     * @param   entityType  the entity type
     * @param   id          the entity id
     * @param   includes    the metadata to include
     * @return              the metadata
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
    @Nonnull
    private org.musicbrainz.ns.mmd_2.Metadata getMusicBrainzEntity (final @Nonnull String entityType,
                                                                    final @Nonnull Id id,
                                                                    final @Nonnull String includes) 
      throws IOException, JAXBException, InterruptedException
      {
        final JAXBContext context = JAXBContext.newInstance("org.musicbrainz.ns.mmd_2");
        final Unmarshaller u = context.createUnmarshaller();
        final String urlAsString = String.format("http://musicbrainz.org/ws/2/%s/%s%s", 
                                                 entityType,
                                                 id.stringValue().replaceAll("^mbz:", ""),
                                                 includes);
        throttle();
        final URL url = new URL(urlAsString);
        final URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-agent", "blueMarine2-1");
        @Cleanup final InputStream is = connection.getInputStream();
        return (org.musicbrainz.ns.mmd_2.Metadata)u.unmarshal(is);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void throttle() 
      throws InterruptedException 
      {
        final long now = System.currentTimeMillis();
        final long delta = 1200 - (now - latestMusicBrainzAccessTime);
        latestMusicBrainzAccessTime = now;
        
        if (delta > 0)
        {
            log.info("Throttling: waiting for {} msec...", delta);
            Thread.sleep(delta);
        }
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
