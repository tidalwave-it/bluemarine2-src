/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.impl.MapBindingSet;
import it.tidalwave.util.Id;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.audio.MusicArtist;
import it.tidalwave.bluemarine2.model.audio.Record;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.model.spi.EntityWithRoles;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.impl.PathAwareMediaFolderDecorator;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryMusicArtist;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryRecord;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Identifiable.Identifiable;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter.DIDLAdapter;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DIDLAdapterBindingTest extends SpringTestSupport
  {
    public DIDLAdapterBindingTest()
      {
        super("META-INF/DciAutoBeans.xml",
              "META-INF/CompositeDIDLAdapterSupportTestBeans.xml");
      }

    @Test
    public void must_find_the_correct_adapter_for_MusicArtist()
      {
        must_find_the_correct_adapter(createMockArtist(), MusicArtistDIDLAdapter.class);
      }

    @Test
    public void must_find_the_correct_adapter_for_decorated_MusicArtist()
      {
        must_find_the_correct_adapter_for_decorated_entities(createMockArtist(), PathAwareDecoratorDIDLAdapter.class);
      }

    @Test
    public void must_find_the_correct_adapter_for_Record()
      {
        must_find_the_correct_adapter(createMockRecord(), RecordDIDLAdapter.class);
      }

    @Test
    public void must_find_the_correct_adapter_for_decorated_Record()
      {
        must_find_the_correct_adapter_for_decorated_entities(createMockRecord(), PathAwareDecoratorDIDLAdapter.class);
      }

    // TODO: test for Track

    @Test
    public void must_find_the_correct_adapter_for_Entity()
      {
        must_find_the_correct_adapter(createMockEntity(), EntityDIDLAdapter.class);
      }

    @Test
    public void must_find_the_correct_adapter_for_decorated_Entity()
      {
        must_find_the_correct_adapter_for_decorated_entities(createMockEntity(), PathAwareDecoratorDIDLAdapter.class);
      }

    private void must_find_the_correct_adapter_for_decorated_entities (final @Nonnull Entity datum,
                                                                       final @Nonnull Class<?> expectedAdapterClass)
      {
        // given
        final PathAwareEntity parent = mock(PathAwareEntity.class);
        when(parent.getPath()).thenReturn(Paths.get("/"));
        final Path pathSegment = Paths.get(datum.as(Identifiable).getId().stringValue());
        final PathAwareMediaFolderDecorator decorator = new PathAwareMediaFolderDecorator(datum, parent, pathSegment);
        // when
        final DIDLAdapter adapter = decorator.as(DIDLAdapter);
        // then
        assertThat(adapter, instanceOf(expectedAdapterClass));
      }

    private void must_find_the_correct_adapter (final @Nonnull Entity datum,
                                                final @Nonnull Class<?> expectedAdapterClass)
      {
        // when
        final DIDLAdapter adapter = datum.as(DIDLAdapter);
        // then
        assertThat(adapter, instanceOf(expectedAdapterClass));
      }

    @Nonnull
    private MusicArtist createMockArtist()
      {
        final Repository repository = mock(Repository.class);
        final MapBindingSet bindingSet = new MapBindingSet();
        final Id artistId = new Id("urn:bluemarine:artist:john_doe");
        bindingSet.addBinding("artist", literalFor(artistId));
        bindingSet.addBinding("label", literalFor("John Doe"));
        bindingSet.addBinding("artist_type", literalFor(1));
        return new RepositoryMusicArtist(repository, bindingSet);
      }

    @Nonnull
    private Record createMockRecord()
      {
        final Repository repository = mock(Repository.class);
        final MapBindingSet bindingSet = new MapBindingSet();
        final Id recordId = new Id("urn:bluemarine:record:the_record");
        bindingSet.addBinding("record", literalFor(recordId));
        bindingSet.addBinding("label", literalFor("The record"));
        return new RepositoryRecord(repository, bindingSet);
      }

    @Nonnull
    private Entity createMockEntity()
      {
        return new EntityWithRoles((Identifiable) () -> new Id("urn:bluemarine:something:foo_bar"));
      }
  }
