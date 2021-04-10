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
package it.tidalwave.bluemarine2.service.vimeo.impl;

import it.tidalwave.bluemarine2.upnp.mediaserver.impl.ClingTestSupport;
import org.testng.annotations.Test;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class VimeoMediaServerServiceTest extends ClingTestSupport
  {
    public VimeoMediaServerServiceTest()
      {
        super("classpath*:META-INF/DciAutoBeans.xml",
              "classpath*:META-INF/CommonsAutoBeans.xml",
              "classpath*:META-INF/VimeoServiceAutoBeans.xml",
              "classpath*:META-INF/MediaServerAutoBeans.xml",
              "classpath*:META-INF/UPnPAutoBeans.xml");
      }

    @Test
    public void test_deploy()
      throws InterruptedException
      {
        delay();
      }
  }
