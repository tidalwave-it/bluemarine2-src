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
package it.tidalwave.cec;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import it.tidalwave.util.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @RequiredArgsConstructor @ToString
public class CecEvent 
  {
    @RequiredArgsConstructor @Getter
    public enum KeyDirection
      {
        KEY_PRESSED(0x44),
        KEY_RELEASED(0x8b);
        
        private final int code;  
        
        @Nonnull
        public static KeyDirection findByCode (final int code) 
          throws NotFoundException
          {
            for (final KeyDirection keyDirection : values())
              {
                if (keyDirection.getCode() == code)
                  {
                    return keyDirection;  
                  }
              }
            
            throw new NotFoundException("CEC key direction: " + Integer.toHexString(code));
          } 
      }

    @RequiredArgsConstructor @Getter
    public enum KeyCode
      {
        CENTER(0x00),
        UP(0x01),
        DOWN(0x02),
        LEFT(0x03),
        RIGHT(0x04),
        EXIT(0x0d);
        
        private final int code;  
        
        @Nonnull
        public static KeyCode findByCode (final int code) 
          throws NotFoundException
          {
            for (final KeyCode keycode : values())
              {
                if (keycode.getCode() == code)
                  {
                    return keycode;  
                  }
              }
            
            throw new NotFoundException("CEC key code: " + Integer.toHexString(code));
          } 
      }
    
    @Nonnull
    private final KeyCode keyCode;
    
    @Nonnull
    private final KeyDirection keyDirection;
  }
