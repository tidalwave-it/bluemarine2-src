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
package it.tidalwave.bluemarine2.gracenote.api.impl;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.test.FileComparisonUtils8.*;
import static it.tidalwave.bluemarine2.commons.test.TestSetLocator.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public class DefaultGracenoteApiTest
  {
    private DefaultGracenoteApi underTest;

    @BeforeMethod
    public void setup()
      {
        underTest = new DefaultGracenoteApi();
        underTest.initialize();
        underTest.setUserId(System.getProperty("gracenote.user", ""));
        underTest.setClientId(System.getProperty("gracenote.client", ""));
      }

    @Test
    public void testQueryAlbumToc()
      throws Exception
      {
        // given
        // when
        final String response = underTest.queryAlbumToc();
        final Path actualResult = dump("queryAlbumTocResponse.xml", response);
        // then
        assertSameContents(PATH_EXPECTED_TEST_RESULTS.resolve("queryAlbumTocResponse.xml"), actualResult);
      }

    @Test
    public void testQueryAlbumFetch()
      throws Exception
      {
        // given
        // when
        final String response = underTest.queryAlbumFetch();
        final Path actualResult = dump("queryAlbumFetchResponse.xml", response);
        // then
        assertSameContents(PATH_EXPECTED_TEST_RESULTS.resolve("queryAlbumFetchResponse.xml"), actualResult);
      }

    @Nonnull
    private static Path dump (final @Nonnull String resourceName, final @Nonnull String content)
      throws IOException
      {
        final Path actualResult = PATH_TEST_RESULTS.resolve(resourceName);
        Files.createDirectories(actualResult.getParent());
        Files.write(actualResult, Arrays.asList(content), UTF_8);
        return actualResult;
      }
  }
