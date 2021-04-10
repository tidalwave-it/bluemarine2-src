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
package it.tidalwave.bluemarine2.mediaserver.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.rdf4j.repository.Repository;
import it.tidalwave.bluemarine2.model.MediaFolder;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import static it.tidalwave.bluemarine2.commons.test.TestUtilities.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectorySystemIntegrationTest extends SpringTestSupport
  {
    private static final Path PATH_TEST_SETS = Paths.get("target/test-classes/test-sets");

    private DefaultContentDirectory underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DefaultContentDirectorySystemIntegrationTest()
      {
        super("META-INF/DciAutoBeans.xml",
              "META-INF/CommonsAutoBeans.xml",
              "META-INF/CatalogAutoBeans.xml",
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
    public void must_properly_expose_data (@Nonnull final String testSetName)
      throws Exception
      {
        // given
        // The file system browser is not tested here, because we can't share audio files
        final Path repositoryPath = PATH_TEST_SETS.resolve(testSetName + ".n3").toAbsolutePath();
        final Repository repository = context.getBean(Repository.class);
        repository.initialize();
        loadRepository(repository, repositoryPath);
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
