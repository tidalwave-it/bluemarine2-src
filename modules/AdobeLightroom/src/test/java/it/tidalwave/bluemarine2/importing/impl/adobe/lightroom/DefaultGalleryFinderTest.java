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
package it.tidalwave.bluemarine2.importing.impl.adobe.lightroom;

import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom.impl.DefaultGallery;
import it.tidalwave.bluemarine2.importing.impl.adobe.lightroom.impl.DefaultGalleryFinder;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id:$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultGalleryFinderTest
  {
    private static final String DATABASE_FILE_NAME = "Fabrizio Lightroom Catalog.lrcat";

    private EntityManagerFactory emf;

    private EntityManager em;

    @BeforeMethod
    public void setup()
      {
        final Properties properties = new Properties();
        final String databaseFile = Paths.get("target", DATABASE_FILE_NAME).toAbsolutePath().toString();
        log.info("Connecting to database: {}", databaseFile);
        properties.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + databaseFile);
        emf = Persistence.createEntityManagerFactory("ADOBE_LIGHTROOM_PU", properties);
        em = emf.createEntityManager();
      }

    @AfterMethod
    public void cleanUp()
      {
        em.close();
        emf.close();
      }

    @Test
    public void must_correctly_query_galleries()
      throws Exception
      {
        // given
        final DefaultGalleryFinder underTest = new DefaultGalleryFinder(em);
        // when
        final List<? extends Gallery> results = underTest.results();
        // then
        assertThat(results.size(), is(305));
        results.stream().forEach(c -> log.info(">>>> {} / {} / {} / {}",
                c.getParent().flatMap(Gallery::getParent).flatMap(Gallery::getParent).map(Gallery::getName).orElse(""),
                c.getParent().flatMap(Gallery::getParent).map(Gallery::getName).orElse(""),
                c.getParent().map(Gallery::getName).orElse(""),
                c.getName()));
      }
  }
