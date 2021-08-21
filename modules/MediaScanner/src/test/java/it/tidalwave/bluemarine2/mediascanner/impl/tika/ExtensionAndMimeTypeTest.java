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
import java.util.function.Function;
import java.nio.file.Path;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class ExtensionAndMimeTypeTest
  {
    @Test
    public void test()
      {
        assertTrue(ExtensionAndMimeType.ofExtension("xmp")
                                       .withProbe(mockProbe(""))
                                       .matches(Path.of("foo.xmp")));
        assertFalse(ExtensionAndMimeType.ofExtension("jpg")
                                       .withProbe(mockProbe(""))
                                       .matches(Path.of("foo.xmp")));
        assertTrue(ExtensionAndMimeType.ofMimeType("image/jpeg")
                                       .withProbe(mockProbe("image/jpeg"))
                                       .matches(Path.of("")));
        assertFalse(ExtensionAndMimeType.ofMimeType("image/jpeg")
                                       .withProbe(mockProbe("image/gif"))
                                       .matches(Path.of("")));
      }

    @Nonnull
    private static Function<Path, String> mockProbe (@Nonnull final String mimeType)
      {
        return __ -> mimeType;
      }
  }
