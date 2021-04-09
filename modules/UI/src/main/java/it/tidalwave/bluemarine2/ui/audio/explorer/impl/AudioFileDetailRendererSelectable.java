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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.BIT_RATE;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.SAMPLE_RATE;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.YEAR;

/***********************************************************************************************************************
 *
 * The role for an {@link AudioFileSupplier} that is capable to render details upon selection, in the context of
 * {@link DefaultAudioExplorerPresentationControl}.
 *
 * @stereotype  Role
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = AudioFileSupplier.class, context = DefaultAudioExplorerPresentationControl.class)
public class AudioFileDetailRendererSelectable extends DetailRendererSelectable<AudioFileSupplier>
  {
    public AudioFileDetailRendererSelectable (final @Nonnull AudioFileSupplier audioFileSupplier)
      {
        super(audioFileSupplier);
      }

    @Override
    protected void renderDetails()
      {
        final AudioFile audioFile = this.owner.getAudioFile();
        final MediaItem.Metadata metadata = audioFile.getMetadata();

        final String details = String.format("%s%n%s%n%s%n%s%n%s",
            audioFile.findMakers().stream().map(m -> m.as(_Displayable_).getDisplayName())
                                  .collect(joining(", ", "Artist: ", "")),
            audioFile.findComposers().stream().map(e -> e.as(_Displayable_).getDisplayName())
                                     .collect(joining(", ", "Composer: ", "")),
            metadata.get(BIT_RATE).map(br -> "Bit rate: " + br + " kbps").orElse(""),
            metadata.get(SAMPLE_RATE).map(sr -> String.format("Sample rate: %.1f kHz", sr / 1000.0)).orElse(""),
            metadata.get(YEAR).map(y -> "Year: " + y).orElse(""));

        renderDetails(details);
        renderCoverArt(audioFile.getRecord().flatMap(Record::getImageUrl));
      }
  }
