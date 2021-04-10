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
import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * A {@link Finder} which retrieve results from another instance applying a {@link Function}.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable
public class MappingFinder<TYPE> extends SupplierBasedFinder<TYPE>
  {
    private static final long serialVersionUID = -6359683808082070089L;

    @Nonnull
    private final transient Function<TYPE, TYPE> mapper;

    public MappingFinder (@Nonnull final Finder<TYPE> delegate, @Nonnull final Function<TYPE, TYPE> mapper)
      {
        super(delegate::results);
        this.mapper = mapper;
      }

    public MappingFinder (@Nonnull final MappingFinder other, @Nonnull final Object override)
      {
        super(other, override);
        final MappingFinder<TYPE> source = getSource(MappingFinder.class, other, override);
        this.mapper = source.mapper;
      }

    @Override @Nonnull
    protected List<? extends TYPE> computeResults()
      {
        return super.computeResults().stream().map(mapper).collect(toList());
      }
  }
