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
package it.tidalwave.bluemarine2.model.impl.role;

import javax.annotation.Nonnull;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.impl.FileSystemAudioFile;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.TITLE;

/***********************************************************************************************************************
 *
 * The {@link Displayable} for {@link FileSystemAudioFile}; it uses the metadata title.
 *
 * @stereotype  Role
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = FileSystemAudioFile.class) @RequiredArgsConstructor
public class FileSystemAudioFileDisplayable implements Displayable
  {
    private final FileSystemAudioFile file;

    @Override @Nonnull
    public String getDisplayName()
      {
        return file.getMetadata().get(TITLE).orElse("???");
      }
  }
