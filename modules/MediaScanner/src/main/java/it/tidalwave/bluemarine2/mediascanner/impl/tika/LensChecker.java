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
package it.tidalwave.bluemarine2.mediascanner.impl.tika;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import com.healthmarketscience.jackcess.RuntimeIOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * This is more a tool required by the author at a certain point than effective code of BM, but it can be thought as a
 * test.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class LensChecker implements Consumer<MetadataWithPath>
  {
    private final Properties nameNormalisationMap = new Properties();

    @Getter
    private final SortedMap<String, AtomicInteger> statistics = new TreeMap<>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public LensChecker()
      {
        try (final Reader r = Files.newBufferedReader(Path.of("src/main/resources/Lens.properties")))
          {
            nameNormalisationMap.load(r);
          }
        catch (IOException e)
          {
            throw new RuntimeIOException(e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Override
    public void accept (@Nonnull final MetadataWithPath metadata)
      {
        try
          {
            final List<String> errors = new ArrayList<>();
            String lensModel = metadata.getMeta().get("aux:Lens"); // .get("Exif SubIFD:Lens Model");
            String lensInfo = metadata.getMeta().get("exifEX:LensModel"); // ("Exif SubIFD:Lens Specification")

            if (lensInfo == null)
              {
                lensInfo = metadata.getMeta().get("aux:LensInfo");
              }

            if (lensModel == null)
              {
                errors.add("Null lensModel");
              }
            else if (lensInfo == null)
              {
                errors.add("Null lensInfo");
              }
            else if (!lensInfo.equals(lensModel))
              {
                final String mapped = (String)nameNormalisationMap.get(lensInfo);

                if (mapped == null)
                  {
                    errors.add("Unmapped: " + lensInfo + " vs " + lensModel);
                  }
                else if (!mapped.equals(lensModel))
                  {
                    errors.add("Mismatch: " + lensInfo + " vs " + lensModel);
                  }
              }

            if (lensModel == null)
              {
                lensModel = "*missing*";
              }

            statistics.computeIfAbsent(lensModel, __ -> new AtomicInteger(0)).incrementAndGet();

            if (!errors.isEmpty())
              {
                log.debug("    LENS: {}: {}", metadata.getPath().getFileName().toString(), errors);
              }

            // log.debug("    {} LENS {} --- {}", metadata.getPath().getFileName().toString(), lensModel, lensInfo);
          }
        catch (Exception e)
          {
            log.warn("    {}", e.toString());
          }
      }
  }
