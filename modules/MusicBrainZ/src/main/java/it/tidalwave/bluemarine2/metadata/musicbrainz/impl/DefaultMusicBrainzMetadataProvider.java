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
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import java.net.URLEncoder;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import it.tidalwave.bluemarine2.metadata.musicbrainz.MusicBrainzMetadataProvider;
import it.tidalwave.bluemarine2.rest.CachingRestClientSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMusicBrainzMetadataProvider extends CachingRestClientSupport implements MusicBrainzMetadataProvider
  {
    private static final String URL_TEMPLATE = "http://%s/ws/2/release-group/?query=release:%s";

    @Getter @Setter
    private String host = "musicbrainz.org";

    public void findReleaseGroup (final @Nonnull String title)
      throws IOException, InterruptedException
      {
        log.info("findReleaseGroup({})", title);
        final ResponseEntity<String> response = request(String.format(URL_TEMPLATE, host, URLEncoder.encode(title, "UTF-8")));
      }
  }
