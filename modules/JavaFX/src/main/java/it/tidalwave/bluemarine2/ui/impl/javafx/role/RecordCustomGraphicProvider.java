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
package it.tidalwave.bluemarine2.ui.impl.javafx.role;

import javax.annotation.Nonnull;
import javafx.scene.Node;
import it.tidalwave.ui.role.javafx.CustomGraphicProvider;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.audio.Record;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.bluemarine2.ui.impl.javafx.NodeFactory.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @DciRole(datumType = Record.class)
public class RecordCustomGraphicProvider implements CustomGraphicProvider
  {
    @Nonnull
    private final Record record;

    @Override @Nonnull
    public Node getGraphic()
      {
        return hBox("cell-container",
                    label("record-icon", ""),
                    label("record-label", record.as(_Displayable_).getDisplayName()),
                    label("record-duration", "")); // FIXME format(record.getDuration())
      }
  }
