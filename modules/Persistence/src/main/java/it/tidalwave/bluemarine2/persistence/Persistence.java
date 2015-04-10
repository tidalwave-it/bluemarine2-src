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

import java.io.FileNotFoundException;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import lombok.NonNull;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

/***********************************************************************************************************************
 *
 * This interface should not exist. For storing data into Persistence we're firing events to the messageBus. Why instead
 * we retrieve data by directly accessing the repository? Perhaps we could switch to a coherently sync model for
 * Persistence. In this case, AddStatementsRequest should me a private affair of DefaultMediaScanner.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface Persistence 
  {
    public static final Class<Persistence> Persistence = Persistence.class;
    
    @NonNull
    public Repository getRepository();
    
    public void dump (final @Nonnull Path path)
      throws RDFHandlerException, FileNotFoundException, RepositoryException;
  }
