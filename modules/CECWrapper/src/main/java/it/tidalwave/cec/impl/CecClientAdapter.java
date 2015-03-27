/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.cec.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import it.tidalwave.util.ProcessExecutor;
import it.tidalwave.util.ProcessExecutor.ConsoleOutput;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.spi.DefaultProcessExecutor;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.cec.CecEvent;
import it.tidalwave.cec.CecEvent.EventType;
import it.tidalwave.bluemarine2.ui.commons.PowerOnNotification;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An adapter that receives notifications from {@code cec-client} and forwards events to the message bus.
 * 
 * @stereotype  Adapter
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class CecClientAdapter 
  {
    private static final String CEC_REGEX = "^TRAFFIC: *\\[ *([0-9]+)\\][ \\t>]*([0-9A-Fa-f]+):([0-9A-Fa-f]+):([0-9A-Fa-f]+)$";
    
    private static final Pattern CEC_PATTERN = Pattern.compile(CEC_REGEX);
    
    private ProcessExecutor executor;
    
    @Inject
    private MessageBus messageBus;
    
    /*******************************************************************************************************************
     *
     * Parses the output from {@code cec-client} and fires events.
     *
     ******************************************************************************************************************/
    private final ConsoleOutput.Listener listener = string ->
      {
        final Matcher matcher = CEC_PATTERN.matcher(string);
        
        if (matcher.matches())
          {
            final int eventType = Integer.parseInt(matcher.group(3), 16);
            final int keyCode = Integer.parseInt(matcher.group(4), 16);
            
            try
              {
                final CecEvent event = EventType.forCode(eventType).createEvent(keyCode);
                log.debug("Sending {}...", event);
                messageBus.publish(event);
              }
            catch (NotFoundException e)
              {
                log.warn("Not found: {}", e.getMessage());
              }
          }
      };
    
    /*******************************************************************************************************************
     *
     * At power on runs {@code cec-client}.
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPowerOnReceived (final @Nonnull @ListensTo PowerOnNotification notification)
      {
        try 
          {
            log.info("onPowerOnReceived({})");
            
            executor = DefaultProcessExecutor.forExecutable("/usr/local/bin/cec-client") // FIXME: path
                                             .withArguments("-d", "8", "-t", "p", "-o", "blueMarine")
                                             .start();
            executor.getStdout().setListener(listener);
          }
        catch (IOException e) 
          {
            log.error("Cannot run cec-client: {}", e.toString());
            // TODO: UI notification of the error
          }
      }
    
    // TODO: kill executor on PowerOff?
  }
