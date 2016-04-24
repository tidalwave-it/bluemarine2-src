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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.spi.SupplierBasedEntityFinder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import lombok.extern.slf4j.Slf4j;
import static javax.xml.xpath.XPathConstants.*;
import static it.tidalwave.bluemarine2.service.stoppingdown.impl.PhotoCollectionProviderSupport.PARSER_FACTORY;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class ThemesPhotoCollectionProvider extends PhotoCollectionProviderSupport
  {
    private static final String URL_TEMPLATE = "http://stoppingdown.net%s/images.xml";

    /* VisibleForTesting */ static final XPathExpression XPATH_THEMES_THUMBNAIL_EXPR;

    /* VisibleForTesting */  static final XPathExpression XPATH_PLACES_THUMBNAIL_EXPR;

    private static final XPathExpression XPATH_THUMBNAIL_URL_EXPR;

    private static final XPathExpression XPATH_THUMBNAIL_DESCRIPTION_EXPR;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static
      {
        try
          {
            final XPath xpath = XPATH_FACTORY.newXPath();
//            XPATH_THEMES_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/h3[1]/following-sibling::div[@class='thumbnail']");
//            XPATH_PLACES_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/h3[2]/following-sibling::div[@class='thumbnail']");
            XPATH_THEMES_THUMBNAIL_EXPR = xpath.compile("//div[@class='container-fluid']/div[@class='row'][1]//div[@class='thumbnail']");
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
    @Override @Nonnull
    public EntityFinder findPhotos (final @Nonnull MediaFolder parent)
      {
        final List<Entity> children = new ArrayList<>();
        children.add(c(parent, "birds", "Birds"));
        children.add(c(parent, "castles", "Castles"));
        children.add(c(parent, "churches", "Churches & chapels"));
        children.add(c(parent, "clouds", "Clouds"));
        children.add(c(parent, "fog", "Fog"));
        children.add(c(parent, "lonely-trees", "Lonely Trees"));

        return new SupplierBasedEntityFinder(parent, () -> children);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
//    @Nonnull
//    /* VisibleForTesting */ List<GalleryDescription> parseThemes (final @Nonnull String themesUrl)
//      {
//
//      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    /* VisibleForTesting */ List<GalleryDescription> parseThemes (final @Nonnull String themesUrl,
                                                                  final @Nonnull XPathExpression expr)
      {
        log.debug("parseThemes({}, {})", themesUrl, expr);

        try
          {
            final DocumentBuilder builder = PARSER_FACTORY.newDocumentBuilder();
            final Document doc = builder.parse(themesUrl);
            final NodeList thumbnailNodes = (NodeList)expr.evaluate(doc, NODESET);
            final List<GalleryDescription> galleryDescriptions = new ArrayList<>();

            for (int i = 0; i < thumbnailNodes.getLength(); i++)
              {
                final Node thumbnailNode = thumbnailNodes.item(i);
                final String description = (String)XPATH_THUMBNAIL_DESCRIPTION_EXPR.evaluate(thumbnailNode, STRING);
                final String url = (String)XPATH_THUMBNAIL_URL_EXPR.evaluate(thumbnailNode, STRING);
                galleryDescriptions.add(new GalleryDescription(description, String.format(URL_TEMPLATE, url)));
              }

            Collections.sort(galleryDescriptions);

            return galleryDescriptions;
          }
        catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    // FIXME: even though the finder is retrived later, through the supplier, the translation to DIDL does compute
    // the finder because it calls the count() for the children count
    @Nonnull
    private MediaFolder c (final @Nonnull MediaFolder parent,
                           final @Nonnull String path,
                           final @Nonnull String displayName)
      {
        return new VirtualMediaFolder(parent, Paths.get(path),  displayName,
            (p) -> findCachedPhotos(p, String.format("http://stoppingdown.net/themes/%s/images.xml", path)));
      }
  }
