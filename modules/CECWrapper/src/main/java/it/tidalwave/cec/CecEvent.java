/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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
package it.tidalwave.cec;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.cec.CecUserControlEvent.UserControlCode;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * Abstract base class for all CEC events.
 * 
 * @see http://www.cec-o-matic.com/
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @RequiredArgsConstructor @EqualsAndHashCode @ToString
public abstract class CecEvent 
  {
    /*******************************************************************************************************************
     *
     * Defines event types. 
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter
    public enum EventType
      {
        USER_CONTROL_PRESSED(0x44,  (code) -> new CecUserControlEvent(forCode(0x44), UserControlCode.forCode(code))),
        USER_CONTROL_RELEASED(0x8b, (code) -> new CecUserControlEvent(forCode(0x8b), UserControlCode.forCode(code)));

        interface CecEventFactory
          {
            @Nonnull
            public CecEvent createEvent (int code)
              throws NotFoundException;
          }

        private final int code;  
        
        @Delegate @Nonnull
        private final CecEventFactory eventFactory;
        
        @Nonnull
        public static EventType forCode (final int code) 
          throws NotFoundException
          {
            for (final EventType eventType : values())
              {
                if (eventType.getCode() == code)
                  {
                    return eventType;  
                  }
              }
            
            throw new NotFoundException("CEC event type: " + Integer.toHexString(code));
          } 
      }

    @Nonnull
    private final EventType eventType;
  }
