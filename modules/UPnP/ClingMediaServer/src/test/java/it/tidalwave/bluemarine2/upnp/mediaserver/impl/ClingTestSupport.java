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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.fourthline.cling.UpnpService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class ClingTestSupport
  {
    protected ClassPathXmlApplicationContext context;

    protected UpnpService upnpService;

    private final String[] configLocations;

    protected ClingTestSupport (final @Nonnull String ... configLocations)
      {
        final List<String> list = new ArrayList<>(Arrays.asList(configLocations));
        list.add(0, "META-INF/DciBeans.xml"); // for DCI injectors
        this.configLocations = list.toArray(new String[0]);
        log.info(">>>> Spring configuration locations: {}", (Object[])this.configLocations);
      }

    @BeforeClass
    public void setup()
      {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        context = new ClassPathXmlApplicationContext(configLocations);
        upnpService = context.getBean(UpnpService.class);
      }

    @AfterClass
    public void shutdown()
      {
        log.info("Shutting down...");
        upnpService.shutdown();
      }

    protected void delay()
      throws InterruptedException
      {
        Thread.sleep(Long.getLong("delay", 2000));
      }
  }
