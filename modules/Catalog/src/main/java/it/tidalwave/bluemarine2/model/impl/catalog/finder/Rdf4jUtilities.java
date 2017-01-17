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
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.stream.Stream;
import org.eclipse.rdf4j.common.iteration.Iteration;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public class Rdf4jUtilities
  {
    @Nonnull
    public static <T, X extends RuntimeException> Stream<T> streamOf (final @Nonnull Iteration<T, X> iteration)
      {
        return stream(spliteratorUnknownSize(iteratorOf(iteration), 0), false);
      }

    @Nonnull
    private static <T, X extends RuntimeException> Iterator<T> iteratorOf (final @Nonnull Iteration<T, X> iteration)
      {
        final Iterator<T> iterator = new Iterator<T>()
          {
            @Override
            public boolean hasNext()
              {
                return iteration.hasNext();
              }

            @Override
            public T next()
              {
                return iteration.next();
              }
          };

        return iterator;
      }
  }
