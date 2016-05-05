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
package it.tidalwave.bluemarine2.mediaserver.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.util.Key;
import it.tidalwave.role.SimpleComposite8;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.persistence.PropertyNames;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.PropertyNames.ROOT_PATH;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectorySystemIntegrationTest
  {
    private ApplicationContext context;

    private DefaultContentDirectory underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        context = new ClassPathXmlApplicationContext("META-INF/DciAutoBeans.xml",
                                                     "META-INF/DefaultDevicePublisherTest.xml",
                                                     "classpath*:META-INF/CatalogAutoBeans.xml",
                                                     "classpath*:META-INF/CommonsAutoBeans.xml",
                                                     "classpath*:META-INF/ModelAutoBeans.xml",
                                                     "classpath*:META-INF/PersistenceAutoBeans.xml");
        underTest = context.getBean(DefaultContentDirectory.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_return_correct_root_children()
      throws Exception
      {
        // given
        final Map<Key<?>, Object> properties = new HashMap<>();
        // The file system browser is not tested here, because we can't share audio files
        final Path configPath = Paths.get("src/test/resources/config");
        final Path repositoryPath = configPath.resolve("repository.n3");
        properties.put(ROOT_PATH, configPath); // FIXME: why is this needed?
        properties.put(PropertyNames.REPOSITORY_PATH, repositoryPath);
        context.getBean(MessageBus.class).publish(new PowerOnNotification(properties));
        // when
        final MediaFolder root = underTest.findRoot();
        // then
        dumpAndAssertResults("media-server-dump.txt", dump(root));
      }

    // FIXME: copy and paste below
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected static void dumpAndAssertResults (final @Nonnull String fileName, final @Nonnull Collection<?> data)
      throws IOException
      {
        final Path actualResult = Paths.get("target", "test-results", fileName);
        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", fileName);
        Files.createDirectories(actualResult.getParent());
        final Stream<String> stream = data.stream().map(Object::toString);
        Files.write(actualResult, (Iterable<String>)stream::iterator, StandardCharsets.UTF_8);
        assertSameContents(expectedResult.toFile(), actualResult.toFile());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static List<String> dump (final @Nonnull Entity entity)
      {
        final List<String> result = new ArrayList<>();
        result.add("" + entity);

        final Optional<SimpleComposite8> asOptional = entity.asOptional(SimpleComposite8.class);
        asOptional.ifPresent(c -> c.findChildren().results().forEach(child -> result.addAll(dump((Entity)child))));

        return result;
      }

}
