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
package it.tidalwave.bluemarine2.gracenote.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
@Immutable @Getter @EqualsAndHashCode @ToString @Builder
public class Album
  {
    @Nonnull
    private final String gnId;

    @Nonnull
    private final String artist;

    @Nonnull
    private final String title;

    // TODO: language
    // TODO: date
    // TODO: genre

    @Nonnull
    public static Album of (final @Nonnull ResponseEntity<String> response)
      throws IOException
      {
        try
          {
            final XPathFactory xPathFactory = XPathFactory.newInstance();
            final XPath xPath = xPathFactory.newXPath();
            final XPathExpression exprGnId = xPath.compile("/RESPONSES/RESPONSE/ALBUM/GN_ID");
            final XPathExpression exprArtist = xPath.compile("/RESPONSES/RESPONSE/ALBUM/ARTIST");
            final XPathExpression exprTitle = xPath.compile("/RESPONSES/RESPONSE/ALBUM/TITLE");

            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();

            try (final InputStream is = new ByteArrayInputStream(response.getBody().getBytes(UTF_8)))
              {
                final Document dom = db.parse(is);

                return builder().gnId(exprGnId.evaluate(dom))
                                .artist(exprArtist.evaluate(dom))
                                .title(exprTitle.evaluate(dom))
                                .build();
              }
          }
        catch (XPathExpressionException | ParserConfigurationException | SAXException e)
          {
            throw new IOException(e);
          }

      }
  }
