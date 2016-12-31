/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class UpnpClient implements Runnable
  {
    private final AtomicReference<Service> service = new AtomicReference<>();

    private final UpnpService upnpService = new UpnpServiceImpl();

    private final ServiceId serviceId;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public UpnpClient (final @Nonnull String serviceName)
      {
        serviceId = new UDAServiceId(serviceName);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public Service getService()
      throws InterruptedException
      {
        // FIXME: wait
        while (service.get() == null)
          {
            log.debug(">>>> waiting for service... (if this runs too long, check the firewall)");
            Thread.sleep(100);
          }

        return service.get();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private final RegistryListener listener = new DefaultRegistryListener()
      {
        @Override
        public void remoteDeviceAdded (final @Nonnull Registry registry, final @Nonnull RemoteDevice device)
          {
            final Service theService = device.findService(serviceId);

            if (theService != null)
              {
                log.info("Service discovered: {}", theService);
                service.set(theService);
              }
          }

        @Override
        public void remoteDeviceRemoved (final @Nonnull Registry registry, final @Nonnull RemoteDevice device)
          {
            final Service theService = device.findService(serviceId);

            if (theService != null)
              {
                log.info("Service removed: {}", theService);
                service.compareAndSet(theService, null);
              }
          }
      };

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Override
    public void run()
      {
        try
          {
            log.info(">>>> starting discovery ...");
            upnpService.getRegistry().addListener(listener);
            upnpService.getControlPoint().search(new STAllHeader());
          }
        catch (Exception e)
          {
            log.error("", e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public void execute (final @Nonnull ActionCallback actionCallback)
      {
        upnpService.getControlPoint().execute(actionCallback);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public void shutdown()
      {
        upnpService.shutdown();
      }
  }
