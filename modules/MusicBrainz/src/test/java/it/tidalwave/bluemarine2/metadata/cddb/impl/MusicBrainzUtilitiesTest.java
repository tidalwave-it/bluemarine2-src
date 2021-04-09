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
package it.tidalwave.bluemarine2.metadata.cddb.impl;

import static it.tidalwave.bluemarine2.metadata.cddb.impl.MusicBrainzUtilities.*;
import java.io.UnsupportedEncodingException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class MusicBrainzUtilitiesTest
  {
    // wrong: http://musicbrainz.org/ws/2/release-group/?query=release:Brahms\:%20Piano%20Concerto%20%232
    // wrong: /ws/2/release-group/?query=release:Brahms\:%20Piano%20Concerto%20%232
    // right: /ws/2/release-group/?query=release:Brahms\:%20Piano%20Concerto%20%232
    @Test(enabled = false)
    public void testSomeMethod()
      throws UnsupportedEncodingException
      {
        final String string = "Brahms: Piano Concerto #2";
        final String actual = escape(string);
        assertEquals(actual, "Brahms\\:%20Piano%20Concerto%20%232");
      }
  }
