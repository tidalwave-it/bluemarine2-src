/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import it.tidalwave.ui.role.javafx.CustomGraphicProvider;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MusicArtist;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @DciRole(datumType = MusicArtist.class) 
public class MusicArtistCustomGraphicProvider implements CustomGraphicProvider
  {
    @Nonnull
    private final MusicArtist artist;
    
    @Override @Nonnull
    public Node getGraphic() 
      {
        final Label lbIcon = new Label("");
        lbIcon.setMinWidth(30); // FIXME
        lbIcon.getStyleClass().setAll("list-cell", "artist-icon"); // TODO: use artist-group-icon for groups
        final Label lbName = new Label(artist.as(Displayable).getDisplayName());
        lbName.setPrefWidth(9999); // FIXME
        lbName.getStyleClass().setAll("list-cell", "artist-label");
        final HBox hBox = new HBox(lbIcon, lbName);
        hBox.setAlignment(Pos.BASELINE_LEFT);
        hBox.setPrefWidth(500); // FIXME
        return hBox;
      }
  }
