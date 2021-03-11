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
package it.tidalwave.bluemarine2.persistence.impl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.message.PowerOnNotification;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static it.tidalwave.bluemarine2.persistence.PersistencePropertyNames.*;
import static it.tidalwave.util.test.FileComparisonUtils8.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class DefaultPersistenceTest extends SpringTestSupport
  {
    private static final Path TEST_WORKSPACE = Paths.get("target/workspace");

    /** Everything happens here, and this folder is scratched at the beginning of each test. */
    private static final Path TEST_STORAGE_FOLDER = TEST_WORKSPACE.resolve("storageFolder");

    /** The original file with triples to import. */
    private static final Path TEST_IMPORT_FILE = Paths.get("target/test-classes/triples.n3");

    /** The import file is copied here, since it could be renamed. */
    private static final Path TEST_IMPORT_FILE_COPY = TEST_WORKSPACE.resolve("triples.n3");

    private static final Path TEST_TILDE_FILE = TEST_WORKSPACE.resolve("triples.n3~");

    private static final Path EMPTY_STORE = Paths.get("target/test-classes/empty.n3");

    private static final Path TEST_EXPORT_FILE = Paths.get("target/test-results/export.n3");

    private DefaultPersistence underTest;

    private MessageBus messageBus;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DefaultPersistenceTest()
      {
        super("META-INF/PersistenceTestBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      throws IOException
      {
        underTest = context.getBean(DefaultPersistence.class);
        messageBus = context.getBean(MessageBus.class);
        FileUtils.deleteDirectory(TEST_WORKSPACE.toFile());
        Files.createDirectories(TEST_WORKSPACE);
        Files.copy(TEST_IMPORT_FILE, TEST_IMPORT_FILE_COPY);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_use_MemoryStore_when_no_configuration()
      throws Exception
      {
        // given
        final PowerOnNotification powerOnNotification = new PowerOnNotification(Collections.emptyMap());
        // when
        underTest.onPowerOnNotification(powerOnNotification);
        // then
        assertThat(underTest.sail, is(instanceOf(MemoryStore.class)));
//        verify(messageBus).publish(new PersistenceInitializedNotification()); // FIXME
//        verifyNoMoreInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_use_NativeStore_when_STORAGE_FOLDER_configured()
      throws Exception
      {
        // given
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(STORAGE_FOLDER, TEST_STORAGE_FOLDER);
        final PowerOnNotification powerOnNotification = new PowerOnNotification(properties);
        // when
        underTest.onPowerOnNotification(powerOnNotification);
        // then
        assertThat(underTest.sail, is(instanceOf(NativeStore.class)));
        underTest.exportToFile(TEST_EXPORT_FILE);
        assertSameContents(EMPTY_STORE, TEST_EXPORT_FILE);
//        verify(messageBus).publish(new PersistenceInitializedNotification()); // FIXME
//        verifyNoMoreInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_use_NativeStore_when_STORAGE_FOLDER_configured_and_not_scratch()
      throws Exception
      {
        // given
        createPreviousStorageFrom(TEST_IMPORT_FILE);
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(STORAGE_FOLDER, TEST_STORAGE_FOLDER);
        final PowerOnNotification powerOnNotification = new PowerOnNotification(properties);
        // when
        underTest.onPowerOnNotification(powerOnNotification);
        // then
        assertThat(underTest.sail, is(instanceOf(NativeStore.class)));
        underTest.exportToFile(TEST_EXPORT_FILE);
        assertSameContents(TEST_IMPORT_FILE, TEST_EXPORT_FILE);
//        verify(messageBus).publish(new PersistenceInitializedNotification()); // FIXME
//        verifyNoMoreInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_use_NativeStore_and_import_when_STORAGE_FOLDER_and_IMPORT_FILE_configured()
      throws Exception
      {
        // given
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(STORAGE_FOLDER, TEST_STORAGE_FOLDER);
        properties.put(IMPORT_FILE, TEST_IMPORT_FILE_COPY);
        final PowerOnNotification powerOnNotification = new PowerOnNotification(properties);
        // when
        underTest.onPowerOnNotification(powerOnNotification);
        // then
        assertThat(underTest.sail, is(instanceOf(NativeStore.class)));
        underTest.exportToFile(TEST_EXPORT_FILE);
        assertSameContents(TEST_IMPORT_FILE, TEST_EXPORT_FILE);
        assertThat(Files.exists(TEST_IMPORT_FILE_COPY), is(true));
        assertThat(Files.exists(TEST_TILDE_FILE), is(false));
//        verify(messageBus).publish(new PersistenceInitializedNotification()); // FIXME
//        verifyNoMoreInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_use_NativeStore_and_import_when_STORAGE_FOLDER_and_IMPORT_FILE_configured_and_rename_import_file()
      throws Exception
      {
        // given
        final Map<Key<?>, Object> properties = new HashMap<>();
        properties.put(STORAGE_FOLDER, TEST_STORAGE_FOLDER);
        properties.put(IMPORT_FILE, TEST_IMPORT_FILE_COPY);
        properties.put(RENAME_IMPORT_FILE, true);
        final PowerOnNotification powerOnNotification = new PowerOnNotification(properties);
        // when
        underTest.onPowerOnNotification(powerOnNotification);
        // then
        assertThat(underTest.sail, is(instanceOf(NativeStore.class)));
        underTest.exportToFile(TEST_EXPORT_FILE);
        assertSameContents(TEST_IMPORT_FILE, TEST_EXPORT_FILE);
        assertThat(Files.exists(TEST_IMPORT_FILE_COPY), is(false));
        assertThat(Files.exists(TEST_TILDE_FILE), is(true));
//        verify(messageBus).publish(new PersistenceInitializedNotification()); // FIXME
//        verifyNoMoreInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private static void createPreviousStorageFrom (final @Nonnull Path path)
      throws Exception
      {
        final Sail sail = new NativeStore(TEST_STORAGE_FOLDER.toFile());
        final Repository repository = new SailRepository(sail);
        repository.initialize();

        try (final RepositoryConnection connection = repository.getConnection();
             final Reader reader = Files.newBufferedReader(TEST_IMPORT_FILE, UTF_8))
          {
            connection.add(reader, TEST_IMPORT_FILE.toUri().toString(), RDFFormat.N3);
            connection.commit();
          }

        repository.shutDown();
      }
  }
