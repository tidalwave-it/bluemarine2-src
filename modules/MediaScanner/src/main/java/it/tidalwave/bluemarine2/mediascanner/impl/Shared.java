/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.mediascanner.impl;

import java.util.Optional;
import org.eclipse.rdf4j.model.IRI;
import it.tidalwave.util.ConcurrentHashMapWithOptionals;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class Shared 
  {
    // Set would suffice, but there's no ConcurrentSet
    public final ConcurrentHashMapWithOptionals<IRI, Optional<String>> seenArtistUris = 
            new ConcurrentHashMapWithOptionals<>();
    
    public final ConcurrentHashMapWithOptionals<IRI, Boolean> seenRecordUris = new ConcurrentHashMapWithOptionals<>();
    
    /*******************************************************************************************************************
     *
     * 
     * 
     ******************************************************************************************************************/
    public void reset()
      {
        // FIXME: should load existing URIs from the Persistence
        seenArtistUris.clear();
        seenRecordUris.clear();
      }
  }
