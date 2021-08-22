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
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.SafeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import it.tidalwave.bluemarine2.mediascanner.impl.xmp.XmpMetadataExtractor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A specific parser for XMP sidecar files, it copies a bunch of tags into metadata.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class XmpParser extends AbstractParser
  {
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public Set<MediaType> getSupportedTypes (@Nonnull final ParseContext context)
      {
        return Set.of(MediaType.application("rdf+xml"));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void parse (@Nonnull final InputStream stream,
                       @Nonnull final ContentHandler handler,
                       @Nonnull final Metadata metadata,
                       @Nonnull final ParseContext context)
            throws IOException, SAXException, TikaException
      {
        metadata.add(Metadata.CONTENT_TYPE, "application/rdf+xml");
        context.getSAXParser().parse(stream, new SafeContentHandler(new XmpMetadataExtractor(metadata::add)));
      }
  }
