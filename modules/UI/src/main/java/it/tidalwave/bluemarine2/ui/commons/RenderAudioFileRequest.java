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
package it.tidalwave.bluemarine2.ui.commons;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.PlayList;
import lombok.ToString;
import static java.util.Collections.*;

/***********************************************************************************************************************
 *
 * A message that requests to render an {@link AudioFile} in a {@link Playlist}.
 *
 * @stereotype  Message
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @ToString
public class RenderAudioFileRequest
  {
    @Nonnull
    private final AudioFile audioFile;

    @Nonnull
    private final List<AudioFile> list;

    public RenderAudioFileRequest (final @Nonnull AudioFile audioFile)
      {
        this(audioFile, emptyList());
      }

    public RenderAudioFileRequest (final @Nonnull AudioFile audioFile, final @Nonnull List<AudioFile> list)
      {
        this.audioFile = audioFile;
        this.list = unmodifiableList(new ArrayList<>(list));
      }

    @Nonnull
    public PlayList<AudioFile> getPlayList()
      {
        return new PlayList(audioFile, list);
      }
  }
