/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.rest.impl.resource;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import lombok.Getter;
import static it.tidalwave.role.ui.Displayable.Displayable;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;

/***********************************************************************************************************************
 *
 * An adapter for exporting {@link AudioFile} in REST.
 *
 * @stereotype  Adapter
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Getter
@JsonInclude(Include.NON_ABSENT)
@JsonPropertyOrder(alphabetic = true)
public class AudioFileResource extends ResourceSupport
  {
    private final String id;

    private final String displayName;

    private final String path;

    private final Optional<Long> fileSize;

    private final Optional<String> duration;

    private final String content;

    private final Optional<String> coverArt;

    public AudioFileResource (final @Nonnull AudioFile audioFile)
      {
        this.id          = audioFile.getId().stringValue();
        this.displayName = audioFile.as(Displayable).getDisplayName();
        this.path        = audioFile.getPath().toString();

        final Metadata metadata = audioFile.getMetadata();
        this.fileSize    = metadata.get(FILE_SIZE);
        this.duration    = metadata.get(DURATION).map(Duration::toString);

        final String myUri = resourceUri("audiofile", id);
        this.content     = myUri + "/content";
        this.coverArt    = metadata.get(ARTWORK).map(x -> myUri + "/coverart");
      }
  }
