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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import it.tidalwave.bluemarine2.commons.test.TestSetLocator;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class FileSystemTest
  {
    @Test
    public void test_filesystem_consistency()
      throws IOException
      {
        final Path tmpDir = Paths.get("/tmp");
        final Path parent = tmpDir.resolve("test");
        final Path file1 = parent.resolve(new String(new byte[] { 65, (byte)0314, (byte)0201 }));
        final Path file2 = parent.resolve(new String(new byte[] { 65, (byte)0303, (byte)0251 }));

        Runtime.getRuntime().exec("/bin/rm -r " + parent.toAbsolutePath().toString());
        Files.createDirectory(parent);
        Runtime.getRuntime().exec("/bin/cp /etc/hosts " + file1.toAbsolutePath().toString());
        Runtime.getRuntime().exec("/bin/cp /etc/hosts " + file2.toAbsolutePath().toString());

        Files.walk(parent).filter(Files::isRegularFile).forEach(path ->
          {
            System.err.println("FILE: " + path.toString());
            System.err.println("Files.exists(): " + Files.exists(path));
            System.err.println("toFile().exists(): " + path.toFile().exists());

            try
              {
                Files.readAllLines(path);
                System.err.println("Can be opened");
              }
            catch (IOException e)
              {
                System.err.println("Can't be opened");
              }
          });
//        Files.createFile(file1);
//        Files.createFile(file2);
      }

    /**
     * With a wrong encoding, on EXT4 some files can't be ever accessed.
     */
    @Test(dataProvider = "pathProvider")
    public void test_that_all_files_are_accessible (final @Nonnull Path path)
      throws Exception
      {
        tryToOpen(path);
      }

    @Test(dataProvider = "pathProviderIconv")
    public void test_that_all_files_are_accessible_iconv (final @Nonnull Path path)
      throws Exception
      {
        tryToOpen(path);
      }

    @Test(dataProvider = "pathProviderNoIconv")
    public void test_that_all_files_are_accessible_noiconv (final @Nonnull Path path)
      throws Exception
      {
        tryToOpen(path);
      }

    private void tryToOpen (final Path path)
      throws IOException
      {
        assertThat("Files.exists() " + toDebugString(path.toString()), Files.exists(path), is(true));
        assertThat("toFile.exists() " + toDebugString(path.toString()), path.toFile().exists(), is(true));
//
//        try (final InputStream is = Files.newInputStream(path))
//          {
//          }
      }

    @DataProvider
    private static Object[][] pathProvider()
      throws IOException
      {
        return pathProviderFor("iTunes-fg-20160504-1");
      }

    @DataProvider
    private static Object[][] pathProviderIconv()
      throws IOException
      {
        return pathProviderFor("iTunes-fg-20160504-1-iconv");
      }

    @DataProvider
    private static Object[][] pathProviderNoIconv()
      throws IOException
      {
        return pathProviderFor("iTunes-fg-20160504-1-noiconv");
      }

    private static Object[][] pathProviderFor (final @Nonnull String testSetName)
      throws IOException
      {
        final Path testSetPath = TestSetLocator.getMusicTestSetsPath().resolve(testSetName);
        return Files.walk(testSetPath, FOLLOW_LINKS)
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> !path.getFileName().toString().startsWith(".")) // isHidden() throws exception
                    .map(path -> new Object[] { path })
                    .collect(toList())
                    .toArray(new Object[0][0]);
      }

    @Nonnull
    private String toDebugString (final @Nonnull String string)
      {
        final StringBuilder buffer = new StringBuilder();
        final byte[] bytes = string.getBytes();

        for (int i = 0; i < bytes.length; i++)
          {
            final int b = bytes[i] & 0xff;

            if ((b >= 32) && (b <= 127))
              {
                buffer.append((char)b);
              }
            else
              {
                buffer.append("_" + Integer.toHexString(b).toUpperCase());
              }
          }

        return buffer.toString();
      }
  }
