/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.ui.mainscreen.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.NodeAndDelegate;
import it.tidalwave.bluemarine2.ui.commons.flowcontroller.FlowController;
import it.tidalwave.bluemarine2.ui.mainscreen.MainScreenPresentation;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.createNodeAndDelegate;

/***********************************************************************************************************************
 *
 * The JavaFX implementation of {@link MainScreenPresentation}.
 * 
 * @stereotype  Presentation
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFxMainScreenPresentation implements MainScreenPresentation
  {
    private static final String FXML_URL = "/it/tidalwave/bluemarine2/ui/impl/javafx/MainScreen.fxml";
    
    @Inject
    private FlowController flowController;
    
    private final NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);
    
    private final MainScreenPresentation delegate = nad.getDelegate();
            
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void bind (final @Nonnull Collection<UserAction> mainMenuActions, final @Nonnull UserAction powerOffAction)
      {
        delegate.bind(mainMenuActions, powerOffAction); 
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void showUp()  
      {
        delegate.showUp();
        flowController.showPresentation(nad.getNode());
      }
  }
