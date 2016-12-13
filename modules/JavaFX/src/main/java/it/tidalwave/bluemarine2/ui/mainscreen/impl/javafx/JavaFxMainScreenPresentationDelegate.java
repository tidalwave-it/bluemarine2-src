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
package it.tidalwave.bluemarine2.ui.mainscreen.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.bluemarine2.ui.mainscreen.MainScreenPresentation;

/***********************************************************************************************************************
 *
 * The JavaFX Delegate for {@link MainScreenPresentation}.
 *
 * @stereotype  JavaFXDelegate
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class JavaFxMainScreenPresentationDelegate implements MainScreenPresentation
  {
    @FXML
    private GridPane gpMainMenuBar;

    @FXML
    private Button btPowerOff;

    @Inject
    private JavaFXBinder binder;

    @Override
    public void bind (final @Nonnull Collection<UserAction> mainMenuActions, final @Nonnull UserAction powerOffAction)
      {
        binder.bindButtonsInPane(gpMainMenuBar, mainMenuActions);
        binder.bind(btPowerOff, powerOffAction);
      }

    @Override
    public void showUp()
      {
      }
  }
