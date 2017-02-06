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
import java.util.Collection;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.Record;
import lombok.Getter;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * An adapter for exporting {@link Record} in JSON.
 *
 * @stereotype  Adapter
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Getter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder(alphabetic = true)
public class RecordJson extends JsonSupport
  {
    private final String id;

    private final String displayName;

    private final Optional<Integer> diskCount;

    private final Optional<Integer> diskNumber;

    private final Optional<Integer> trackCount;

    private final Optional<String> source;

    private final Collection<String> tracks;

    public RecordJson (final @Nonnull Record record)
      {
        this.id          = record.getId().stringValue();
        this.displayName = record.as(Displayable).getDisplayName();
        this.diskCount   = record.getDiskCount();
        this.diskNumber  = record.getDiskNumber();
        this.trackCount  = record.getTrackCount();
        this.source      = record.getSource().map(Id::toString);
        this.tracks      = record.findTracks().stream().map(track -> resourceUri("track", track)).collect(toList());
      }
  }
