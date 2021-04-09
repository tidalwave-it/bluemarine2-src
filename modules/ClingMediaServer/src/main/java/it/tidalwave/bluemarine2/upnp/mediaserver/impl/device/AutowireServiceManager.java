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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.device;

import javax.annotation.Nonnull;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.LocalService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class AutowireServiceManager<T> extends DefaultServiceManager<T>
  {
    @Nonnull
    private final AutowireCapableBeanFactory beanFactory;

    public AutowireServiceManager (final @Nonnull AutowireCapableBeanFactory beanFactory,
                                   final @Nonnull LocalService<T> service)
      {
        super(service);
        this.beanFactory = beanFactory;
      }

    public AutowireServiceManager (final @Nonnull AutowireCapableBeanFactory beanFactory,
                                   final @Nonnull LocalService<T> service,
                                   final @Nonnull Class<T> serviceClass)
      {
        super(service, serviceClass);
        this.beanFactory = beanFactory;
      }

    @Override
    protected T createServiceInstance()
      throws Exception
      {
        final T serviceInstance = super.createServiceInstance();
        beanFactory.autowireBean(serviceInstance);
        return serviceInstance;
      }
  }
