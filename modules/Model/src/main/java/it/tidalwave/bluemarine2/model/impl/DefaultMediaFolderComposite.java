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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import it.tidalwave.util.As;
import it.tidalwave.util.Finder8;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MediaFolder;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * The {@link Composite&lt;As&gt;} role for {@link MediaFolder}. It uses a {@link MediaFolderFinder}.
 * 
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = MediaFolder.class) @RequiredArgsConstructor
public class DefaultMediaFolderComposite implements SimpleComposite8<As>
  {
    private final MediaFolder mediaFolder;
    
    @Override @Nonnull
    public Finder8<As> findChildren() 
      {
        return new MediaFolderFinder(mediaFolder);
      }
  }
