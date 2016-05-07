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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.nio.file.Paths;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.testng.annotations.Test;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class AudioMetadataTest
  {
    @Test(enabled = false, groups = "no-ci") // requires files on my disk
    public void testScan()
      {
        final AudioMetadata am = new AudioMetadata(Paths.get("/Users/fritz/Personal/Music/iTunes/iTunes Music/Music/Compilations/Beethoven Symphonies 1 & 2/01 Beethoven_ Symphonie #1 C-dur op. 21 (1. Adagio molto - Allegro con brio).m4a"));
        System.err.println(am);
        System.err.println(am.audioFile);
        final Tag tag = am.audioFile.getTag();
        final List<TagField> fields = toList(tag.getFields());
        System.err.println("FIELDS: " + fields);
//        tag.getFields(FieldKey.)

      }

    private <T> List<T> toList (final @Nonnull Iterator<T> i)
      {
        final List<T> result = new ArrayList<>();

        while (i.hasNext())
          {
            result.add(i.next());
          }

        return result;
      }
  }
