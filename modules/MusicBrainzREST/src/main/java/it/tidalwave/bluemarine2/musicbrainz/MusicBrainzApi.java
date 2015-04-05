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
package it.tidalwave.bluemarine2.musicbrainz;

import javax.annotation.Nonnull;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.musicbrainz.ns.mmd_2.Metadata;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface MusicBrainzApi 
  {
    /*******************************************************************************************************************
     *
     * Downloads metadata for a MusicBrainz entity.
     * 
     * @param   entityType  the entity type
     * @param   entityId    the entity id
     * @param   includes    the metadata to include
     * @return              the metadata
     * @throws  IOException             when an I/O problem occurred
     * @throws  JAXBException           when an XML error occurs
     * @throws  InterruptedException    if the operation is interrupted
     *
     ******************************************************************************************************************/
    @Nonnull
    public Metadata getMusicBrainzEntity (final @Nonnull String entityType,
                                          final @Nonnull String entityId,
                                          final @Nonnull String includes) 
      throws IOException, JAXBException, InterruptedException;
  }
