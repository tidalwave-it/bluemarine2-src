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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Path; 
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import it.tidalwave.util.Id;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class AudioMetadata extends MetadataSupport
  {
    @Nonnull
    /* VisibleForTesting */ AudioFile audioFile;
    
    public AudioMetadata (final @Nonnull Path path) 
      {
        super(path);

        try
          {
            final Path aPath = path.toAbsolutePath();
            log.debug("path: {}", aPath);
            audioFile = AudioFileIO.read(aPath.toFile());
        
            final AudioHeader header = audioFile.getAudioHeader();
            put(DURATION, Duration.ofSeconds(header.getTrackLength()));
            put(BIT_RATE, (int)header.getBitRateAsNumber());
            put(SAMPLE_RATE, header.getSampleRateAsNumber());
            
            final Tag tag = audioFile.getTag();
            put(ARTIST, tag.getFirst(FieldKey.ARTIST));
            put(ALBUM, tag.getFirst(FieldKey.ALBUM));
            put(TITLE, tag.getFirst(FieldKey.TITLE));
            put(COMMENT, tag.getFirst(FieldKey.COMMENT));
//            put(YEAR, Integer.valueOf(tag.getFirst(FieldKey.YEAR)));
            
            try
              {
                put(TRACK, Integer.parseInt(tag.getFirst(FieldKey.TRACK)));
              }
            catch (NumberFormatException e)
              {
                log.warn("", e);  
              }
            
//            put(TRACK, tag.getFirst(FieldKey.DISC_NO));
            put(COMPOSER, tag.getFirst(FieldKey.COMPOSER));
            
            put(MBZ_TRACK_ID,  id(tag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID)));
            put(MBZ_WORK_ID,   id(tag.getFirst(FieldKey.MUSICBRAINZ_WORK_ID)));
            put(MBZ_DISC_ID,   id(tag.getFirst(FieldKey.MUSICBRAINZ_DISC_ID)));
            put(MBZ_ARTIST_ID, tag.getAll(FieldKey.MUSICBRAINZ_ARTISTID).stream()
                                  .map(s -> id(s))
                                  .filter(id -> id != null)
                                  .collect(Collectors.toList()));
            
//            tag.getFirst(FieldKey.ARTIST_SORT);

////            log.debug("Bitrate: " + mp3File.getBitrate()+ " kbps " + (mp3File.isVbr() ? "(VBR)" : "(CBR)"));
//
//            if (mp3File.hasId3v1Tag())
//              {
//                final ID3v1 id3v1Tag = mp3File.getId3v1Tag();
//                log.debug("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
//              }
//
//            if (mp3File.hasId3v2Tag()) 
//              {
//                final ID3v2 id3v2Tag = mp3File.getId3v2Tag();
//                put(PUBLISHER, id3v2Tag.getPublisher());
//                log.debug("Original artist: " + id3v2Tag.getOriginalArtist());
//                log.debug("Album artist: " + id3v2Tag.getAlbumArtist());
//                log.debug("Copyright: " + id3v2Tag.getCopyright());
//                log.debug("URL: " + id3v2Tag.getUrl());
//                log.debug("Encoder: " + id3v2Tag.getEncoder());
//                final byte[] albumImageData = id3v2Tag.getAlbumImage();
//
//                if (albumImageData != null) 
//                  {
//                    log.debug("Have album image data, length: " + albumImageData.length + " bytes");
//                    log.debug("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
//                  }
//              }
          }
        catch (IOException | CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e)
          {
            log.error("", e);  
          } 
      }
    
    @CheckForNull
    private static Id id (final @Nullable String string)
      {
        if ((string == null) || "".equals(string))
          {
            return null;  
          }
        
        return new Id("mbz:" + string);
      }
  }
