package it.tidalwave.bluemarine2.ui.audio.renderer.impl.javafx;

import it.tidalwave.util.ProcessExecutor;
import it.tidalwave.util.spi.DefaultProcessExecutor;
import java.io.File;
import java.io.FileInputStream;
import org.testng.annotations.Test;

/**
 *
 * @author fritz
 */
public class SoXMediaPlayerTest
  {
    @Test(enabled = false)
    public void testSetMediaItem()
      throws Exception
      {
        File dir = new File("/root/.blueMarine2/Music/Compilations/Debussy_ Piano Works [Disc 1]");
        File[] files = dir.listFiles();

        for (final File file : files)
          {
            System.err.println(file.getAbsolutePath());

            new FileInputStream(file).close();

            if (file.getAbsolutePath().contains("Engloutie"))
              {
                System.err.println("Playing ...");
                ProcessExecutor executor = DefaultProcessExecutor.forExecutable("/usr/bin/play") // FIXME
                                                 .withArguments(file.getAbsolutePath())
                                                 .start();
                executor.start();
              }
          }
      }
  }
