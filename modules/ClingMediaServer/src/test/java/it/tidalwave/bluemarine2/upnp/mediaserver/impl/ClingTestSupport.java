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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnull;
import org.fourthline.cling.UpnpService;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import it.tidalwave.bluemarine2.util.SystemConfigurer;
import org.testng.annotations.BeforeClass;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class ClingTestSupport extends SpringTestSupport
  {
    protected UpnpService upnpService;

    static
      {
        SystemConfigurer.setupSlf4jBridgeHandler();
      }

    protected ClingTestSupport (final @Nonnull String ... configLocations)
      {
        super(LifeCycle.AROUND_CLASS, configLocations);
      }

    @BeforeClass
    public final void setupJulLoggingBridge()
      {
        upnpService = context.getBean(UpnpService.class);
      }

    protected void delay()
      throws InterruptedException
      {
        Thread.sleep(Long.getLong("delay", 2000));
      }
  }
