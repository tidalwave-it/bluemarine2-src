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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.spi.FactoryBasedEntityFinder;
import static it.tidalwave.bluemarine2.service.stoppingdown.impl.PhotoCollectionProviderSupport.XPATH_FACTORY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import static javax.xml.xpath.XPathConstants.NODESET;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DiaryPhotoCollectionProvider extends PhotoCollectionProviderSupport
  {
    @Override @Nonnull
    public EntityFinder findPhotos (final @Nonnull MediaFolder parent)
      {
        return new FactoryBasedEntityFinder(parent,
                p -> findPhotos(p, "http://stoppingdown.net/private/diary/20160407/images.xml"));
      }

    private static final String URL_DIARY = "http://stoppingdown.net/diary/";

//    private static final String URL_GALLERY_TEMPLATE = "http://stoppingdown.net%s/images.xml";

    private static final XPathExpression XPATH_DIARY_EXPR;

    @Nonnull
    private final String themesUrl;

    /**
     * A local cache for themes is advisable because multiple calls will be performed.
     */
    private final Map<Integer, List<GalleryDescription>> diaryCache = new ConcurrentHashMap<>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static
      {
        try
          {
            final XPath xpath = XPATH_FACTORY.newXPath();
            XPATH_DIARY_EXPR = xpath.compile("//div[@class='nw-calendar']//li/a");
          }
        catch (XPathExpressionException e)
          {
            throw new ExceptionInInitializerError(e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DiaryPhotoCollectionProvider()
      {
        this(URL_DIARY);
      }

    /*******************************************************************************************************************
//     *
//     ******************************************************************************************************************/
//    @Override
//    @Nonnull
//    public EntityFinder findPhotos (final @Nonnull MediaFolder parent)
//      {
//        return new FactoryBasedEntityFinder(parent, p -> Arrays.asList(
//                new VirtualMediaFolder(p, PATH_PLACES,   "Places",   this::placesFactory),
//                new VirtualMediaFolder(p, PATH_SUBJECTS, "Subjects", this::subjectsFactory)));
//      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected void clearCachesImpl()
      {
        super.clearCachesImpl();
        diaryCache.clear();
      }

//    /*******************************************************************************************************************
//     *
//     ******************************************************************************************************************/
//    @Nonnull
//    private Collection<Entity> subjectsFactory (final @Nonnull MediaFolder parent)
//      {
//        return parseThemes(XPATH_DIARY_EXPR).stream()
//                                                         .map(gallery -> gallery.createFolder(parent, this::findPhotos))
//                                                         .collect(toList());
//      }
//
//    /*******************************************************************************************************************
//     *
//     ******************************************************************************************************************/
//    @Nonnull
//    private Collection<Entity> placesFactory (final @Nonnull MediaFolder parent)
//      {
//        return parseThemes(XPATH_PLACES_THUMBNAIL_EXPR).stream()
//                                                       .map(gallery -> gallery.createFolder(parent, this::findPhotos))
//                                                       .collect(toList());
//      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    /* VisibleForTesting */ List<GalleryDescription> parseDiary()
      {
        log.debug("parseThemes({})", themesUrl);

        return diaryCache.computeIfAbsent(2016, key ->
          {
            try
              {
                final Document document = downloadXml(themesUrl);
                final NodeList thumbnailNodes = (NodeList)XPATH_DIARY_EXPR.evaluate(document, NODESET);
                final List<GalleryDescription> galleryDescriptions = new ArrayList<>();

                for (int i = 0; i < thumbnailNodes.getLength(); i++)
                  {
                    final Node thumbnailNode = thumbnailNodes.item(i);
                    final String url = getAttribute(thumbnailNode, "href");
                    String date = url.substring(url.length() - 11, url.length() - 1);
                    final String displayName = date + " - " + thumbnailNode.getTextContent();
                    galleryDescriptions.add(new GalleryDescription(displayName, url.replace("//", "/")
                                                                                   .replace(":/", "://")));
                  }

                Collections.sort(galleryDescriptions, Comparator.comparing(GalleryDescription::getUrl));

                return galleryDescriptions;
              }
            catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e)
              {
                throw new RuntimeException(e);
              }
          });
      }
  }
