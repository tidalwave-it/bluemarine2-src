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
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.tidalwave.bluemarine2.model.audio.Record;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * An adapter for exporting {@link Record} in REST.
 * FIXME: differentiating the serialized fields should be done with JsonView
 *
 * @stereotype  Adapter
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Getter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder(alphabetic = true)
public class DetailedRecordResource extends RecordResource
  {
    private final List<TrackResource> tracks;

    public DetailedRecordResource (@Nonnull final Record record, @Nonnull final List<TrackResource> tracks)
      {
        super(record);
        this.details = null;
        this.tracks  = tracks;
      }
  }
