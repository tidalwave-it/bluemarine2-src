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

import javax.annotation.Nonnull;
import java.time.Duration;
import java.io.IOException;
import java.nio.file.Path;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMp3Metadata extends MetadataSupport
  {
    @Nonnull
    private Mp3File mp3File;
    
    public DefaultMp3Metadata (final @Nonnull Path path) 
      {
        super(path);

        try
          {
            final Path aPath = path.toAbsolutePath();
            log.debug("path: {}", aPath);
            mp3File = new Mp3File(aPath.toString());
            
            put(DURATION, Duration.ofSeconds(mp3File.getLengthInSeconds()));
//            log.debug("Bitrate: " + mp3File.getBitrate()+ " kbps " + (mp3File.isVbr() ? "(VBR)" : "(CBR)"));
            put(BIT_RATE, mp3File.getBitrate());
            put(SAMPLE_RATE, mp3File.getSampleRate());
//            log.debug("Has custom tag?: " + (mp3File.hasCustomTag() ? "YES" : "NO"));

            if (mp3File.hasId3v1Tag())
              {
                final ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                put(TRACK, id3v1Tag.getTrack());
                put(ARTIST, id3v1Tag.getArtist());
                put(TITLE, id3v1Tag.getTitle());
                put(ALBUM, id3v1Tag.getAlbum());
                put(YEAR, Integer.valueOf(id3v1Tag.getYear()));
                log.debug("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
                put(COMMENT, id3v1Tag.getComment());
              }

            if (mp3File.hasId3v2Tag()) 
              {
                final ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                put(TRACK, id3v2Tag.getTrack());
                put(ARTIST, id3v2Tag.getArtist());
                put(TITLE, id3v2Tag.getTitle());
                put(ALBUM, id3v2Tag.getAlbum());
                put(YEAR, Integer.valueOf(id3v2Tag.getYear()));
                log.debug("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
                put(COMMENT, id3v2Tag.getComment());
                put(COMPOSER, id3v2Tag.getComposer());
                put(PUBLISHER, id3v2Tag.getPublisher());
                log.debug("Original artist: " + id3v2Tag.getOriginalArtist());
                log.debug("Album artist: " + id3v2Tag.getAlbumArtist());
                log.debug("Copyright: " + id3v2Tag.getCopyright());
                log.debug("URL: " + id3v2Tag.getUrl());
                log.debug("Encoder: " + id3v2Tag.getEncoder());
                final byte[] albumImageData = id3v2Tag.getAlbumImage();

                if (albumImageData != null) 
                  {
                    log.debug("Have album image data, length: " + albumImageData.length + " bytes");
                    log.debug("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
                  }
              }
          }
        catch (IOException | UnsupportedTagException | InvalidDataException e)
          {
            log.error("", e);  
          }
      }
  }
