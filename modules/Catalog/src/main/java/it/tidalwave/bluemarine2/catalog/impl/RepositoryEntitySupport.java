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
package it.tidalwave.bluemarine2.catalog.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import org.openrdf.repository.Repository;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.Entity;
import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString(of = { "rdfsLabel", "id"})
public class RepositoryEntitySupport implements Entity, Identifiable
  {
    @Nonnull
    protected final Repository repository;
    
    @Getter @Nonnull
    protected final Id id;
    
    @Getter
    protected final String rdfsLabel;
    
    @Delegate
    private final AsSupport asSupport = new AsSupport(this);

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public RepositoryEntitySupport (final @Nonnull Repository repository, 
                                    final @Nonnull BindingSet bindingSet,
                                    final @Nonnull String idName)
      {
        this.repository = repository;
        this.id = new Id(toString(bindingSet.getBinding(idName)));
        this.rdfsLabel = toString(bindingSet.getBinding("label"));
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nullable
    protected static String toString (final @Nullable Binding binding)
      {
        if (binding == null)
          {
            return null;  
          }
        
        final Value value = binding.getValue();
        
        return (value != null) ? value.stringValue() : null;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nullable
    protected static Integer toInteger (final @Nullable Binding binding)
      {
        if (binding == null)
          {
            return null;  
          }
        
        final Value value = binding.getValue();
        
        return (value != null) ? Integer.parseInt(value.stringValue()) : null;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nullable
    protected static Duration toDuration (final @Nullable Binding binding)
      {
        if (binding == null)
          {
            return null;  
          }
        
        final Value value = binding.getValue();
        
        return (value != null) ? Duration.ofMillis((int)Float.parseFloat(value.stringValue())) : null;
      }
  }