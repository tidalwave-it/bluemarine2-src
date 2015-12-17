/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.musicbrainz.impl;

import it.tidalwave.bluemarine2.musicbrainz.MusicBrainzApi;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.musicbrainz.ns.mmd_2.Metadata;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * http://code.google.com/p/musicbrainzws2-java/ is available with an unsuitable license (GPLv3).
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMusicBrainzApi implements MusicBrainzApi
  {
    private long latestMusicBrainzAccessTime = 0;
            
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Metadata getMusicBrainzEntity (final @Nonnull String entityType,
                                          final @Nonnull String entityId,
                                          final @Nonnull String includes) 
      throws IOException, JAXBException, InterruptedException
      {
        final JAXBContext context = JAXBContext.newInstance("org.musicbrainz.ns.mmd_2");
        final Unmarshaller u = context.createUnmarshaller();
        final String urlAsString = String.format("http://musicbrainz.org/ws/2/%s/%s%s", 
                                                 entityType,
                                                 entityId,
                                                 includes);
        throttle();
        final URL url = new URL(urlAsString);
        final URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-agent", "blueMarine2");
        @Cleanup final InputStream is = connection.getInputStream();
        return (org.musicbrainz.ns.mmd_2.Metadata)u.unmarshal(is);
      }

    /*******************************************************************************************************************
     *
     * See https://wiki.musicbrainz.org/XML_Web_Service/Rate_Limiting
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
  }
