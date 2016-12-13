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
package it.tidalwave.bluemarine2.util;

import javax.annotation.Nonnull;
import java.text.Normalizer;
import java.nio.file.InvalidPathException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static it.tidalwave.bluemarine2.util.BMT46Workaround.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.testng.annotations.DataProvider;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class BMT46WorkaroundTest
  {
    @Test(dataProvider = "stringsProvider", enabled = false)
    public void test_encoding (final @Nonnull String expected, final @Nonnull String actual)
      {
        log.info(">>>> expected: {}", expected);
        log.info(">>>> actual:   {}", actual);
        log.info(">>>> expected: {}", toDebugString(expected));
        log.info(">>>> actual:   {}", toDebugString(actual));

        for (final Normalizer.Form form : Normalizer.Form.values())
          {
            try
              {
                log.info(">>>> normalized with {}: {}", form, Normalizer.normalize(actual, form));
              }
            catch (InvalidPathException e2)
              {
                log.warn(">>>> failed path normalisation with {}", form);
              }
          }

//        assertThat(normalized(actual), is(expected)); FIXME
      }

    @DataProvider
    private Object[][] testSetProvider()
      {
        return new Object[][]
          {
            { "iTunes-fg-20160504-1"         },
            { "iTunes-fg-20160504-1-noiconv" },
            { "iTunes-fg-20160504-1-iconv"   }
          };
      }

    @Test(dataProvider = "testSetProvider")
    public void test_walk_diacritics (final @Nonnull String testSetName)
      throws IOException
      {
        final String testSetFolder = System.getProperty("blueMarine2.musicTestSets.path");
        final Path parent = Paths.get(testSetFolder, testSetName, "Music", "Ivo Pogorelich/Ravel_ Gaspard De La Nuit; Prokofiev_ Piano Sonata #6");

        if (!Files.exists(parent))
          {
            log.warn("Doesn't exist: {}", parent);
          }
        else
          {
            final AtomicBoolean walked = new AtomicBoolean();
            Files.walk(parent).filter(Files::isRegularFile).forEach(path ->
              {
                walked.set(true);
                final String s =  parent.relativize(path).toString();
                log.info(">>>> found: {}", s);
                log.info(">>>> found: {}", toDebugString(s));
              });

            assertThat(walked.get(), is(true));
          }
      }

    @Test(dataProvider = "testSetProvider")
    public void test_walk_diacritics2 (final @Nonnull String testSetName)
      throws IOException
      {
        final String testSetFolder = System.getProperty("blueMarine2.musicTestSets.path");
        final Path parent = Paths.get(testSetFolder, testSetName, "Music", "Ivo Pogorelich/Ravel_ Gaspard De La Nuit; Prokofiev_ Piano Sonata #6");

        if (!Files.exists(parent))
          {
            log.warn("Doesn't exist: {}", parent);
          }
        else
          {
            final AtomicBoolean walked = new AtomicBoolean();
            Files.list(parent).filter(Files::isRegularFile).forEach(path ->
              {
                walked.set(true);
                final String s =  parent.relativize(path).toString();
                log.info(">>>> found: {}", s);
                log.info(">>>> found: {}", toDebugString(s));
              });

            assertThat(walked.get(), is(true));
          }
      }

    @Test(dataProvider = "testSetProvider")
    public void test_walk_diacritics3 (final @Nonnull String testSetName)
      throws IOException
      {
        final String testSetFolder = System.getProperty("blueMarine2.musicTestSets.path");
        final Path parent = Paths.get(testSetFolder, testSetName, "Music", "Ivo Pogorelich/Ravel_ Gaspard De La Nuit; Prokofiev_ Piano Sonata #6");

        if (!Files.exists(parent))
          {
            log.warn("Doesn't exist: {}", parent);
          }
        else
          {
            final AtomicBoolean walked = new AtomicBoolean();
            Stream.of(parent.toFile().listFiles()).map(file -> file.toPath()).filter(Files::isRegularFile).forEach(path ->
              {
                walked.set(true);
                final String s =  parent.relativize(path).toString();
                log.info(">>>> found: {}", s);
                log.info(">>>> found: {}", toDebugString(s));
              });

            assertThat(walked.get(), is(true));
          }
      }

    @Test(dataProvider = "stringsProvider", enabled = false)
    public void test2 (final @Nonnull String expected, final @Nonnull String actual)
      throws IOException
      {
        log.info(">>>> expected: {}", expected);
        log.info(">>>> expected: {}", toDebugString(expected));

        final Path parent = Files.createTempDirectory("test-diacritics");
        log.info(">>>> parent dir is {}", parent.toAbsolutePath());

        final String parentAbsPath = parent.toAbsolutePath().toString();

        Runtime.getRuntime().exec("/bin/cp /etc/hosts " + parentAbsPath + "/" + expected);
//        Runtime.getRuntime().exec("/bin/cp /etc/hosts " + parentAbsPath + "/" + string2);

        final AtomicBoolean walked = new AtomicBoolean();

        Files.walk(parent).filter(Files::isRegularFile).forEach(path ->
          {
            walked.set(true);
            final String s =  parent.relativize(path).toString();
            log.info(">>>> found: {}", s);
            log.info(">>>> found: {}", toDebugString(s));

            assertThat(s, is(expected));
//            System.err.println("FILE: " + path.toString());
//            System.err.println(">>>> Files.exists(): " + Files.exists(path));
//            System.err.println(">>>> toFile().exists(): " + path.toFile().exists());
//
//            try
//              {
//                Files.readAllLines(path);
//                System.err.println(">>>> Can be opened");
//              }
//            catch (IOException e)
//              {
//                System.err.println(">>>> Can't be opened");
//              }
          });

        assertThat(walked.get(), is(true));
      }

    @DataProvider
    private Object[][] stringsProvider()
      {
        return new Object[][]
          {
            {
            // iconv
            // hex    M   o    d    C3    A9    r    C3    A9
            fromBytes(77, 111, 100, 0303, 0251, 114, 0303, 0251),
            // hex    M   o    d    EF    BF    BD    EF    BF    BD    r    EF    BF    BD    EF    BF    BD
            fromBytes(77, 111, 100, 0357, 0277, 0275, 0357, 0277, 0275, 114, 0357, 0277, 0275, 0357, 0277, 0275)
            },

            {
            // noiconv
            // hex    M   o    d    CC    81    r    CC    81
            fromBytes(77, 111, 100, 0314, 0201, 114, 0314, 0201),
            // hex    M   o    d    EF    BF    BD    EF    BF    BD    r    EF    BF    BD    EF    BF    BD
            fromBytes(77, 111, 100, 0357, 0277, 0275, 0357, 0277, 0275, 114, 0357, 0277, 0275, 0357, 0277, 0275)
            }
          };
      }

    @Nonnull
    private static String fromBytes (final @Nonnull int ... bytesAsInt)
      {
        final byte[] bytes = new byte[bytesAsInt.length];

        for (int i = 0; i < bytes.length; i++)
          {
            bytes[i] = (byte)bytesAsInt[i];
          }

        return new String(bytes);
      }
  }
