/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.musicbrainz.ns.mmd_2.Metadata;
import org.musicbrainz.ns.mmd_2.ReleaseGroupList;
import org.musicbrainz.ns.mmd_2.ReleaseList;
import it.tidalwave.bluemarine2.metadata.musicbrainz.MusicBrainzMetadataProvider;
import it.tidalwave.bluemarine2.rest.CachingRestClientSupport;
import it.tidalwave.bluemarine2.rest.RestResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.bluemarine2.metadata.cddb.impl.MusicBrainzUtilities.escape;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMusicBrainzMetadataProvider extends CachingRestClientSupport implements MusicBrainzMetadataProvider
  {
    private static final String URL_RELEASE_GROUP = "http://%s/ws/2/release-group/?query=release:%s";

    private static final String URL_DISCID = "http://%s/ws/2/discid/?toc=%s%s";

    private static final String URL_RESOURCE = "http://%s/ws/2/%s/%s%s";

    @Getter @Setter
    private String host = "musicbrainz.org";

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public DefaultMusicBrainzMetadataProvider()
      {
        // sometimes MusicBrainz returns HTTP 500, but it's usually transitory
        setRetryStatusCodes(List.of(500, 503));
        setMaxRetry(10);
        setThrottleLimit(1500);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RestResponse<ReleaseGroupList> findReleaseGroupByTitle (@Nonnull final String title,
                                                                   @Nonnull final String ... includes)
      throws IOException, InterruptedException
      {
        log.debug("findReleaseGroupByTitle({})", title);
        final ResponseEntity<String> response = request(String.format(URL_RELEASE_GROUP, host, escape(title)));
        return MusicBrainzResponse.of(response, Metadata::getReleaseGroupList);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RestResponse<ReleaseList> findReleaseListByToc (@Nonnull final String toc,
                                                           @Nonnull final String ... includes)
      throws IOException, InterruptedException
      {
        log.debug("findReleaseListByToc({}, {})", toc, includes);
        final ResponseEntity<String> response = request(String.format(URL_DISCID, host, toc, includesToString("&", includes)));
        return MusicBrainzResponse.of(response, Metadata::getReleaseList);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <T> RestResponse<T> getResource (@Nonnull final ResourceType<T> resourceType,
                                            @Nonnull final String id,
                                            @Nonnull final String ... includes)
      throws IOException, InterruptedException
      {
        log.debug("getResource({}. {}, {})", resourceType, id, includes);
        final String url = String.format(URL_RESOURCE, host, resourceType.getName(), id, includesToString("?", includes));
        return MusicBrainzResponse.of(request(url), resourceType.getResultProvider());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String includesToString (@Nonnull final String separator, @Nonnull final String ... includes)
      {
        return Stream.of(includes).collect(joining("%2b", separator + "inc=", ""));
      }
  }
