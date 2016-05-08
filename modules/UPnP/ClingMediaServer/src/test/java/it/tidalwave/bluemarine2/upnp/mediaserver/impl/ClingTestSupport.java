/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnull;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.fourthline.cling.UpnpService;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
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
public class ClingTestSupport extends SpringTestSupport
  {
    protected UpnpService upnpService;

    protected ClingTestSupport (final @Nonnull String ... configLocations)
      {
        super(configLocations);
      }

    @BeforeClass
    public final void setupJulLoggingBridge()
      {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
      }

    @BeforeMethod
    public final void setupCling()
      {
        upnpService = context.getBean(UpnpService.class);
      }

    protected void delay()
      throws InterruptedException
      {
        Thread.sleep(Long.getLong("delay", 2000));
      }
  }
