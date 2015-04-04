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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import javax.xml.bind.JAXBException;
import org.musicbrainz.ns.mmd_2.Artist;
import org.musicbrainz.ns.mmd_2.ArtistCredit;
import org.musicbrainz.ns.mmd_2.NameCredit;
import org.musicbrainz.ns.mmd_2.Recording;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.impl.ValueFactoryImpl;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.vocabulary.BM;
import it.tidalwave.bluemarine2.vocabulary.MO;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import static java.util.stream.Collectors.joining;
import lombok.Cleanup;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultMediaScanner 
  {
    @Nonnull
    private final Model model;
    
    private final Queue<MediaItem> pendingMediaItems = new ConcurrentLinkedQueue<>();
    
    private final Set<Id> seenArtistIds = Collections.synchronizedSet(new HashSet<Id>());
    
    private final Map<Id, Artist> pendingArtists = new ConcurrentHashMap<>();
    
    // FIXME: inject
    private final DefaultMusicBrainzApi mbApi = new DefaultMusicBrainzApi();
    
    // FIXME: inject
    private final ValueFactory factory = ValueFactoryImpl.getInstance();
    
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
                importMediaItem(pendingMediaItems.remove());
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
        while (!pendingArtists.isEmpty())
          {
            try
              {
                final Id artistId = pendingArtists.keySet().iterator().next();
                processArtist(artistId, pendingArtists.remove(artistId));
              }
            catch (NoSuchElementException e)
              {
                // ok  
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
    private void importMediaItem (final @Nonnull MediaItem mediaItem)
      throws InterruptedException
      { 
        log.info("importMediaItem({})", mediaItem);
        
        final URI mediaItemUri = uriFor(createMd5Id(mediaItem.getPath()));
        
        try 
          {
            addStatement(mediaItemUri, RDF.TYPE, MO.TRACK);
            addStatement(mediaItemUri, MO.AUDIOFILE, literalFor(mediaItem.getRelativePath()));
            addStatement(mediaItemUri, BM.LATEST_INDEXING_TIME, literalFor(mediaItem.getPath().toFile().lastModified()));

            importMediaItemMetadata(mediaItem, mediaItemUri);
            
            final Metadata metadata = mediaItem.getMetadata();
            final Optional<Id> musicBrainzTrackId = metadata.get(Metadata.MBZ_TRACK_ID);

            log.debug(">>>> musicBrainzTrackId: {}", musicBrainzTrackId);
//            log.debug(">>>> artistId: {}",         artistIds);
            
            if (musicBrainzTrackId.isPresent())
              {
                importMediaItemMusicBrainzMetadata(mediaItem, mediaItemUri);
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
            if (e.getMessage().contains("503")) // throttling error
              {
                log.warn("Cannot retrieve MusicBrainz metadata {} ... - {}", mediaItem, e.toString());
                addStatement(mediaItemUri, BM.FAILED_MB_METADATA, factory.createLiteral(new Date()));
//                log.warn("Resubmitting {} ... - {}", mediaItem, e.toString());
//                pendingMediaItems.add(mediaItem);
              }
          } 
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void processArtist (final @Nonnull Id artistId, final @Nonnull Artist artist)
      {
        log.info("processArtist({}, {})", artistId, artist);
        final Resource artistUri = uriFor("http://musicmusicbrainz.org/ws/2/artist/" + artistId);
        addStatement(artistUri, RDF.TYPE, MO.MUSIC_ARTIST);
        addStatement(artistUri, FOAF.NAME, literalFor(artist.getName()));
        addStatement(artistUri, MO.MUSICBRAINZ_GUID, artistUri);
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

        if (sampleRate.isPresent())
          {
            addStatement(mediaItemUri, MO.SAMPLE_RATE, factory.createLiteral(sampleRate.get()));
          }

        if (sampleRate.isPresent() && bitRate.isPresent())
          {
            addStatement(mediaItemUri, MO.BITS_PER_SAMPLE, factory.createLiteral(sampleRate.get() * bitRate.get()));
          }

        if (trackNumber.isPresent())
          {
            addStatement(mediaItemUri, MO.TRACK_NUMBER, factory.createLiteral(trackNumber.get()));
          }

        if (duration.isPresent())
          {
            addStatement(mediaItemUri, MO.DURATION, factory.createLiteral(duration.get().toMillis()));
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
    private void importMediaItemMusicBrainzMetadata (final @Nonnull MediaItem mediaItem, 
                                                     final @Nonnull URI mediaItemUri)
      throws IOException, JAXBException, InterruptedException 
      { 
        log.info("importMediaItemMusicBrainzMetadata({}, {})", mediaItem, mediaItemUri);
        
        final Metadata metadata = mediaItem.getMetadata();
        final String mbGuid = metadata.get(Metadata.MBZ_TRACK_ID).get().stringValue().replaceAll("^mbz:", "");
        addStatement(mediaItemUri, MO.MUSICBRAINZ_GUID, literalFor(mbGuid));
        
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

        addStatement(mediaItemUri, BM.LATEST_MB_METADATA, literalFor(System.currentTimeMillis()));
        addStatement(mediaItemUri, DC.TITLE, factory.createLiteral(title));
        addStatement(mediaItemUri, BM.FULL_CREDITS, factory.createLiteral(fullCredits));
        
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
        
        nameCredits.forEach(credit -> addStatement(mediaItemUri, FOAF.MAKER, uriFor(credit.getArtist().getId())));
      }

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    private void importFallbackMetadata (final @Nonnull MediaItem mediaItem, final @Nonnull URI mediaItemUri) 
      {
        log.info("importFallbackMetadata({}, {})", mediaItem, mediaItemUri);
        
        final Metadata metadata = mediaItem.getMetadata();
        addStatement(mediaItemUri, DC.TITLE, literalFor(metadata.get(Metadata.TITLE).get()));
        addStatement(mediaItemUri, BM.MISSED_MB_METADATA, literalFor(System.currentTimeMillis()));
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
            final Id artistId = new Id("http://musicbrainz.org/artist/" + artist.getId());
            
            if (!seenArtistIds.contains(artistId))
              {
                seenArtistIds.add(artistId);
                pendingArtists.putIfAbsent(artistId, artist);
              }
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Id createMd5Id (final @Nonnull Path path)
      {
        try 
          {
            final File file = path.toFile();
            final String algorithm = "MD5";
            final @Cleanup RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            final MappedByteBuffer byteBuffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file.length());
            final MessageDigest digestComputer = MessageDigest.getInstance(algorithm);
            digestComputer.update(byteBuffer);
            randomAccessFile.close();
            return new Id("md5id:" + toString(digestComputer.digest()));
          } 
        catch (NoSuchAlgorithmException | IOException e) 
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void addStatement (final @Nonnull Resource subject, 
                               final @Nonnull URI predicate, 
                               final @Nonnull Value object) 
      {
        model.add(factory.createStatement(subject, predicate, object));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String toString (final @Nonnull byte[] bytes)
      {
        final StringBuilder builder = new StringBuilder();

        for (final byte b : bytes)
          {
            final int value = b & 0xff;
            builder.append(Integer.toHexString(value >>> 4)).append(Integer.toHexString(value & 0x0f));
          }

        return builder.toString();
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
    private Value literalFor (final long date) 
      {
        return factory.createLiteral(new Date(date));
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
