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
package it.tidalwave.bluemarine2.ui.audio.explorer;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.net.URI;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.bluemarine2.model.spi.Entity;
import lombok.Getter;
import lombok.experimental.Accessors;

/***********************************************************************************************************************
 *
 * The Presentation of the explorer of audio media files.
 *
 * @stereotype  Presentation
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface AudioExplorerPresentation
  {
    @Getter @Accessors(fluent = true)
    public static class Properties
      {
        private final StringProperty folderNameProperty = new SimpleStringProperty("");
      }

    /*******************************************************************************************************************
     *
     * Binds the UI with the callbacks.
     *
     * @param   upAction    the action to go to the upper folder
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull Properties properties, @Nonnull UserAction upAction);

    /*******************************************************************************************************************
     *
     * Shows this presentation on the screen.
     *
     ******************************************************************************************************************/
    public void showUp (@Nonnull Object control);

    /*******************************************************************************************************************
     *
     * Populates the presentation with the available media browsers.
     *
     * @param   pm                  the {@link PresentationModel}
     *
     ******************************************************************************************************************/
    public void populateBrowsers (@Nonnull PresentationModel pm);

    /*******************************************************************************************************************
     *
     * Populates the presentation with a set of items and optionally restores some visual properties.
     *
     * @param   pm                  the {@link PresentationModel}
     * @param   optionalMemento     the container of properties
     *
     ******************************************************************************************************************/
    public void populateItems (@Nonnull PresentationModel pm, @Nonnull Optional<Object> optionalMemento);

    /*******************************************************************************************************************
     *
     * Renders some details about an {@link Entity}.
     *
     * @param   entityDetails       the details
     *
     ******************************************************************************************************************/
    public void renderDetails (@Nonnull String entityDetails);

    /*******************************************************************************************************************
     *
     * Puts the focus on the list to select media items.
     *
     ******************************************************************************************************************/
    public void focusOnMediaItems();

    /*******************************************************************************************************************
     *
     * Returns an object containing the snapshot of some relevant visual properties.
     *
     * @return      the memento object
     *
     ******************************************************************************************************************/
    @Nonnull
    public Object getMemento();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setCoverArt (@Nonnull Optional<URI> optionalCoverArtUri);
  }
