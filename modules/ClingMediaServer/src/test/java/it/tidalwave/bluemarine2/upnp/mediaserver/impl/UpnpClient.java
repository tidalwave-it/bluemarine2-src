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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import java.util.Arrays;
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
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;

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
            final Service remoteService = device.findService(serviceId);

            if (remoteService != null)
              {
                final DeviceDetails details = remoteService.getDevice().getDetails();
                final ManufacturerDetails manufacturerDetails = details.getManufacturerDetails();
                final ModelDetails modelDetails = details.getModelDetails();
                log.info("New service discovered: {}", remoteService);
                log.info(">>>>           baseURL: {}", details.getBaseURL());
                log.info(">>>>         DLNA caps: {}", details.getDlnaCaps());
                log.info(">>>>         DLNA docs: {}", Arrays.toString(details.getDlnaDocs()));
                log.info(">>>>     friendly name: {}", details.getFriendlyName());
                log.info(">>>>      manufacturer: {}", manufacturerDetails.getManufacturer());
                log.info(">>>>  manufacturer URI: {}", manufacturerDetails.getManufacturerURI());
                log.info(">>>> model description: {}", modelDetails.getModelDescription());
                log.info(">>>>        model name: {}", modelDetails.getModelName());
                log.info(">>>>      model number: {}", modelDetails.getModelNumber());
                log.info(">>>>         model URI: {}", modelDetails.getModelURI());
                log.info(">>>>  presentation URI: {}", details.getPresentationURI());
                log.info(">>>>  sec product caps: {}", details.getSecProductCaps());
                log.info(">>>>     serial number: {}", details.getSerialNumber());
                log.info(">>>>               UPC: {}", details.getUpc());
                service.set(remoteService);
              }
          }

        @Override
        public void remoteDeviceRemoved (final @Nonnull Registry registry, final @Nonnull RemoteDevice device)
          {
            final Service remoteService = device.findService(serviceId);

            if (remoteService != null)
              {
                log.info("Service removed: {}", remoteService);
                service.compareAndSet(remoteService, null);
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
