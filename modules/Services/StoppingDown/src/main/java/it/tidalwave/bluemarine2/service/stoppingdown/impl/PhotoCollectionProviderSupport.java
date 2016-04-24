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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.spi.SupplierBasedEntityFinder;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PhotoCollectionProviderSupport implements PhotoCollectionProvider
  {
    protected static final DocumentBuilderFactory PARSER_FACTORY = DocumentBuilderFactory.newInstance();

    protected static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    private static final XPathExpression XPATH_STILLIMAGE_EXPR;

    /**
     * A local cache for finders. It's advisable, since clients will frequently retrieve a finder because of pagination.
     */
    private final Map<String, EntityFinder> finderCache = new HashMap<>();

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
    public EntityFinder findPhotos (final @Nonnull MediaFolder parent)
      {
        throw new UnsupportedOperationException("must be implemented in subclasses");
      }

    /*******************************************************************************************************************
     *
     * Retrieves a finder for the given gallery URL, possibly a previously cached one.
     *
     * @param   parent      the parent node
     * @param   galleryUrl  the gallery URL
     * @return              the finder
     *
     ******************************************************************************************************************/
    @Nonnull
    protected EntityFinder findCachedPhotos (final @Nonnull MediaFolder parent, final @Nonnull String galleryUrl)
      {
        return finderCache.computeIfAbsent(galleryUrl, u -> findPhotos(parent, u));
      }

    /*******************************************************************************************************************
     *
     * Creates a finder for the given gallery URL.
     *
     * @param   parent      the parent node
     * @param   galleryUrl  the gallery URL
     * @return              the finder
     *
     ******************************************************************************************************************/
    @Nonnull
    /* VisibleForTesting */ EntityFinder findPhotos (final @Nonnull MediaFolder parent,
                                                     final @Nonnull String galleryUrl)
      {
        try
          {
            final DocumentBuilder builder = PARSER_FACTORY.newDocumentBuilder();
            final Document doc = builder.parse(galleryUrl);
            final NodeList nodes = (NodeList)XPATH_STILLIMAGE_EXPR.evaluate(doc, XPathConstants.NODESET);

            final Collection<Entity> photoItems = new ArrayList<>();

            for (int i = 0; i < nodes.getLength(); i++)
              {
                final Node node = nodes.item(i);
                final String id = getAttribute(node, "id");
                final String title = getAttribute(node, "title");
                photoItems.add(new PhotoItem(parent, id, title));
              }

            return new SupplierBasedEntityFinder(parent, () -> photoItems);
          }
        catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static String getAttribute (final @Nonnull Node node, final @Nonnull String attrName)
      throws DOMException
      {
        return node.getAttributes().getNamedItem(attrName).getNodeValue();
      }
  }
