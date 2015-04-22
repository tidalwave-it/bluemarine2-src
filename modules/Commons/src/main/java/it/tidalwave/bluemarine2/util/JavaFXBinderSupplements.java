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
package it.tidalwave.bluemarine2.util;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import static java.util.stream.Collectors.*;
import static java.util.Collections.*;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;
import static it.tidalwave.role.ui.Styleable.Styleable;
import static it.tidalwave.role.ui.UserActionProvider.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class JavaFXBinderSupplements 
  {
    @Inject
    private JavaFXBinder binder;
    
    /*
     * FIXME: move to SteelBlue
     * The pane must be pre-populated with at least one button, which will be queried for the CSS style.
    */
    public void bindToggleButtons (final @Nonnull Pane pane, final @Nonnull PresentationModel pm)
      {
        final ToggleGroup group = new ToggleGroup();
        final ObservableList<Node> children = pane.getChildren();
        final ObservableList<String> prototypeStyleClass = children.get(0).getStyleClass();
        final SimpleComposite<PresentationModel> pmc = pm.as(SimpleComposite);
        children.setAll(pmc.findChildren().results().stream()
                                                    .map(cpm -> createButton(cpm, prototypeStyleClass, group))
                                                    .collect(toList()));
      }
    
    @Nonnull
    private ToggleButton createButton (final @Nonnull PresentationModel pm,
                                       final @Nonnull List<String> baseStyleClass,
                                       final @Nonnull ToggleGroup group)
      {
        final ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setText(asOptional(pm, Displayable).map(d -> d.getDisplayName()).orElse(""));
        button.getStyleClass().addAll(baseStyleClass);
        button.getStyleClass().addAll(asOptional(pm, Styleable).map(s -> s.getStyles()).orElse(emptyList()));
        
        try 
          {
            binder.bind(button, pm.as(UserActionProvider).getDefaultAction());
          }
        catch (NotFoundException e)
          {
            // ok, no UserActionProvider
          }

        if (group.getSelectedToggle() == null)
          {
            group.selectToggle(button);
          }
        
        return button;
      }
    
    @Nonnull
    private static <T> Optional<T> asOptional (final @Nonnull As asObject, final Class<T> roleClass)
      {
        // can't use asOptional() since PresentationModel is constrained to Java 7
        // FIXME The shortest implementation doesn't work - see DefaultPresentationModel implementation of as()
        // It doesn't call as() with NotFoundBehaviour - it's probably a bug
//        return Optional.ofNullable(asObject.as(roleClass, throwable -> null));
          try
            {
              return Optional.of(asObject.as(roleClass));  
            }
          catch (AsException e)
            {
              return Optional.empty();
            }
      }

  }
