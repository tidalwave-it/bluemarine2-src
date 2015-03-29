/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.ui.roles;

import javax.annotation.Nonnull;
import it.tidalwave.util.As;
import it.tidalwave.role.ui.Presentable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.spi.DefaultPresentationModel;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.MediaItem;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * The {@link Presentable} role for a {@link MediaFolder} or a {@link MediaItem}. It creates a standard
 * {@link DefaultPresentationModel}
 * 
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = { MediaFolder.class, MediaItem.class }) @RequiredArgsConstructor
public class MediaPresentable implements Presentable // FIXME: use DefaultPresentable
  {
    @Nonnull
    private final As mediaFolder;
    
    @Override @Nonnull
    public PresentationModel createPresentationModel (final @Nonnull Object... instanceRoles) 
      {
        return new DefaultPresentationModel(mediaFolder, instanceRoles);
      }
  }
//public class MediaPresentable extends DefaultPresentable
//  {
//    public MediaPresentable (final @Nonnull Object owner) 
//      {
//        super(owner);
//      }
//  }
