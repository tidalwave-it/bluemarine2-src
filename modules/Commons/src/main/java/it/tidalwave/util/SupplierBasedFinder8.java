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
package it.tidalwave.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import it.tidalwave.util.spi.SimpleFinder8Support;

/***********************************************************************************************************************
 *
 * A {@link Finder} which retrieve results from a {@link Supplier}.
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Immutable
public class SupplierBasedFinder8<T> extends SimpleFinder8Support<T>
  {
    private static final long serialVersionUID = 1344191036948400804L;

    @Nonnull
    private final Supplier<Collection<? extends T>> supplier;

    public SupplierBasedFinder8 (final @Nonnull Supplier<Collection<? extends T>> supplier)
      {
        this.supplier = supplier;
      }

    public SupplierBasedFinder8 (final @Nonnull SupplierBasedFinder8<T> other, @Nonnull Object override)
      {
        super(other, override);
        final SupplierBasedFinder8<T> source = getSource(SupplierBasedFinder8.class, other, override);
        this.supplier = source.supplier;
      }

    @Override @Nonnull
    protected List<? extends T> computeResults() // FIXME: or computeNeededResults()?
      {
        return new CopyOnWriteArrayList<>(supplier.get());
      }
  }