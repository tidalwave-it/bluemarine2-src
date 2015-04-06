/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.persistence;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * FIXME: use a buidler and allow passing multiple statements
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @RequiredArgsConstructor(access = AccessLevel.PRIVATE) @Getter @ToString
public class AddStatementsRequest 
  {
    @Nonnull
    private final List<Statement> statements;
    
    public static class Builder
      {
        private final List<Statement> statements = new ArrayList<>();
        
        private final ValueFactory factory = ValueFactoryImpl.getInstance();

        @Nonnull
        public Builder with (final @Nonnull Resource subject, 
                             final @Nonnull URI predicate,
                             final @Nonnull Value object) 
          {
            statements.add(factory.createStatement(subject, predicate, object));
            return this;
          }
        
        @Nonnull
        public AddStatementsRequest create()
          {
            return new AddStatementsRequest(Collections.unmodifiableList(statements));
          }
      }
    
    public AddStatementsRequest (final @Nonnull Resource subject, 
                             final @Nonnull URI predicate,
                             final @Nonnull Value object) 
      {
        this(Arrays.asList(ValueFactoryImpl.getInstance().createStatement(subject, predicate, object)));
      }
     
    @Nonnull
    public static Builder build() 
      {
        return new Builder();
      }
  }
