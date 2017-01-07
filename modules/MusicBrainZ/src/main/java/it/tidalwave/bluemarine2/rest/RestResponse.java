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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static lombok.AccessLevel.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PROTECTED) @Slf4j
public class RestResponse<T>
  {
    @Nonnull
    private final Optional<T> datum;

    @Getter @Nonnull
    private final String responseStatus;

    @Nonnull
    public static <X> RestResponse<X> empty()
      {
        return new RestResponse<>(Optional.empty(), "");
      }

    /*******************************************************************************************************************
     *
     * Returns the datum, if available.
     *
     * @return      the datum
     * @throws      NoSuchElementException if the datum is not available
     *
     ******************************************************************************************************************/
    @Nonnull
    public T get()
      throws NoSuchElementException
      {
        return datum.get();
      }

    /*******************************************************************************************************************
     *
     * Returns <code>true</code> if the datum is available.
     *
     * @return      <code>true</code> if the datum is available
     *
     ******************************************************************************************************************/
    public boolean isPresent()
      {
        return datum.isPresent();
      }

    /*******************************************************************************************************************
     *
     * Maps the result, if present, to a new value using a mapping function.
     *
     * @param <U>       the type of the result
     * @param mapper    a mapping function to apply to the value, if present
     * @return          an {@code Optional} result
     *
     ******************************************************************************************************************/
    @Nonnull
    public <U> Optional<U> map (final @Nonnull Function<? super T, ? extends U> mapper)
      {
        return datum.map(mapper);
      }

    /*******************************************************************************************************************
     *
     * Maps the result, if present, to a new value using a mapping function, avoiding wrapping with multiple
     * {@code Optional}s.
     *
     * @param <U>       the type of the result
     * @param mapper    a mapping function to apply to the value, if present
     * @return          an {@code Optional} result
     *
     ******************************************************************************************************************/
    @Nonnull
    public <U> Optional<U> flatMap (final @Nonnull Function<? super T, Optional<U>> mapper)
      {
        return datum.flatMap(mapper);
      }

    /*******************************************************************************************************************
     *
     * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
     *
     * @param consumer  code to be executed if a value is present
     *
     ******************************************************************************************************************/
    public void ifPresent (final @Nonnull Consumer<? super T> consumer)
      {
        datum.ifPresent(consumer);
      }
  }
