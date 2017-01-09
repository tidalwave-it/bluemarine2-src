/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import it.tidalwave.bluemarine2.rest.RestException;
import it.tidalwave.bluemarine2.rest.RestResponse;
import java.io.StringReader;
import java.util.function.Function;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.musicbrainz.ns.mmd_2.Metadata;

/***********************************************************************************************************************
 *
 * This class encapsulates a response from the MusicBrainz API, including the requested datum - if available - and some
 * status codes.
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
class MusicBrainzResponse<T> extends RestResponse<T>
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public MusicBrainzResponse (final @Nonnull Optional<T> datum, final @Nonnull String responseStatus)
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
    public static <X> MusicBrainzResponse<X> of (final @Nonnull ResponseEntity<String> response,
                                                 final @Nonnull Function<Metadata, X> function)
      {
        final int httpStatus = response.getStatusCodeValue();

        if (httpStatus != HttpStatus.OK.value())
          {
            throw new RestException("Unexpected HTTP status", response.getStatusCode());
          }

        try
          {
            final JAXBContext ctx = JAXBContext.newInstance("org.musicbrainz.ns.mmd_2");
            final Metadata metadata = (Metadata)ctx.createUnmarshaller().unmarshal(new StringReader(response.getBody()));
            return new MusicBrainzResponse<>(Optional.of(function.apply(metadata)), "");
          }
        catch (JAXBException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
