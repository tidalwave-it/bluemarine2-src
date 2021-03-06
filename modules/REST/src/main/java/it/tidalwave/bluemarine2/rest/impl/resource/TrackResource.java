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
package it.tidalwave.bluemarine2.rest.impl.resource;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.audio.Track;
import lombok.Getter;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier._AudioFileSupplier_;

/***********************************************************************************************************************
 *
 * An adapter for exporting {@link Track} in REST.
 *
 * @stereotype  Adapter
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Getter
@JsonInclude(Include.NON_ABSENT)
@JsonPropertyOrder(alphabetic = true)
public class TrackResource extends ResourceSupport
  {
    private final String id;

    private final String displayName;

    private final Optional<String> record;

    private final Optional<Integer> diskCount;

    private final Optional<Integer> diskNumber;

    private final Optional<Integer> trackNumber;

    private final Optional<String> duration;

    private final Optional<String> source;

    private final Optional<String> audioFile;

    public TrackResource (@Nonnull final Track track)
      {
        this.id          = track.getId().stringValue();
        this.displayName = track.as(_Displayable_).getDisplayName();
        this.record      = track.getRecord().map(r -> resourceUri("record", r));
        this.diskCount   = track.getDiskCount();
        this.diskNumber  = track.getDiskNumber();
        this.trackNumber = track.getTrackNumber();
        this.duration    = track.getDuration().map(Duration::toString);
        this.source      = track.getSource().map(Id::toString);
        this.audioFile   = track.maybeAs(_AudioFileSupplier_).map(afs -> resourceUri("audiofile", afs.getAudioFile()));
      }
  }
