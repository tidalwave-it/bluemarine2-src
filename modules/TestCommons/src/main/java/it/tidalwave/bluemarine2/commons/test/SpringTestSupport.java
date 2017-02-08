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
package it.tidalwave.bluemarine2.commons.test;

import javax.annotation.Nonnull;
import java.util.Arrays;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.role.ContextManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class SpringTestSupport
  {
    protected enum LifeCycle
      {
        AROUND_METHOD, AROUND_CLASS
      }

    protected ClassPathXmlApplicationContext context;

    private final LifeCycle lifeCycle;

    @Nonnull
    private final String[] configLocations;

    protected SpringTestSupport (final @Nonnull LifeCycle lifeCycle, final @Nonnull String ... configLocations)
      {
        this.lifeCycle = lifeCycle;
        this.configLocations = configLocations;
      }

    protected SpringTestSupport (final @Nonnull String ... configLocations)
      {
        this(LifeCycle.AROUND_METHOD, configLocations);
      }

    @BeforeMethod
    public final void beforeMethod()
      {
        if (lifeCycle == LifeCycle.AROUND_METHOD)
          {
            createSpringContext();
          }
      }

    @AfterMethod(timeOut = 60000)
    public final void afterMethod()
      {
        if (lifeCycle == LifeCycle.AROUND_METHOD)
          {
            closeSpringContext();
          }
      }

    @BeforeClass
    public final void beforeClass()
      {
        if (lifeCycle == LifeCycle.AROUND_CLASS)
          {
            createSpringContext();
          }
      }

    @AfterClass(timeOut = 60000)
    public final void afterClass()
      {
        if (lifeCycle == LifeCycle.AROUND_CLASS)
          {
            closeSpringContext();
          }
      }

    private void createSpringContext()
      {
        log.info("Spring configuration locations: {}", Arrays.toString(configLocations));
        context = new ClassPathXmlApplicationContext(configLocations);
        log.info(">>>> bean names: {}", Arrays.toString(context.getBeanDefinitionNames()));
      }

    private void closeSpringContext()
      {
        log.info("Closing Spring context...");
        context.close();
        context = null; // don't keep in memory useless stuff
        ContextManager.Locator.set(null);
      }
  }
