/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import it.tidalwave.bluemarine2.model.Track;
import lombok.Getter;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Getter
@JsonInclude(Include.NON_ABSENT)
public class TrackJson
  {
    @JsonView(Profile.Master.class)
    private final String id;

    @JsonView(Profile.Master.class)
    private final String displayName;

    @JsonView(Profile.Master.class)
    private final Optional<Integer> diskCount;

    @JsonView(Profile.Master.class)
    private final Optional<Integer> diskNumber;

    @JsonView(Profile.Master.class)
    private final Optional<Integer> trackNumber;

    @JsonView(Profile.Master.class)
    private final Optional<String> duration;

    public TrackJson (final @Nonnull Track track)
      {
        this.id          = track.getId().stringValue();
        this.displayName = track.as(Displayable).getDisplayName();
        this.diskCount   = track.getDiskCount();
        this.diskNumber  = track.getDiskNumber();
        this.trackNumber = track.getTrackNumber();
        this.duration    = track.getDuration().map(Duration::toString);
      }
  }
