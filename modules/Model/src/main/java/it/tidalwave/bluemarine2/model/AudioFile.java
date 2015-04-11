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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.time.Duration;
import it.tidalwave.util.Finder8;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface AudioFile extends MediaItem // FIXME: MediaItem should not be statically Parentable
  {
    @Nonnull
    public Optional<String> getTitle();
    
    @Nonnull
    public Optional<Duration> getDuration();
    
    @Nonnull
    public Finder8<? extends Entity> findMakers();

    // FIXME: should't be here: this should become getSignal().findMakers() or something like that
    @Nonnull
    public Finder8<? extends Entity> findComposers();

    // FIXME: should't be here: this should become getRecord().getRecord() or something like that
    // FIXME: and it should be Optional<Record>
    public Optional<? extends Entity> getRecord();
  }
