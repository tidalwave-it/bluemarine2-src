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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.nio.file.Paths;
import it.tidalwave.util.Finder8;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import it.tidalwave.bluemarine2.commons.test.TestUtilities;
import org.testng.annotations.BeforeMethod;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PhotoCollectionProviderTestSupport extends SpringTestSupport
  {
    protected static final String URL_MOCK_RESOURCE = System.getProperty("stoppingdown",
                                                                         "file:src/test/resources/stoppingdown.net");
//            "http://localhost:8080";

    protected MediaFolder mediaFolder;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public PhotoCollectionProviderTestSupport()
      {
        super("META-INF/DciAutoBeans.xml", // required for DCI stuff
              "META-INF/MockBeans.xml");
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        // required for DCI stuff

        mediaFolder = mock(MediaFolder.class);
        when(mediaFolder.getPath()).thenReturn(Paths.get("/folder"));
        when(mediaFolder.toString()).thenReturn("Folder(\"/folder\"))");
//        when(mediaFolder.toDumpString()).thenReturn("Folder(\"/folder\"))");
        when(mediaFolder.finderOf(any(Finder8.class))).thenCallRealMethod();
        when(mediaFolder.finderOf(any(Function.class))).thenCallRealMethod();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static List<String> dump (final @Nonnull PathAwareFinder finder)
      {
        VirtualMediaFolder.EntityFinderFactory fff = t -> finder;
        final VirtualMediaFolder md = new VirtualMediaFolder(Optional.empty(), Paths.get("/"), "xxx", fff);
        return TestUtilities.dump(md);
      }
//    protected static void dumpAndAssertResults (final @Nonnull String fileName, final @Nonnull Collection<?> data)
//      throws IOException
//      {
//        TestUtilities.dumpAndAssertResults(fileName, data);
//        final Path actualResult = Paths.get("target", "test-results", fileName);
//        final Path expectedResult = Paths.get("target", "test-classes", "expected-results", fileName);
//        Files.createDirectories(actualResult.getParent());
//        final Stream<String> stream = data.stream().map(Object::toString);
//        Files.write(actualResult, (Iterable<String>)stream::iterator, StandardCharsets.UTF_8);
//        assertSameContents(expectedResult.toFile(), actualResult.toFile());
//      }

//    /*******************************************************************************************************************
//     *
//     ******************************************************************************************************************/
//    @Nonnull
//    protected static List<String> dump (final @Nonnull Entity entity)
//      {
//        final List<String> result = new ArrayList<>();
//        result.add("" + entity);
//
//        if (entity instanceof MediaFolder)
//          {
//            ((MediaFolder)entity).findChildren().forEach(child -> result.addAll(dump(child)));
//          }
//
//        return result;
//      }
  }
