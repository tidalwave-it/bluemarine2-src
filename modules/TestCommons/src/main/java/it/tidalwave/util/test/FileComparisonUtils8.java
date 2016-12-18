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
package it.tidalwave.util.test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.test.FileComparisonUtils.commonPrefix;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
@Slf4j
public class FileComparisonUtils8
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public static void assertSameContents (final @Nonnull Path expectedPath, final @Nonnull Path actualPath)
      throws IOException
      {
        final String commonPath = commonPrefix(expectedPath.toString(), actualPath.toString());
        log.info("******** Comparing files:");
        log.info(">>>> path is: {}", commonPath);
        log.info(">>>> exp is:  {}", expectedPath.toString().substring(commonPath.length()));
        log.info(">>>> act is:  {}", actualPath.toString().substring(commonPath.length()));
        FileComparisonUtils.assertSameContents(Files.readAllLines(expectedPath), Files.readAllLines(actualPath));
      }

  }
