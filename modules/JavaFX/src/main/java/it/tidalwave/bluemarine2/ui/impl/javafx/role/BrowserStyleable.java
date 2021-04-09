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
import java.util.List;
import java.util.Collection;
import it.tidalwave.role.ui.Styleable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.impl.catalog.browser.RepositoryBrowserSupport;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = RepositoryBrowserSupport.class) @RequiredArgsConstructor
public class BrowserStyleable implements Styleable
  {
    private final RepositoryBrowserSupport owner;
    
    @Override @Nonnull
    public Collection<String> getStyles() 
      {
        return List.of(owner.getClass().getSimpleName());
      }
  }
