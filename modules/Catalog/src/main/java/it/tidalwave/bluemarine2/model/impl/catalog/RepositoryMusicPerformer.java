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
package it.tidalwave.bluemarine2.model.impl.catalog;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.MusicPerformer;
import it.tidalwave.bluemarine2.model.vocabulary.BMMO;
import it.tidalwave.bluemarine2.model.spi.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.Repository;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 *
 **********************************************************************************************************************/
@Getter @EqualsAndHashCode @ToString
public class RepositoryMusicPerformer implements MusicPerformer
  {
    @Nonnull
    private final MusicArtist musicArtist;

    @Nonnull
    private final Optional<Entity> role;

//    @Delegate
//    private final PriorityAsSupport asSupport = new PriorityAsSupport(this);

    public RepositoryMusicPerformer (final @Nonnull Repository repository, final @Nonnull BindingSet bindingSet)
      {
        this.musicArtist = new RepositoryMusicArtist(repository, bindingSet);
        final Optional<String> r = Optional.of(bindingSet.getBinding("role").getValue().stringValue()
                                                .replaceAll(BMMO.NS + "performer_", ""));
        this.role = r.map(RepositoryMusicPerformerRole::new);
      }

    @Override
    public Id getId()
      {
        return musicArtist.getId();
      }

    // FIXME: should delegate first to its own AsDelegate and only as a fallaback to MusicArtist
    @Override
    public <T> T as(Class<T> type)
      {
        return musicArtist.as(type);
      }

    @Override
    public <T> T as(Class<T> type, NotFoundBehaviour<T> notFoundBehaviour)
      {
        return musicArtist.as(type, notFoundBehaviour);
      }

    @Override
    public <T> Collection<T> asMany(Class<T> type)
      {
        return musicArtist.asMany(type);
      }
  }
