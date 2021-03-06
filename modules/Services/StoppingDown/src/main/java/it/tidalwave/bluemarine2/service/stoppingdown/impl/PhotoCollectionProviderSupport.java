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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.spi.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.PathAwareFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class PhotoCollectionProviderSupport implements PhotoCollectionProvider
  {
    protected static final String URL_STOPPINGDOWN = System.getProperty("stoppingdown", "http://stoppingdown.net");

    protected static final String URL_GALLERY_TEMPLATE = "%s%s/images.xml";

    protected static final DocumentBuilderFactory PARSER_FACTORY = DocumentBuilderFactory.newInstance();

    // FIXME: XPath stuff is not thread-safe - fix!
    protected static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    private static final XPathExpression XPATH_STILLIMAGE_EXPR;

    @Nonnull
    protected final String baseUrl;

    /**
     * A local cache for finders. It's advisable, since clients will frequently retrieve a finder because of pagination.
     */
    private final Map<String, Collection<PathAwareEntity>> photoCollectionCache = new ConcurrentHashMap<>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static
      {
        try
          {
            final XPath xpath = XPATH_FACTORY.newXPath();
            XPATH_STILLIMAGE_EXPR = xpath.compile("/gallery/stillImage");
          }
        catch (XPathExpressionException e)
          {
            throw new ExceptionInInitializerError(e);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public PathAwareFinder findPhotos (@Nonnull final MediaFolder parent)
      {
        throw new UnsupportedOperationException("must be implemented in subclasses");
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Scheduled(fixedDelay = 14_400_000) // 12 hours TODO: yes, can use properties here
    private void clearCaches()
      {
        log.info("clearCaches()");
        clearCachesImpl();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    protected void clearCachesImpl()
      {
        photoCollectionCache.clear();
      }

    /*******************************************************************************************************************
     *
     * Creates a collection of entities for the given gallery URL.
     *
     * @param   parent      the parent node
     * @param   galleryUrl  the gallery URL
     * @return              the collection of entities
     *
     ******************************************************************************************************************/
    @Nonnull
    @VisibleForTesting Collection<PathAwareEntity> findPhotos (@Nonnull final MediaFolder parent,
                                                               @Nonnull final String galleryUrl)
      {
        log.debug("findPhotos({}, {}", parent, galleryUrl);

        return photoCollectionCache.computeIfAbsent(galleryUrl, u ->
          {
            try
              {
                final Document document = downloadXml(galleryUrl);
                final NodeList nodes = (NodeList)XPATH_STILLIMAGE_EXPR.evaluate(document, XPathConstants.NODESET);

                final Collection<PathAwareEntity> photoItems = new ArrayList<>();

                for (int i = 0; i < nodes.getLength(); i++)
                  {
                    final Node node = nodes.item(i);
                    final String id = getAttribute(node, "id");
                    final String title = getAttribute(node, "title");
                    photoItems.add(new PhotoItem(parent, id, title));
                  }

                return photoItems;
              }
            catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e)
              {
                throw new RuntimeException(e);
              }
          });
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    // FIXME: implement a local cache on disk
    @Nonnull
    protected Document downloadXml (@Nonnull String url)
      throws SAXException, ParserConfigurationException, IOException
      {
        log.info("downloadXml({})", url);

        url = url.replaceAll("(^.*)\\/([0-9]{2})-([0-9]{2})\\/(.*)$", "$1/$2/$3/$4");

        if (url.startsWith("file:") && url.endsWith("/")) // To support local test resources
          {
            url += "/index.xhtml";
          }

        return PARSER_FACTORY.newDocumentBuilder().parse(url);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static String getAttribute (@Nonnull final Node node, @Nonnull final String attrName)
      throws DOMException
      {
        return node.getAttributes().getNamedItem(attrName).getNodeValue();
      }
  }
