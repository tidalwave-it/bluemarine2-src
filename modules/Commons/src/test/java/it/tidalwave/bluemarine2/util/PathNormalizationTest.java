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
package it.tidalwave.bluemarine2.util;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import it.tidalwave.util.spi.DefaultProcessExecutor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static java.util.stream.Collectors.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;
import static org.testng.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/***********************************************************************************************************************
 *
 * A number of fascinating things happens when you deal with files whose name include non-ascii characters, different
 * operating systems and filesystems, and you mix them - even in the simplified case of having UTF-8 as the charset
 * encoding. In fact there are variants, known as "Unicode normalisations" (see https://unicode.org/reports/tr15/).
 * In particular NFD ("canonical decomposition") and NFC ("canonical decomposition followed by canonical composition"):
 * the former is used by e.g. macOS with APFS, the latter is used by e.g. Linux with EXT4 or BTRFS.
 *
 * Everything is fine when you create and manipulate files from the same platform, and also in some cases when you
 * transfer files from one system to the other; but in other cases a file with the wrong normalisation might land on
 * the system, and workarounds might be needed to manage it.
 *
 * See also http://askubuntu.com/questions/533690/rsync-with-special-character-files-not-working-between-mac-and-linux
 *
 * This file try to reproduce some cases in a simple way and also tests the workarounds. For more details, read the
 * comments on tests (in sequential order). Currently tested on BigSur + APFS and Linux + BTRFS, standard mount options.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class PathNormalizationTest
  {
    /**
     * Two strings with non-ascii characters and different normalisation. Everything started with tests including
     * a MP3 file named "L'Agaçante", a composition by Jean-Philippe Rameau. Tests worked on macOS but failed on the
     * CI system on Linux.
     *
     * BTW, “agaçante" in French means “irritating", a word that perfectly suits this scenario.
     */
    private static final String AGACANTE_NFC = "L'Agac" + ofBytes(0xCC, 0xA7) + "ante";

    /** Test string in NFD mode. */
    private static final String AGACANTE_NFD = "L'Aga" + ofBytes(0xC3, 0xA7) + "ante";

    /** Test string in native mode. */
    private static final String AGACANTE = "L'Agaçante";

    private static final Path TARGET = Path.of("target");
    private static final Path JAVA_FOLDER = TARGET.resolve("java");
    private static final Path ZIP_FOLDER = TARGET.resolve("zip");
    private static final Path TAR_FOLDER = TARGET.resolve("tar");

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    private static final String FOO_BAR = "foo bar";

    private static final String MAC_OS_X = "mac os x";
    private static final String LINUX = "linux";

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeTest
    public void clean()
      throws IOException
      {
        FileUtils.deleteDirectory(JAVA_FOLDER.toFile());
        FileUtils.deleteDirectory(ZIP_FOLDER.toFile());
        FileUtils.deleteDirectory(TAR_FOLDER.toFile());
        Files.createDirectories(JAVA_FOLDER);
        Files.createDirectories(ZIP_FOLDER);
        Files.createDirectories(TAR_FOLDER);
      }

    /*******************************************************************************************************************
     *
     * First check that the two strings have been properly created with different normalisations.
     *
     ******************************************************************************************************************/
    @Test
    public void test_precondition()
      {
        log.info("NFC: {}", toHex(AGACANTE_NFC));
        log.info("NFD: {}", toHex(AGACANTE_NFD));
        assertThat(AGACANTE_NFC.equals(AGACANTE_NFD), is(false));
      }

    /*******************************************************************************************************************
     *
     * Then check that we are capable to convert both to the same native form.
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_precondition")
    public void test_normalizedToNativeForm()
      {
        // when
        final String normalizedFromNFC = PathNormalization.normalizedToNativeForm(AGACANTE_NFC);
        final String normalizedFromNFD = PathNormalization.normalizedToNativeForm(AGACANTE_NFD);
        // then
        assertThat(normalizedFromNFC, is(normalizedFromNFD));
      }

    /*******************************************************************************************************************
     *
     * Then check that we can detect that two paths with different normalizations are the same path.
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_normalizedToNativeForm")
    public void test_equalsNormalized()
      {
        // given
        final Path pathAgacanteNfc = JAVA_FOLDER.resolve(AGACANTE_NFC);
        final Path pathAgacanteNfd = JAVA_FOLDER.resolve(AGACANTE_NFD);
        // when
        final boolean equals = PathNormalization.equalsNormalized(pathAgacanteNfc, pathAgacanteNfd);
        // then
        assertThat(equals, is(true));
      }

    /*******************************************************************************************************************
     *
     * Creating a file from Java behaves differently on macOS and Linux!
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_equalsNormalized")
    public void test_file_creation_from_Java()
      throws IOException
      {
        // given
        Files.createDirectories(JAVA_FOLDER);
        final Path pathAgacanteNfc = JAVA_FOLDER.resolve(AGACANTE_NFC);
        final Path pathAgacanteNfd = JAVA_FOLDER.resolve(AGACANTE_NFD);
        // when
        Files.writeString(pathAgacanteNfc, FOO_BAR, UTF_8, CREATE);
        Files.writeString(pathAgacanteNfd, FOO_BAR, UTF_8, CREATE);
        // then
        final List<Path> children = Files.list(JAVA_FOLDER).collect(toList());

        switch (OS_NAME)
          {
            case MAC_OS_X:
              assertThat(children.size(), is(1));
              assertThat(children.get(0).getFileName().toString(), is(AGACANTE_NFD));
              log.info("On macOS only a single file is created, with the native normalization.");
              break;

            case LINUX:
              assertThat(children.size(), is(2));
              assertThat(children.get(0).getFileName().toString(), is(AGACANTE_NFC));
              assertThat(children.get(1).getFileName().toString(), is(AGACANTE_NFD));
              log.info("On Linux two files are created, with both normalizations.");
              break;

            // TODO: Windows
          }

        for (final Path child : children)
          {
            final String childFileName = child.getFileName().toString();
            log.info("Child: {}", childFileName);
            log.info("NFC:   {}", toHex(AGACANTE_NFC));
            log.info("NFD:   {}", toHex(AGACANTE_NFD));
            log.info("Child: {}", toHex(childFileName));
            final boolean b1 = childFileName.equals(AGACANTE_NFC);
            final boolean b2 = childFileName.equals(AGACANTE_NFD);
            assertThat(b1 || b2, is(true));
          }
      }

    /*******************************************************************************************************************
     *
     * Extracting a file from zip behaves differently on macOS and Linux!
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_file_creation_from_Java")
    public void test_extraction_from_zip()
            throws IOException, InterruptedException
      {
        // when
        log.info("Extracting files from zip...");
        DefaultProcessExecutor.forExecutable("/usr/bin/unzip")
                              .withArgument("-o")
                              .withArgument("src/test/resources/agacantes.zip")
                              .withArgument("-d")
                              .withArgument(ZIP_FOLDER.toString())
                              .start()
                              .waitForCompletion();
        // then
        assertExtractionPostConditions(ZIP_FOLDER);
      }

    /*******************************************************************************************************************
     *
     * Extracting a file from tar behaves differently on macOS and Linux!
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_extraction_from_zip")
    public void test_extraction_from_tar()
            throws IOException, InterruptedException
      {
        // when
        extractFilesFromTar();
        // then
        assertExtractionPostConditions(TAR_FOLDER);
      }

    /*******************************************************************************************************************
     *
     * On Linux, if you address the file with the properly normalised name, there are no problems: files can be probed
     * for existence and opened.
     *
     * Once upon a time, in JDK 8, a way to probe the problematic condition was to compare file existence with the
     * old java.io API (path.toFile().exists()) and the java.nio API (Files.exist(path)): they gave different results
     * (java.io was able to open the file). As per JDK 11 they have been aligned.
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_extraction_from_tar")
    public void test_probe_and_open1()
            throws IOException, InterruptedException
      {
        // given
        extractFilesFromTar();
        // when
        final Path extractedNfc = TAR_FOLDER.resolve("nfc").resolve(AGACANTE_NFC);
        final Path extractedNfd = TAR_FOLDER.resolve("nfd").resolve(AGACANTE_NFD);
        // then
        assertThat(extractedNfc.toFile().exists(), is(true));
        assertThat(Files.exists(extractedNfc), is(true));
        assertThat(Files.readString(extractedNfc, UTF_8), is(FOO_BAR));
        Files.newBufferedReader(extractedNfc);
        log.info("NFC can be opened: {}", extractedNfc);

        assertThat(extractedNfd.toFile().exists(), is(true));
        assertThat(Files.exists(extractedNfd), is(true));
        assertThat(Files.readString(extractedNfd, UTF_8), is(FOO_BAR));
        Files.newBufferedReader(extractedNfd);
        log.info("NFD can be opened: {}", extractedNfd);
      }

    /*******************************************************************************************************************
     *
     * So where do problems actually arise? When you are trying to access a file with a path encoded with a different
     * normalisation. This happens e.g. when the path doesn't come from a filesystem inspection (File.list() or such)
     * but from another data source. In blueMarine it happens in tests, where file names are stored in plain text files
     * or the database. Strings are normalised by Java (so on Linux they are in the NFC form) and can't access file
     * names encoded in NFD form (that could come from an expanded archive - this happens all the time with tests).
     * testProbe() seems to be ineffective in this case too, but problems happen when you try to open the file.
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_probe_and_open1")
    public void test_probe_and_open2()
            throws IOException, InterruptedException
      {
        // given
        extractFilesFromTar();
        // when
        // note that normalisation in names here is swapped
        final Path extractedNfc = TAR_FOLDER.resolve("nfc").resolve(AGACANTE);
        final Path extractedNfd = TAR_FOLDER.resolve("nfd").resolve(AGACANTE);
        // then

        switch (OS_NAME)
          {
            case MAC_OS_X:
              assertThat(extractedNfc.toFile().exists(), is(true));
              assertThat(Files.exists(extractedNfc), is(true));
              break;

            case LINUX:
              assertThat(extractedNfc.toFile().exists(), is(false));
              assertThat(Files.exists(extractedNfc), is(false));
              break;
          }

        try
          {
            assertThat(Files.readString(extractedNfc, UTF_8), is(FOO_BAR));
            Files.newBufferedReader(extractedNfc);
            log.info("NFC can be opened: {}", extractedNfc);
          }
        catch (NoSuchFileException e)
          {
            switch (OS_NAME)
              {
                case MAC_OS_X:
                  fail("NFC can't be opened: " + extractedNfc);
                  break;

                case LINUX:
                  log.info("NFC can't be opened, as expected: {}", extractedNfc);
                  break;
              }
          }

        assertThat(extractedNfd.toFile().exists(), is(true));
        assertThat(Files.exists(extractedNfd), is(true));
        assertThat(Files.readString(extractedNfd, UTF_8), is(FOO_BAR));
        Files.newBufferedReader(extractedNfd);
        log.info("NFD can be opened: {}", extractedNfd);
      }

    /*******************************************************************************************************************
     *
     * So where do problems actually arise? When you are trying to access a file with a path encoded with a different
     * normalisation. This happens e.g. when the path doesn't come from a filesystem inspection (File.list() or such)
     * but from another data source. In blueMarine it happens in tests, where file names are stored in plain text files
     * or the database. Strings are normalised by Java (so on Linux they are in the NFC form) and can't access file
     * names encoded in NFD form (that could come from an expanded archive - this happens all the time with tests).
     * testProbe() seems to be ineffective in this case too, but problems happen when you try to open the file.
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods = "test_probe_and_open2")
    public void test_normalizedPath()
            throws IOException, InterruptedException
      {
        // given
        extractFilesFromTar();
        // when
        // note that normalisation in names here is swapped
        final Path extractedNfc = PathNormalization.normalizedPath(TAR_FOLDER.resolve("nfc").resolve(AGACANTE));
        final Path extractedNfd = PathNormalization.normalizedPath(TAR_FOLDER.resolve("nfd").resolve(AGACANTE));
        // then
        assertThat(extractedNfc.toFile().exists(), is(true));
        assertThat(Files.exists(extractedNfc), is(true));
        assertThat(Files.readString(extractedNfc, UTF_8), is(FOO_BAR));
        Files.newBufferedReader(extractedNfc);
        log.info("NFC can be opened: {}", extractedNfc);

        assertThat(extractedNfd.toFile().exists(), is(true));
        assertThat(Files.exists(extractedNfd), is(true));
        assertThat(Files.readString(extractedNfd, UTF_8), is(FOO_BAR));
        Files.newBufferedReader(extractedNfd);
        log.info("NFD can be opened: {}", extractedNfd);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private static void extractFilesFromTar()
            throws IOException, InterruptedException
      {
        log.info("Extracting files from tar...");
        DefaultProcessExecutor.forExecutable("/usr/bin/tar")
                              .withArgument("-xvf")
                              .withArgument("src/test/resources/agacantes.tar.gz")
                              .withArgument("-C")
                              .withArgument(TAR_FOLDER.toString())
                              .start()
                              .waitForCompletion();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private static void assertExtractionPostConditions (@Nonnull final Path folder)
            throws IOException
      {
        final Path extractedNfc = Files.list(folder.resolve("nfc")).findFirst().get();
        final Path extractedNfd = Files.list(folder.resolve("nfd")).findFirst().get();

        switch (OS_NAME)
          {
            case MAC_OS_X:
              assertThat(extractedNfc.getFileName().toString(), is(extractedNfd.getFileName().toString()));
              assertThat(extractedNfd.getFileName().toString(), is(AGACANTE_NFD));
              log.info("On macOS only a single file is extracted, with the native normalization.");
              break;

            case LINUX:
              assertThat(extractedNfc.getFileName().toString(), is(not(extractedNfd.getFileName().toString())));
              assertThat(extractedNfc.getFileName().toString(), is(AGACANTE_NFC));
              assertThat(extractedNfd.getFileName().toString(), is(AGACANTE_NFD));
              log.info("On Linux two files are extracted, with both normalizations.");
              break;

            // TODO: Windows
          }

        log.info("NFC:           {}", toHex(AGACANTE_NFC));
        log.info("NFD:           {}", toHex(AGACANTE_NFD));
        log.info("Extracted NFC: {}", toHex(extractedNfc.getFileName().toString()));
        log.info("Extracted NFD: {}", toHex(extractedNfd.getFileName().toString()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String ofBytes (final int b1, final int b2)
      {
        return new String(new byte[]{(byte)b1, (byte)b2});
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String toHex (final @Nonnull String string)
      {
        return string.chars().mapToObj(c -> String.format("%04x", c)).collect(Collectors.joining(" "));
      }
  }
