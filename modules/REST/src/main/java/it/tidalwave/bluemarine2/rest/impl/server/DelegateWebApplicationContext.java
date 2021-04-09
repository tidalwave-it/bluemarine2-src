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
package it.tidalwave.bluemarine2.rest.impl.server;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.WebApplicationContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class DelegateWebApplicationContext implements WebApplicationContext
  {
    @Delegate(types = { ApplicationContext.class, BeanFactory.class, EnvironmentCapable.class,
                        ListableBeanFactory.class, HierarchicalBeanFactory.class, MessageSource.class,
                        ApplicationEventPublisher.class, ResourcePatternResolver.class} )
    @Nonnull
    private final ApplicationContext delegate;

    @Getter @Nonnull
    private final ServletContext servletContext;
  }
