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
package it.tidalwave.bluemarine2.upnp.mediaserver.mock.impl;

import java.beans.PropertyChangeSupport;
import lombok.extern.slf4j.Slf4j;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@UpnpService
  (
    serviceId = @UpnpServiceId("UPnPServerMock"),
    serviceType = @UpnpServiceType(value = "UPnPServerMock", version = 1)
  )
@Slf4j
public class UPnPServerMock
  {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private boolean target = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false;

    @UpnpAction
    public void setTarget (@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue)
      {
        log.info("setTarget({})", newTargetValue);
        final boolean targetOldValue = target;
        final boolean statusOldValue = status;
        target = newTargetValue;
        status = newTargetValue;

        // These have no effect on the UPnP monitoring but it's JavaBean compliant
        pcs.firePropertyChange("target", targetOldValue, target);
        pcs.firePropertyChange("status", statusOldValue, status);

        // This will send a UPnP event, it's the name of a state variable that sends events
        pcs.firePropertyChange("Status", statusOldValue, status);
      }

    @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
    public boolean getTarget()
      {
        return target;
      }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus()
      {
        return status;
      }
  }
