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
 */package it.tidalwave.bluemarine2.mediaserver.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.role.EntityBrowser;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * The {@link Displayable} role for all {@link EntityBrowser}s.
 *
 * @stereotype  Role
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
@Immutable @DciRole(datumType = EntityBrowser.class)
public class EntityBrowserDisplayable implements Displayable
  {
    // FIXME: use a real bundle
    private final static Map<String, String> BUNDLE = new HashMap<>();

    static
      {
        BUNDLE.put("RepositoryBrowserByArtistThenRecord", "By artist & record");
        BUNDLE.put("RepositoryBrowserByArtistThenTrack", "By artist & track");
        BUNDLE.put("RepositoryBrowserByRecordThenTrack", "By record & track");
        BUNDLE.put("RepositoryBrowserByTrack", "By track");
        BUNDLE.put("DefaultMediaFileSystem", "By file");
      }

    @Nonnull
    private final EntityBrowser owner;

    @Override @Nonnull
    public String getDisplayName()
      {
        final String className = owner.getClass().getSimpleName();
        return BUNDLE.getOrDefault(className, className);
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("%s(%s)", getClass().getSimpleName(), getDisplayName());
      }
  }
