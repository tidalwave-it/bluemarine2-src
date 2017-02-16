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
package it.tidalwave.bluemarine2.util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class FunctionWrappers
  {
    @FunctionalInterface
    public static interface FunctionWithException<T, R>
      {
        public R apply (T t)
          throws Exception;
      }

    @FunctionalInterface
    public static interface ConsumerWithException<T>
      {
        public void accept (T t)
          throws Exception;
      }

    @FunctionalInterface
    public static interface SupplierWithException<T>
      {
        public T get()
          throws Exception;
      }

    @Nonnull
    public static <T, R> Function<T, R> _f (final @Nonnull FunctionWithException<T, R> function)
      {
        return t ->
          {
            try
              {
                return function.apply(t);
              }
            catch (Exception e)
              {
                throw new RuntimeException(e);
              }
          };
      }

    @Nonnull
    public static <T> Consumer<T> _c (final @Nonnull ConsumerWithException<T> consumer)
      {
        return t ->
          {
            try
              {
                consumer.accept(t);
              }
            catch (Exception e)
              {
                throw new RuntimeException(e);
              }
          };
      }

    @Nonnull
    public static <T> Supplier<T> _s (final @Nonnull SupplierWithException<T> supplier)
      {
        return () ->
          {
            try
              {
                return supplier.get();
              }
            catch (Exception e)
              {
                throw new RuntimeException(e);
              }
          };
      }
  }
