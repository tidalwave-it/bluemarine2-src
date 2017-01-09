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
import it.tidalwave.bluemarine2.rest.CachingRestClientSupport;
import it.tidalwave.bluemarine2.rest.RestResponse;
import it.tidalwave.bluemarine2.metadata.cddb.CddbAlbum;
import it.tidalwave.bluemarine2.metadata.cddb.CddbMetadataProvider;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.util.FunctionWrappers.*;

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
    public RestResponse<CddbAlbum> findCddbAlbum (final @Nonnull Metadata metadata)
      throws IOException, InterruptedException
      {
        return metadata.get(CDDB).map(_f(this::findCddbAlbum)).orElse(CddbResponse.empty());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private RestResponse<CddbAlbum> findCddbAlbum (final @Nonnull Cddb cddb)
      throws IOException, InterruptedException
      {
        final String discId = cddb.getDiscId();
        final ResponseEntity<String> response = request(String.format(FREEDB_URL_TEMPLATE, host, discId));
        return CddbResponse.of(response, CddbAlbum::of);
      }
  }
