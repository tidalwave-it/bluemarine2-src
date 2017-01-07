/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.cddb.impl;

import javax.annotation.Nonnull;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import it.tidalwave.bluemarine2.rest.CachingRestClientSupport;
import it.tidalwave.bluemarine2.metadata.cddb.CddbAlbum;
import it.tidalwave.bluemarine2.metadata.cddb.CddbResponse;
import it.tidalwave.bluemarine2.metadata.cddb.CddbMetadataProvider;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata.ITunesComment;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultCddbMetadataProvider extends CachingRestClientSupport implements CddbMetadataProvider
  {
    private static final String FREEDB_URL_TEMPLATE = "http://%s/~cddb/cddb.cgi?cmd=cddb read rock %s&proto=6";

    @Getter @Setter
    private String host = "freedb.musicbrainz.org";

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public CddbResponse<CddbAlbum> findCddbAlbum (final @Nonnull ITunesComment iTunesComment)
      throws IOException
      {
        final String freeDbDiscId = iTunesComment.getFreeDbDiscId();
        final ResponseEntity<String> response = request(String.format(FREEDB_URL_TEMPLATE, host, freeDbDiscId));
        final CddbResponse<CddbAlbum> of = CddbResponse.of(response, CddbAlbum::of);

        if (of.isPresent())
          {
            final CddbAlbum album = of.get();
            final int[] requestedOffsets = iTunesComment.getTrackFrameOffsets();
            final int[] albumOffsets = album.getTrackFrameOffsets();
            boolean reject = false;

            if (!Arrays.equals(albumOffsets, requestedOffsets))
              {
                if (albumOffsets.length != requestedOffsets.length)
                  {
                    log.debug(">>>> different number of tracks: found {}, requested {}",
                              albumOffsets.length, requestedOffsets.length);
                    reject = true;
                  }
                else
                  {
                    final int delta = requestedOffsets[0] - albumOffsets[0];
                    double acc = 0;

                    for (int i = 1; i < requestedOffsets.length; i++)
                      {
                        final double x = (requestedOffsets[i] - albumOffsets[i] - delta) / (double)albumOffsets[i];
                        acc += x * x;
                      }

                    final int ppm = (int)Math.round(acc * 1E6);

                    if (ppm > 2500)
                      {
                        log.debug(">>>> found track offsets:     {}", albumOffsets);
                        log.debug(">>>> requested track offsets: {}", requestedOffsets);
                        log.debug(">>>> ppm {}", ppm);
                        reject = true;
                      }
                  }
              }

            if (reject)
              {
                log.warn("Rejected: " + album);
                return CddbResponse.empty();
              }
          }

        return of;
      }
  }
