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
import java.nio.file.Path;
// import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * A temporary class to work with Tika metadata. In case Tika is incorporated, this class must be merged to
 * i.t.b.model.Metadata.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class MetadataWithPath // extends XMPMetadata
  {
    @Nonnull @Getter
    private final Path path;

    @Nonnull
    private final Metadata metadata;

    public MetadataWithPath (@Nonnull  final Path path, @Nonnull final Metadata metadata)
//            throws TikaException
      {
        // super(metadata);
        this.path = path;
        this.metadata = metadata;
      }

    @Nonnull
    public Metadata getMeta()
      {
        return metadata;
      }
  }
