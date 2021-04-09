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
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.musicbrainz.ns.mmd_2.Metadata;
import org.springframework.http.ResponseEntity;
import it.tidalwave.bluemarine2.rest.RestException;
import it.tidalwave.bluemarine2.rest.RestResponse;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * This class encapsulates a response from the MusicBrainz API, including the requested datum - if available - and some
 * status codes.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
class MusicBrainzResponse<T> extends RestResponse<T>
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public MusicBrainzResponse (@Nonnull final Optional<T> datum, @Nonnull final String responseStatus)
      {
        super(datum, responseStatus);
      }

    /*******************************************************************************************************************
     *
     * Creates a {@code Response} containing a datum out of a {@link ResponseEntity} applying a parser. The parser
     * receives an XML DOM as the input - it typically uses XPath to extract information.
     *
     * @param <X>       the type of the datum
     * @param response  the HTTP response
     * @param parser    the parser that produces the datum
     * @return          the {@code Response}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <X> MusicBrainzResponse<X> of (@Nonnull final ResponseEntity<String> response,
                                                 @Nonnull final Function<Metadata, X> function)
      {
        final int httpStatus = response.getStatusCodeValue();
        final String statusCodeAsString = response.getStatusCode().toString();

        switch (httpStatus)
          {
            case 200: // OK
                try
                  {
                    final JAXBContext ctx = JAXBContext.newInstance("org.musicbrainz.ns.mmd_2");
                    final Metadata metadata = (Metadata)ctx.createUnmarshaller().unmarshal(new StringReader(response.getBody()));
                    return new MusicBrainzResponse<>(Optional.of(function.apply(metadata)), statusCodeAsString);
                  }
                catch (JAXBException e)
                  {
                    throw new RuntimeException(e);
                  }

            case 400: // BAD REQUEST
                log.warn(">>>> returning HTTP status code: {}", response.getStatusCode());
                return new MusicBrainzResponse<>(Optional.empty(), statusCodeAsString);

            default:
                throw new RestException("Unexpected HTTP status: " + response.getStatusCode(), response.getStatusCode());
          }
      }
  }
