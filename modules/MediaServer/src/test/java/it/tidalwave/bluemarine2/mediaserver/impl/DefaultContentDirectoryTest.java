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
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import it.tidalwave.bluemarine2.model.Entity;
import static it.tidalwave.role.Displayable.Displayable;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DefaultContentDirectoryTest
  {
    private ApplicationContext context;

    private DefaultContentDirectory underTest;

    @BeforeMethod
    public void setup()
      {
        context = new ClassPathXmlApplicationContext("META-INF/DciBeans.xml", "META-INF/DefaultDevicePublisherTest.xml");
        underTest = context.getBean(DefaultContentDirectory.class);
      }

    @Test
    public void must_return_correct_root_children()
      {
        List<? extends Entity> children = underTest.findRoot().findChildren().results();
        assertThat(children.size(), is(4));
        assertThat(children.get(0).as(Displayable).getDisplayName(), is("Music Library"));
        assertThat(children.get(1).as(Displayable).getDisplayName(), is("Photo Library"));
        assertThat(children.get(2).as(Displayable).getDisplayName(), is("Video Library"));
        assertThat(children.get(3).as(Displayable).getDisplayName(), is("Services"));
      }
}
