/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaItem;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.role.AudioFileSupplier;
import static java.util.stream.Collectors.joining;
import static it.tidalwave.role.Displayable.Displayable;
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
 * @version $Id$
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
        
        final String details = String.format("%s\n%s\n%s\n%s\n%s",
            audioFile.findMakers().stream().map(m -> m.as(Displayable).getDisplayName())
                                  .collect(joining(", ", "Artist: ", "")),
            audioFile.findComposers().stream().map(e -> e.as(Displayable).getDisplayName())
                                     .collect(joining(", ", "Composer: ", "")),
            metadata.get(BIT_RATE).map(br -> "Bit rate: " + br + " kbps").orElse(""),
            metadata.get(SAMPLE_RATE).map(sr -> String.format("Sample rate: %.1f kHz", sr / 1000.0)).orElse(""),
            metadata.get(YEAR).map(y -> "Year: " + y).orElse(""));
        
        renderDetails(details);
        renderCoverArt(audioFile.getRecord().flatMap(Record::getImageUrl));
      }
  }
