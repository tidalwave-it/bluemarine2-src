/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2017 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerBase;
import org.eclipse.rdf4j.rio.n3.N3ParserFactory;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static java.nio.charset.StandardCharsets.UTF_8;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Utilities
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Model parseModel (final @Nonnull DownloadComplete message)
        throws RDFHandlerException, RDFParseException, IOException
      {
        final N3ParserFactory n3ParserFactory = new N3ParserFactory();
        final RDFParser parser = n3ParserFactory.getParser();
        final Model model = new LinkedHashModel();

        parser.setRDFHandler(new RDFHandlerBase()
          {
            @Override
            public void handleStatement (final @Nonnull Statement statement)
              {
                model.add(statement);
              }
          });

        // FIXME
        final byte[] bytes = new String(message.getBytes(), UTF_8).replaceAll(" = ", "owl:sameAs")
                                                                  .replaceAll("/ASIN/ *>", "/ASIN>")
                                                                  .getBytes(UTF_8);
        final String uri = message.getUrl().toString();
        parser.parse(new ByteArrayInputStream(bytes), uri);

        return model;
      }
  }
