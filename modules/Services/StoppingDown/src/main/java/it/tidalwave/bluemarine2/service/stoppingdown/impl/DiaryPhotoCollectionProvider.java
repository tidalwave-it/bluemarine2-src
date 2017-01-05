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
package it.tidalwave.bluemarine2.service.stoppingdown.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.file.Paths;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.model.finder.EntityFinder;
import it.tidalwave.bluemarine2.model.role.PathAwareEntity;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder;
import it.tidalwave.bluemarine2.model.spi.VirtualMediaFolder.EntityCollectionFactory;
import lombok.extern.slf4j.Slf4j;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static javax.xml.xpath.XPathConstants.NODESET;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DiaryPhotoCollectionProvider extends PhotoCollectionProviderSupport
  {
    private static final String URL_DIARY_TEMPLATE = "%s/diary/%d/";

    private static final String REGEXP_URL_HOST_AND_PORT = "http:\\/\\/[^\\/]*";

    private static final XPathExpression XPATH_DIARY_EXPR;

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
        this(URL_STOPPINGDOWN);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DiaryPhotoCollectionProvider (final @Nonnull String baseUrl)
      {
        super(baseUrl);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public EntityFinder findPhotos(final @Nonnull MediaFolder parent) {
        return parent.finderOf(
                p1 -> IntStream.range(1999, 2016 + 1) // FIXME: use current year
                        .mapToObj(x -> x)
                        .map(year -> new VirtualMediaFolder(p1,
                                Paths.get("" + year),
                                "" + year,
                                (EntityCollectionFactory)(p2 -> entriesFactory(p2, year))))
                        .collect(toList()));
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
        diaryCache.clear();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private Collection<PathAwareEntity> entriesFactory (final @Nonnull MediaFolder parent, final int year)
      {
        return parseDiary(year).stream().map(gallery -> gallery.createFolder(parent, this::findPhotos))
                                        .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    /* VisibleForTesting */ List<GalleryDescription> parseDiary (final int year)
      {
        final String diaryUrl = String.format(URL_DIARY_TEMPLATE, baseUrl, year);
        log.debug("parseDiary({})", diaryUrl);

        return diaryCache.computeIfAbsent(year, key ->
          {
            try
              {
                final Document document = downloadXml(diaryUrl);
                final NodeList entryNodes = (NodeList)XPATH_DIARY_EXPR.evaluate(document, NODESET);
                final List<GalleryDescription> galleryDescriptions = new ArrayList<>();

                for (int i = 0; i < entryNodes.getLength(); i++)
                  {
                    final Node entryNode = entryNodes.item(i);
                    final String href = getAttribute(entryNode, "href").replaceAll(REGEXP_URL_HOST_AND_PORT, "");
                    final String url = String.format(URL_GALLERY_TEMPLATE, baseUrl, href).replace("//", "/")
                                                                                         .replace(":/", "://")
                                               .replaceAll("(^.*)\\/([0-9]{2})\\/([0-9]{2})\\/(.*)$", "$1/$2-$3/$4");
                    final String date = href.substring(href.length() - 11, href.length() - 1);
                    final String displayName = date + " - " + entryNode.getTextContent();
                    galleryDescriptions.add(new GalleryDescription(displayName, url));
                  }

                Collections.sort(galleryDescriptions, comparing(GalleryDescription::getUrl));

                return galleryDescriptions;
              }
            catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e)
              {
                throw new RuntimeException(e);
              }
          });
      }
  }
