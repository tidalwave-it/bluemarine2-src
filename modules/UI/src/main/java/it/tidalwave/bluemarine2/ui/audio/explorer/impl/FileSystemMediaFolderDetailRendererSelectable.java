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
package it.tidalwave.bluemarine2.ui.audio.explorer.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.impl.FileSystemMediaFolder;

/***********************************************************************************************************************
 *
 * The role for an {@link FileSystemMediaFolder} that is capable to render details upon selection, in the context of
 * {@link DefaultAudioExplorerPresentationControl}.
 * 
 * FIXME: doesn't work
 * 
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = FileSystemMediaFolder.class, context = DefaultAudioExplorerPresentationControl.class)
public class FileSystemMediaFolderDetailRendererSelectable extends DetailRendererSelectable<FileSystemMediaFolder>
  {
    public FileSystemMediaFolderDetailRendererSelectable (final @Nonnull FileSystemMediaFolder folder) 
      {
        super(folder);
      }
    
    @Override
    protected void renderDetails() 
      {
        renderDetails("");
        renderCoverArt(Optional.empty());
      }
  }