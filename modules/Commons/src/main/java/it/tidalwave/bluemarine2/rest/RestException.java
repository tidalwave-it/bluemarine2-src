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
package it.tidalwave.bluemarine2.rest;

import javax.annotation.Nonnull;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Getter @ToString
public class RestException extends RuntimeException // FIXME: runtime or checked?
  {
    private static final long serialVersionUID = -6024223349240820858L;

    @Nonnull
    private final Optional<String> responseStatus;

    @Nonnull
    private final Optional<HttpStatus> httpStatus;

    public RestException (final @Nonnull String message)
      {
        super(message);
        responseStatus = Optional.empty();
        httpStatus = Optional.empty();
      }

    public RestException (final @Nonnull String message, final @Nonnull String responseStatus)
      {
        super(message + ": " + responseStatus);
        this.responseStatus = Optional.of(responseStatus);
        this.httpStatus = Optional.empty();
      }

    public RestException (final @Nonnull String message, final @Nonnull HttpStatus httpStatus)
      {
        super(message);
        this.responseStatus = Optional.empty();
        this.httpStatus = Optional.of(httpStatus);
      }

    public RestException (final @Nonnull Throwable cause)
      {
        super(cause);
        responseStatus = Optional.empty();
        httpStatus = Optional.empty();
      }
  }
