/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;
import lombok.extern.slf4j.Slf4j;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static javax.xml.xpath.XPathConstants.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class ThemesPhotoCollectionProvider extends PhotoCollectionProviderSupport
  {
    private static final String URL_THEMES_TEMPLATE = "%s/themes/";

    private static final Path PATH_SUBJECTS = Paths.get("subjects");

    private static final Path PATH_PLACES = Paths.get("places");

    /* VisibleForTesting */ static final XPathExpression XPATH_SUBJECTS_THUMBNAIL_EXPR;

    /* VisibleForTesting */ static final XPathExpression XPATH_PLACES_THUMBNAIL_EXPR;

    private static final XPathExpression XPATH_THUMBNAIL_URL_EXPR;

    private static final XPathExpression XPATH_THUMBNAIL_DESCRIPTION_EXPR;

    /**
     * A local cache for themes is advisable because multiple calls will be performed.
     */
    private final Map<XPathExpression, List<GalleryDescription>> themesCache = new ConcurrentHashMap<>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static
      {
        try
          {
            final XPath xpath = XPATH_FACTORY.newXPath();
//            XPATH_SUBJECTS_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/h3[1]/following-sibling::div[@class='thumbnail']");
//            XPATH_PLACES_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/h3[2]/following-sibling::div[@class='thumbnail']");
            XPATH_SUBJECTS_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/div[@class='row'][1]//div[@class='thumbnail']");
            XPATH_PLACES_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/div[@class='row'][2]//div[@class='thumbnail']");
            XPATH_THUMBNAIL_URL_EXPR = xpath.compile("a/@href");
            XPATH_THUMBNAIL_DESCRIPTION_EXPR = xpath.compile(".//div[@class='caption']/h3/text()");
          }
        catch (XPathExpressionException e)
          {
            throw new ExceptionInInitializerError(e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ThemesPhotoCollectionProvider()
      {
        this(URL_STOPPINGDOWN);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ThemesPhotoCollectionProvider (final @Nonnull String baseUrl)
      {
        super(baseUrl);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PathAwareFinder findPhotos (final @Nonnull MediaFolder parent)
      {
        return parent.finderOf(p -> List.of(
                new VirtualMediaFolder(p, PATH_PLACES,   "Places",   this::placesFactory),
                new VirtualMediaFolder(p, PATH_SUBJECTS, "Subjects", this::subjectsFactory)));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected void clearCachesImpl()
      {
        super.clearCachesImpl();
        themesCache.clear();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<PathAwareEntity> subjectsFactory (final @Nonnull MediaFolder parent)
      {
        return parseThemes(XPATH_SUBJECTS_THUMBNAIL_EXPR).stream()
                                                         .map(gallery -> gallery.createFolder(parent, this::findPhotos))
                                                         .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<PathAwareEntity> placesFactory (final @Nonnull MediaFolder parent)
      {
        return parseThemes(XPATH_PLACES_THUMBNAIL_EXPR).stream()
                                                       .map(gallery -> gallery.createFolder(parent, this::findPhotos))
                                                       .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    /* VisibleForTesting */ List<GalleryDescription> parseThemes (final @Nonnull XPathExpression expr)
      {
        final String themeUrl = String.format(URL_THEMES_TEMPLATE, baseUrl);
        log.debug("parseThemes({}, {})", themeUrl, expr);

        return themesCache.computeIfAbsent(expr, key ->
          {
            try
              {
                final Document document = downloadXml(themeUrl);
                final NodeList thumbnailNodes = (NodeList)expr.evaluate(document, NODESET);
                final List<GalleryDescription> galleryDescriptions = new ArrayList<>();

                for (int i = 0; i < thumbnailNodes.getLength(); i++)
                  {
                    final Node thumbnailNode = thumbnailNodes.item(i);
                    final String description = (String)XPATH_THUMBNAIL_DESCRIPTION_EXPR.evaluate(thumbnailNode, STRING);
                    final String href = (String)XPATH_THUMBNAIL_URL_EXPR.evaluate(thumbnailNode, STRING);
                    final String url = String.format(URL_GALLERY_TEMPLATE, baseUrl, href).replace("//", "/")
                                                                                         .replace(":/", "://");
                    galleryDescriptions.add(new GalleryDescription(description, url));
                  }

                galleryDescriptions.sort(comparing(GalleryDescription::getDisplayName));

                return galleryDescriptions;
              }
            catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e)
              {
                throw new RuntimeException(e);
              }
          });
      }
  }
