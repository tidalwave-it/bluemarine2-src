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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Configurable(preConstruction = true)
public abstract class JsonSupport
  {
    @Inject @JsonIgnore
    private ResourceServer server;

    @Nonnull
    protected final String resourceUri (final @Nonnull String resourceType, final @Nonnull Identifiable resource)
      {
        return resourceUri(resourceType, resource.getId().stringValue());
      }

    @Nonnull
    protected final String resourceUri (final @Nonnull String resourceType, final @Nonnull String resourceId)
      {
        return server.absoluteUrl(String.format("rest/%s/%s", resourceType, resourceId));
      }
  }
