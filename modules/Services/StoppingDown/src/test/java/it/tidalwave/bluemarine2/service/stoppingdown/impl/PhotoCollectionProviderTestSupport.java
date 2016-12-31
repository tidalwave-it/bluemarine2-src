/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.bluemarine2.model.role.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import org.testng.annotations.BeforeMethod;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PhotoCollectionProviderTestSupport
  {
    protected static final String URL_MOCK_RESOURCE = System.getProperty("stoppingdown",
                                                                         "file:src/test/resources/stoppingdown.net");
//            "http://localhost:8080";

    protected ApplicationContext context;

    protected MediaFolder mediaFolder;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        // required for DCI stuff
        context = new ClassPathXmlApplicationContext("META-INF/DciAutoBeans.xml");

        mediaFolder = mock(MediaFolder.class);
        when(mediaFolder.getPath()).thenReturn(Paths.get("/folder"));
        when(mediaFolder.toString()).thenReturn("MediaFolder(\"/folder\"))");
      }

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

        if (entity instanceof MediaFolder)
          {
            ((MediaFolder)entity).findChildren().forEach(child -> result.addAll(dump(child)));
          }

        return result;
      }
  }
