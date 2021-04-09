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
package it.tidalwave.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import it.tidalwave.util.spi.SimpleFinderSupport;

/***********************************************************************************************************************
 *
 * A {@link Finder} which retrieve results from a {@link Supplier}.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable
public class SupplierBasedFinder<T> extends SimpleFinderSupport<T>
  {
    private static final long serialVersionUID = 1344191036948400804L;

    @Nonnull
    private final Supplier<Collection<? extends T>> supplier;

    public SupplierBasedFinder (@Nonnull final Supplier<Collection<? extends T>> supplier)
      {
        this.supplier = supplier;
      }

    public SupplierBasedFinder (@Nonnull final SupplierBasedFinder<T> other, @Nonnull Object override)
      {
        super(other, override);
        final SupplierBasedFinder<T> source = getSource(SupplierBasedFinder.class, other, override);
        this.supplier = source.supplier;
      }

    @Override @Nonnull
    protected List<? extends T> computeResults() // FIXME: or computeNeededResults()?
      {
        return new CopyOnWriteArrayList<>(supplier.get());
      }
  }