/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.device;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Optional;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultDevicePublisher<T> implements DevicePublisher<T>
  {
    @Inject
    private UpnpService upnpService;

    @Inject
    private AutowireCapableBeanFactory beanFactory;

    private final Class<?> serviceClass;

    private LocalService service;

    private DefaultServiceManager<T>  serviceManager;

    private final AnnotationLocalServiceBinder serviceBinder = new AnnotationLocalServiceBinder();

    @Getter @Setter
    private boolean autoPublish = true;

    @Getter @Setter
    private UDN udn;

    @Getter @Setter
    private String friendlyName = "bm II";

    @Getter @Setter
    private boolean useHostNameInFriendlyName = true;

    @Getter @Setter
    private String serialNumber = "n.a.";

    @Getter @Setter
    private String version = "n.a.";

    @Getter @Setter
    private UDADeviceType udaDeviceType = new UDADeviceType("MediaServer", 1);

    @Getter @Setter
    private int maxAge = 1800;

    @Getter @Setter
    private String modelName = "blueMarine II media server";

    @Getter @Setter
    private String presentationUri;

    @Getter @Setter
    private ManufacturerDetails manufacturerDetails = new ManufacturerDetails("Tidalwave s.a.s.", "http://tidalwave.it");

    @Getter @Setter
    private ModelDetails modelDetails = new ModelDetails(modelName,
                                                         modelName,
                                                         version,
                                                         "http://bluemarine.tidalwave.it");

    @Getter @Setter
    private List<DLNADoc> dlnaDocs = Arrays.asList
      (
        new DLNADoc("DMS", DLNADoc.Version.V1_5),
        new DLNADoc("M-DMS", DLNADoc.Version.V1_5)
      );

    @Getter @Setter
    private List<String> dlnaCaps = Arrays.asList
      (
        "av-upload", "image-upload", "audio-upload"
      );

    @Getter @Setter
    private List<Icon> icons = Collections.emptyList();

    private LocalDevice device;

    private ValidationException exception;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public LocalDevice getDevice()
      throws ValidationException
      {
        if (exception != null)
          {
            throw exception;
          }

        assert device != null;

        return device;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void publishDevice()
      throws ValidationException
      {
        try
          {
            udn = UDN.uniqueSystemIdentifier("1");
            service = serviceBinder.read(serviceClass);
            serviceManager = new AutowireServiceManager<>(beanFactory, service, serviceClass);
            service.setManager(serviceManager);
            log.info("publishDevice() - {}", service);
            final DeviceDetails deviceDetails = new DeviceDetails(computeFriendyName(),
                                                                  manufacturerDetails,
                                                                  modelDetails,
                                                                  serialNumber,
                                                                  null, // UPC
                                                                  Optional.ofNullable(presentationUri).map(URI::create).orElse(null),
                                                                  dlnaDocs.toArray(new DLNADoc[0]),
                                                                  new DLNACaps(dlnaCaps.toArray(new String[0])));
            device = new LocalDevice(new DeviceIdentity(udn, maxAge),
                                     udaDeviceType,
                                     deviceDetails,
                                     icons.toArray(new Icon[0]),
                                     service);
            upnpService.getRegistry().addDevice(device);
          }
        catch (ValidationException e)
          {
            this.exception = e;
            throw e;
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    private void initialize()
      {
        if (autoPublish)
          {
            try
              {
                publishDevice();
              }
            catch (RuntimeException | ValidationException e)
              {
                log.error("Could not publish device", e);
              }
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private String computeFriendyName()
      {
        final StringBuilder buffer = new StringBuilder(friendlyName);

        if (useHostNameInFriendlyName)
          {
            try
              {
                buffer.append(" (").append(InetAddress.getLocalHost().getCanonicalHostName()).append(")");
              }
            catch (UnknownHostException e)
              {
                log.warn("Cannot get host name", e);
              }
          }

        return buffer.toString();
      }
  }
