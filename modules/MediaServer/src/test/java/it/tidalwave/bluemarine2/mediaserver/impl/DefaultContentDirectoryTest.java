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
package it.tidalwave.bluemarine2.mediaserver.impl;

import java.util.List;
import it.tidalwave.bluemarine2.model.spi.Entity;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import it.tidalwave.bluemarine2.commons.test.SpringTestSupport;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectoryTest extends SpringTestSupport
  {
    private DefaultContentDirectory underTest;

    public DefaultContentDirectoryTest()
      {
        super("META-INF/DciAutoBeans.xml",
              "META-INF/DefaultContentDirectoryTestBeans.xml");
      }

    @BeforeMethod
    public void setup()
      {
        underTest = context.getBean(DefaultContentDirectory.class);
      }

    @Test
    public void must_return_correct_root_children()
      throws Exception
      {
        // when
        final List<? extends Entity> children = underTest.findRoot().findChildren().results();
        // then
        assertThat(children.size(), is(4));
        assertThat(children.get(0).as(_Displayable_).getDisplayName(), is("Music"));
        assertThat(children.get(1).as(_Displayable_).getDisplayName(), is("Photos"));
        assertThat(children.get(2).as(_Displayable_).getDisplayName(), is("Videos"));
        assertThat(children.get(3).as(_Displayable_).getDisplayName(), is("Services"));

        // TODO: mock music and services, assert contents
      }
  }
