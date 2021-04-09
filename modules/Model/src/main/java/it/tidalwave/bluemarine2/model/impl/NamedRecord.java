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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.net.URL;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.finder.audio.TrackFinder;
import it.tidalwave.bluemarine2.model.spi.NamedEntity;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class NamedRecord extends NamedEntity implements Record
  {
    public NamedRecord (@Nonnull final String displayName)
      {
        super(displayName);
      }

    @Override @Nonnull
    public TrackFinder findTracks()
      {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: return empty finder
      }

    @Override @Nonnull
    public Optional<URL> getImageUrl()
      {
        return Optional.empty();
      }

    @Override @Nonnull
    public Id getId()
      {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME
      }

    @Override @Nonnull
    public Optional<Id> getSource()
      {
        return Optional.empty();
      }

    @Override @Nonnull
    public Optional<String> getAsin()
      {
        return Optional.empty();
      }

    @Override @Nonnull
    public Optional<String> getGtin()
      {
        return Optional.empty();
      }

    @Override @Nonnull
    public Optional<Integer> getDiskNumber()
      {
        return Optional.empty();
      }

    @Override @Nonnull
    public Optional<Integer> getDiskCount()
      {
        return Optional.empty();
      }

    @Override @Nonnull
    public Optional<Integer> getTrackCount()
      {
        return Optional.empty();
      }
  }
