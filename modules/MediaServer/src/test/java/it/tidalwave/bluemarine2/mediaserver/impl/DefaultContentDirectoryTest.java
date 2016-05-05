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

package it.tidalwave.bluemarine2.mediaserver.impl;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.bluemarine2.model.Entity;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultContentDirectoryTest
  {
    private ApplicationContext context;

    private DefaultContentDirectory underTest;

    @BeforeMethod
    public void setup()
      {
        context = new ClassPathXmlApplicationContext("META-INF/DciAutoBeans.xml",
                                                     "META-INF/DefaultDevicePublisherTest.xml");
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
        assertThat(children.get(0).as(Displayable).getDisplayName(), is("Music"));
        assertThat(children.get(1).as(Displayable).getDisplayName(), is("Photos"));
        assertThat(children.get(2).as(Displayable).getDisplayName(), is("Videos"));
        assertThat(children.get(3).as(Displayable).getDisplayName(), is("Services"));

        // TODO: mock music and services, assert contents
      }
  }
