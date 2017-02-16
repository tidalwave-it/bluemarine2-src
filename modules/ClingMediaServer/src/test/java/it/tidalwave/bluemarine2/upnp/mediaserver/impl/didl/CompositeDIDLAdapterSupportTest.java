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
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.query.impl.MapBindingSet;
import org.fourthline.cling.support.model.DIDLObject;
import it.tidalwave.util.As8;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.model.MusicArtist;
import it.tidalwave.bluemarine2.model.Record;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.impl.PathAwareMediaFolderDecorator;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryMusicArtist;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryRecord;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import lombok.extern.slf4j.Slf4j;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static it.tidalwave.bluemarine2.util.RdfUtilities.*;
import static it.tidalwave.role.Identifiable.Identifiable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class CompositeDIDLAdapterSupportTest extends SpringTestSupport
  {
    static class UnderTest extends CompositeDIDLAdapterSupport
      {
        public UnderTest (final @Nonnull As8 datum, final @Nonnull ResourceServer server)
          {
            super(datum, server);
          }

        @Override @Nonnull
        public DIDLObject toObject()
          {
            throw new UnsupportedOperationException("Not supported yet.");
          }
      }

    public CompositeDIDLAdapterSupportTest()
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

    private void must_find_the_correct_adapter_for_decorated_entities (
        final @Nonnull Entity instance, final @Nonnull Class<?> expectedRoleClass)
      {
        // given
        final PathAwareEntity parent = mock(PathAwareEntity.class);
        when(parent.getPath()).thenReturn(Paths.get("/"));
        final PathAwareMediaFolderDecorator decorator = new PathAwareMediaFolderDecorator(
                instance, parent, Paths.get(instance.as(Identifiable).getId().stringValue()));

        final ResourceServer server = mock(ResourceServer.class);
        final As8 datum = mock(As8.class);
        final UnderTest underTest = new UnderTest(datum, server);
        // when
        final DIDLAdapter adapter = underTest.asDIDLAdapter(decorator);
        // then
        assertTrue(expectedRoleClass.isAssignableFrom(adapter.getClass()), "" + adapter);
      }

    private void must_find_the_correct_adapter (
        final @Nonnull Entity instance, final @Nonnull Class<?> expectedRoleClass)
      {
        // given
        final ResourceServer server = mock(ResourceServer.class);
        final As8 datum = mock(As8.class);

        final UnderTest underTest = new UnderTest(datum, server);
        // when
        final DIDLAdapter adapter = underTest.asDIDLAdapter(instance);
        // then
        assertTrue(expectedRoleClass.isAssignableFrom(adapter.getClass()), "" + adapter);
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
  }
