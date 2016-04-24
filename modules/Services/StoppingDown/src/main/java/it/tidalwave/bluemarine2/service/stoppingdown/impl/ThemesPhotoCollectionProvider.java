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
import java.util.Arrays;
import java.util.Collection;
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
import it.tidalwave.bluemarine2.model.spi.FactoryBasedEntityFinder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder.EntityCollectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static javax.xml.xpath.XPathConstants.*;
import static it.tidalwave.bluemarine2.service.stoppingdown.impl.PhotoCollectionProviderSupport.PARSER_FACTORY;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class ThemesPhotoCollectionProvider extends PhotoCollectionProviderSupport
  {
    private static final String URL_TEMPLATE = "http://stoppingdown.net%s/images.xml";

    /* VisibleForTesting */ static final XPathExpression XPATH_SUBJECTS_THUMBNAIL_EXPR;

    /* VisibleForTesting */ static final XPathExpression XPATH_PLACES_THUMBNAIL_EXPR;

    private static final XPathExpression XPATH_THUMBNAIL_URL_EXPR;

    private static final XPathExpression XPATH_THUMBNAIL_DESCRIPTION_EXPR;

    @Nonnull
    private final String themesUrl;

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
    @Override @Nonnull
    public EntityFinder findPhotos (final @Nonnull MediaFolder parent)
      {
        return new FactoryBasedEntityFinder(parent, p -> Arrays.asList(
                new VirtualMediaFolder(p, Paths.get("places"),   "Places",   this::placesFactory),
                new VirtualMediaFolder(p, Paths.get("subjects"), "Subjects", this::subjectsFactory)));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<Entity> subjectsFactory (final @Nonnull MediaFolder parent)
      {
        return parseThemes(XPATH_SUBJECTS_THUMBNAIL_EXPR).stream()
                                                         .map(item -> createMediaFolder(parent, item))
                                                         .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<Entity> placesFactory (final @Nonnull MediaFolder parent)
      {
        return parseThemes(XPATH_PLACES_THUMBNAIL_EXPR).stream()
                                                       .map(item -> createMediaFolder(parent, item))
                                                       .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    /* VisibleForTesting */ List<GalleryDescription> parseThemes (final @Nonnull XPathExpression expr)
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
                galleryDescriptions.add(new GalleryDescription(description, String.format(URL_TEMPLATE, url)
                                                                                  .replace("//", "/")
                                                                                  .replace(":/", "://")));
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
    public MediaFolder createMediaFolder (final @Nonnull MediaFolder parent,
                                          final @Nonnull GalleryDescription galleryDescription)
      {
        final EntityCollectionFactory factory = p -> findCachedPhotos(p, galleryDescription.getUrl());
        return new VirtualMediaFolder(parent,
                                      Paths.get(galleryDescription.getDisplayName()),
                                      galleryDescription.getDisplayName(),
                                      factory);
      }
  }
