/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.mediaserver;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;
import it.tidalwave.util.As;
import it.tidalwave.util.Finder;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.ArrayListFinder;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.role.Identifiable;
import it.tidalwave.role.spi.DefaultDisplayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DefaultContentDirectory implements ContentDirectory
  {
    @Override @Nonnull
    public Finder<As> findObjects()
      {
        final As music = createObject("Music Library");
        final As photo = createObject("Photo Library");
        final As video = createObject("Video Library");
        final As services = createObject("Services");

        return new ArrayListFinder<>(Arrays.asList(music, photo, video, services));
      }

    private As createObject (final @Nonnull String displayName)
      {
        return new AsSupport(null,
            (Identifiable) () -> new Id(UUID.randomUUID().toString()),
            new DefaultDisplayable(displayName)
        );
      }
  }
