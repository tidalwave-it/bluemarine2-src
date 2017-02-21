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
package it.tidalwave.bluemarine2.ui.impl.javafx.role;

import javax.annotation.Nonnull;
import javafx.scene.Node;
import it.tidalwave.ui.role.javafx.CustomGraphicProvider;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.audio.Track;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.bluemarine2.util.Formatters.format;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.ui.impl.javafx.NodeFactory.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @DciRole(datumType = Track.class)
public class TrackCustomGraphicProvider implements CustomGraphicProvider
  {
    @Nonnull
    private final Track track;

    @Override @Nonnull
    public Node getGraphic()
      {
        final Metadata metadata = track.getMetadata();
        return hBox("cell-container",
                    label("track-icon", ""),
                    label("track-index", trackLabel(metadata)),
                    label("track-label", track.as(Displayable).getDisplayName()),
                    label("track-duration", format(metadata.get(DURATION).get())));
      }

    @Nonnull
    private static String trackLabel (final @Nonnull Metadata metadata)
      {
        final int diskCount = metadata.get(DISK_COUNT).orElse(1);
        return String.format("%s%s.",
                              metadata.get(DISK_NUMBER).map(diskNumber -> diskLabel(diskNumber, diskCount)).orElse(""),
                              metadata.get(TRACK_NUMBER).map(trackNumber -> "" + trackNumber).orElse(""));
      }

    @Nonnull
    private static String diskLabel (final int diskNumber, final int diskCount)
      {
        return (diskCount == 1) ? "" : String.format("%d.", diskNumber);
      }
  }
