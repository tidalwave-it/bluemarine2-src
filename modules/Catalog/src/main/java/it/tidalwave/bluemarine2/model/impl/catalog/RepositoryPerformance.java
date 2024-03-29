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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.bluemarine2.model.audio.Performance;
import it.tidalwave.bluemarine2.model.finder.audio.MusicPerformerFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class RepositoryPerformance extends RepositoryEntitySupport implements Performance
  {
    public RepositoryPerformance (@Nonnull final Repository repository, @Nonnull final BindingSet bindingSet)
      {
        super(repository, bindingSet, "performance");
      }

    @Override @Nonnull
    public MusicPerformerFinder findPerformers()
      {
        return _findPerformers().performerOf(this);
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("RepositoryPerformance(rdfs:label=%s, uri=%s)", rdfsLabel, id);
      }

    @Override @Nonnull
    public String toDumpString()
      {
        return String.format("Performance(uri=%s)", id);
      }
  }
