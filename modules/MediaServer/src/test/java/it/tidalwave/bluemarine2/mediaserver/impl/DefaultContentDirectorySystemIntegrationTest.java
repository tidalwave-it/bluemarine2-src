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
package it.tidalwave.bluemarine2.mediaserver.impl;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.util.Key;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.util.PowerOnNotification;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.persistence.PersistencePropertyNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.commons.test.TestUtilities.*;
import static it.tidalwave.bluemarine2.model.ModelPropertyNames.ROOT_PATH;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectorySystemIntegrationTest extends SpringTestSupport
  {
    private DefaultContentDirectory underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DefaultContentDirectorySystemIntegrationTest()
      {
        super("META-INF/DciAutoBeans.xml",
              "META-INF/CommonsAutoBeans.xml",
              "META-INF/ModelAutoBeans.xml",
              "META-INF/CatalogAutoBeans.xml",
              "META-INF/PersistenceAutoBeans.xml",
              "META-INF/DefaultContentDirectoryTestBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        underTest = context.getBean(DefaultContentDirectory.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "testSetNamesProvider")
    public void must_properly_expose_data (final @Nonnull String testSetName)
      throws Exception
      {
        // given
        final Map<Key<?>, Object> properties = new HashMap<>();
        // The file system browser is not tested here, because we can't share audio files
        final Path PATH_TEST_SETS = Paths.get("target/test-classes/test-sets");
        final Path repositoryPath = PATH_TEST_SETS.resolve(testSetName + ".n3").toAbsolutePath();
        properties.put(ROOT_PATH, PATH_TEST_SETS); // FIXME: why is this needed? - mock the file system!
        properties.put(PersistencePropertyNames.IMPORT_FILE, repositoryPath);
        context.getBean(MessageBus.class).publish(new PowerOnNotification(properties));
        // when
        final MediaFolder root = underTest.findRoot();
        // then
        dumpAndAssertResults(testSetName + "-dump.txt", dump(root));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider
    private static Object[][] testSetNamesProvider()
      {
        return new Object[][]
          {
//              { "tiny-model"                    }, TODO
//              { "small-model"                   },
              { "model-iTunes-fg-20160504-2"    },
              { "model-iTunes-fg-20161210-1"    },
          };
      }
  }
