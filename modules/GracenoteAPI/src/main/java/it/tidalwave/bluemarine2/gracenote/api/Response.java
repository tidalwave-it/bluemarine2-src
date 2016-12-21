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

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PRIVATE)
public class Response<T>
  {
    private static final DocumentBuilderFactory DOCBUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    private final Optional<T> datum;

    // FIXME: add error code

    @Nonnull
    public T get()
      {
        return datum.get();
      }

    public boolean isPresent()
      {
        return datum.isPresent();
      }

    @Nonnull
    public <U> Optional<U> map (final @Nonnull Function<? super T, ? extends U> function)
      {
        return datum.map(function);
      }

    @Nonnull
    public <U> Optional<U> flatMap (final @Nonnull Function<? super T, Optional<U>> function)
      {
        return datum.flatMap(function);
      }

    @Nonnull
    public static <X> Response<X> of (final @Nonnull ResponseEntity<String> response,
                                      final @Nonnull Function<Document, X> parser)
      {
        try
          {
            final int httpStatus = response.getStatusCodeValue();

            if (httpStatus != HttpStatus.OK.value())
              {
                throw new RuntimeException("Unexpected HTTP status: " + response.getStatusCode());
              }

            final Document dom = toDom(response);

            final XPath xPath = XPATH_FACTORY.newXPath();
            final XPathExpression exprResponseStatus = xPath.compile("/RESPONSES/RESPONSE/@STATUS");
            final String responseStatus = exprResponseStatus.evaluate(dom);

            switch (responseStatus)
              {
                case "NO_MATCH":
                  return new Response<>(Optional.empty());

                case "OK":
                  return new Response<>(Optional.of(parser.apply(dom)));

                default:
                  throw new RuntimeException("Unexpected response status: " + responseStatus);
              }
          }
        catch (XPathExpressionException e)
          {
            throw new RuntimeException(e);
          }
      }

    @Nonnull
    private static Document toDom (final @Nonnull ResponseEntity<String> response)
      {
        try
          {
            final DocumentBuilder db = DOCBUILDER_FACTORY.newDocumentBuilder();

            try (final InputStream is = new ByteArrayInputStream(response.getBody().getBytes(UTF_8)))
              {
                return db.parse(is);
              }
          }
        catch (ParserConfigurationException | SAXException | IOException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
