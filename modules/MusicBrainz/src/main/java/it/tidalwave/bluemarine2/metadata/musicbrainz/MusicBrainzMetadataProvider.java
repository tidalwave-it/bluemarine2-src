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
package it.tidalwave.bluemarine2.metadata.musicbrainz;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.io.IOException;
import org.musicbrainz.ns.mmd_2.Metadata;
import org.musicbrainz.ns.mmd_2.Recording;
import org.musicbrainz.ns.mmd_2.Release;
import org.musicbrainz.ns.mmd_2.ReleaseGroupList;
import org.musicbrainz.ns.mmd_2.ReleaseList;
import it.tidalwave.bluemarine2.rest.RestResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public interface MusicBrainzMetadataProvider
  {
    @RequiredArgsConstructor @Getter @ToString(of = "name")
    public static class ResourceType<TYPE>
      {
        @Nonnull
        private final String name;

        @Nonnull
        private final Function<Metadata, TYPE> resultProvider;
      }

    public static final ResourceType<Release> RELEASE = new ResourceType<>("release", Metadata::getRelease);

    public static final ResourceType<Recording> RECORDING = new ResourceType<>("recording", Metadata::getRecording);

    public static final ResourceType<ReleaseList> DISC_ID = new ResourceType<>("discId", Metadata::getReleaseList);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public RestResponse<ReleaseGroupList> findReleaseGroupByTitle (@Nonnull String title, @Nonnull String ... includes)
      throws IOException, InterruptedException;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public RestResponse<ReleaseList> findReleaseListByToc (@Nonnull String toc, @Nonnull String ... includes)
      throws IOException, InterruptedException;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public <T> RestResponse<T> getResource (@Nonnull ResourceType<T> resourceType,
                                            @Nonnull String id,
                                            @Nonnull String ... includes)
      throws IOException, InterruptedException;
  }
