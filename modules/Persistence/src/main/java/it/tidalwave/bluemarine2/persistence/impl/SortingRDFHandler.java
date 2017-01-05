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
package it.tidalwave.bluemarine2.persistence.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.Delegate;
import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class SortingRDFHandler implements RDFHandler
  {
    interface Exclusions
      {
        public void handleStatement (Statement statement)
          throws RDFHandlerException;

        public void endRDF()
          throws RDFHandlerException;
      }

    @Nonnull @Delegate(excludes = Exclusions.class)
    private final RDFHandler delegate;

    private final List<Statement> statements = new ArrayList<>();

    @Override
    public void handleStatement (final @Nonnull Statement statement)
      throws RDFHandlerException
      {
        statements.add(statement);
      }

    @Override
    public void endRDF()
      throws RDFHandlerException
      {
        Collections.sort(statements, new Comparator<Statement>()
          {
            @Override
            public int compare (final @Nonnull Statement st1, final @Nonnull Statement st2)
              {
                final String s1 = st1.getSubject().stringValue();
                final String s2 = st2.getSubject().stringValue();
                final int c1 = s1.compareTo(s2);

                if (c1 != 0)
                  {
                    return c1;
                  }

                final String p1 = st1.getPredicate().stringValue();
                final String p2 = st2.getPredicate().stringValue();
                final int c2 = p1.compareTo(p2);

                if (c2 != 0)
                  {
                    if (p1.equals(RDF.TYPE.stringValue()))
                      {
                        return -1;
                      }
                    else if (p2.equals(RDF.TYPE.stringValue()))
                      {
                        return +1;
                      }

                    return c2;
                  }

                final String o1 = st1.getObject().stringValue();
                final String o2 = st2.getObject().stringValue();

                return o1.compareTo(o2);
              }
          });

        for (final Statement statement : statements)
          {
            delegate.handleStatement(statement);
          }

        delegate.endRDF();
      }
  }
