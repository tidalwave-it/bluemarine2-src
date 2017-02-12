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

import java.io.File;
import javax.inject.Inject;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import it.tidalwave.bluemarine2.persistence.Persistence;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id:$
 *
 **********************************************************************************************************************/
public class RepositoryDelegate implements Repository
  {
    @Inject
    private Persistence persistence;

    @Override
    public void setDataDir(File dataDir)
      {
        persistence.getRepository().setDataDir(dataDir);
      }

    @Override
    public File getDataDir()
      {
        return persistence.getRepository().getDataDir();
      }

    @Override
    public void initialize()
      {
      }

    @Override
    public boolean isInitialized()
      {
        return persistence.getRepository().isInitialized();
      }

    @Override
    public void shutDown()
      {
      }

    @Override
    public boolean isWritable()
      throws RepositoryException
      {
        return persistence.getRepository().isWritable();
      }

    @Override
    public RepositoryConnection getConnection()
      throws RepositoryException
      {
        return persistence.getRepository().getConnection();
      }

    @Override
    public ValueFactory getValueFactory()
      {
        return persistence.getRepository().getValueFactory();
      }
  }
