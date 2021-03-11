/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.util;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.eclipse.rdf4j.common.iteration.Iteration;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NotThreadSafe
public class ImmutableTupleQueryResult implements TupleQueryResult
  {
    private final Set<String> bindingNames = new LinkedHashSet<>();

    private final List<BindingSet> bindingSets = new ArrayList<>();

    private int currentIndex = 0;

    public ImmutableTupleQueryResult (final TupleQueryResult tqr)
      throws QueryEvaluationException
      {
        this(tqr.getBindingNames(), tqr);
      }

    public ImmutableTupleQueryResult (final ImmutableTupleQueryResult tqr)
      throws QueryEvaluationException
      {
        this.bindingNames.addAll(tqr.bindingNames);
        this.bindingSets.addAll(tqr.bindingSets);
      }

    private <E extends Exception> ImmutableTupleQueryResult (final Collection<String> bindingNames,
                                                             final Iteration<? extends BindingSet, E> iteration)
      throws E
      {
        this.bindingNames.addAll(bindingNames);
        Iterations.addAll(iteration, this.bindingSets); // this also closes iteration
      }

    @Override
    public List<String> getBindingNames()
      {
        return new ArrayList<>(bindingNames);
      }

    @Override
    public boolean hasNext()
      {
        return currentIndex < bindingSets.size();
      }

    @Override
    public BindingSet next()
      {
        if (hasNext())
          {
            final BindingSet result = bindingSets.get(currentIndex);
            currentIndex++;
            return result;
          }

        throw new NoSuchElementException();
      }

    @Override
    public void close()
      {
        // no-op
      }

    @Override
    public void remove()
      throws QueryEvaluationException
      {
        throw new UnsupportedOperationException("Immutable");
      }
  }
