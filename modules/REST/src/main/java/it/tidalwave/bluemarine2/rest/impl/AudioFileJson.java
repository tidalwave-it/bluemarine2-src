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
import java.util.Optional;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import lombok.Getter;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

/***********************************************************************************************************************
 *
 * An adapter for exporting {@link AudioFile} in JSON.
 *
 * @stereotype  Adapter
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Getter
@JsonInclude(Include.NON_ABSENT)
@JsonPropertyOrder(alphabetic = true)
public class AudioFileJson extends JsonSupport
  {
    @JsonView(Profile.Master.class)
    private final String id;

    @JsonView(Profile.Master.class)
    private final String displayName;

    @JsonView(Profile.Master.class)
    private final String path;

    @JsonView(Profile.Master.class)
    private final Optional<Long> fileSize;

    @JsonView(Profile.Master.class)
    private final Optional<String> duration;

    @JsonView(Profile.Master.class)
    private final String content;

    @JsonView(Profile.Master.class)
    private final Optional<String> coverArt;

    public AudioFileJson (final @Nonnull AudioFile audioFile)
      {
        final Metadata metadata = audioFile.getMetadata();
        this.id          = audioFile.getId().stringValue();
        this.displayName = audioFile.as(Displayable).getDisplayName();
        this.path        = audioFile.getPath().toString();
        this.fileSize    = metadata.get(FILE_SIZE);
        this.duration    = metadata.get(DURATION).map(Duration::toString);

        final String myUri = resourceUri("audiofile", id);
        this.content     = myUri + "/content";
        this.coverArt    = metadata.get(ARTWORK).map(x -> myUri + "/coverart");
      }
  }
