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
package it.tidalwave.bluemarine2.catalog.impl.role;

import javax.annotation.Nonnull;
import it.tidalwave.util.Finder8;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.Track;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@DciRole(datumType = Record.class) @RequiredArgsConstructor
public class RecordComposite implements SimpleComposite8<Track>
  {
    @Nonnull
    private final Record record;
    
    @Override @Nonnull
    public Finder8<Track> findChildren() 
      {
        return record.findTracks();
      }
  }
