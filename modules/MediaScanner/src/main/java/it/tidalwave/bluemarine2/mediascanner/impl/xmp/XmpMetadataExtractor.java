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
package it.tidalwave.bluemarine2.mediascanner.impl.xmp;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;

/***********************************************************************************************************************
 *
 * This class is a SAX Handler that extracts metadata from an XMP file, applies mappings defined in a configuration file
 * to eventually associate each metadata item to a pair (key, value) and calls a {@link BiConsumer} to process them.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j @NotThreadSafe
public class XmpMetadataExtractor extends DefaultHandler
  {
    /** These attributes aren't related to metadata items, but to encoding. */
    private static final Set<String> IGNORED_ATTRS = Set.of("xml:lang", "rdf:about", "rdf:parseType");

    /** A map from XPath expressions to mapped tags. */
    private static final PathMapper PATH_MAPPER;

    static
      {
        try
          {
            PATH_MAPPER = new PathMapper(loadStrings("mapped-tags.txt"));
          }
        catch (IOException e)
          {
            throw new ExceptionInInitializerError(e);
          }
      }

    @Nonnull
    private final BiConsumer<String, String> metadataProcessor;

    private final Deque<String> xpathStack = new LinkedList<>();

    private final Deque<Attributes> attrStack = new LinkedList<>();

    private final Deque<StringBuilder> textStack = new LinkedList<>();

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void startElement (@Nonnull final String uri,
                              @Nonnull final String localName,
                              @Nonnull final String qName,
                              @Nonnull final Attributes attributes)
      {
        // FIXME: this assumes QName is using the standard prefix (e.g. 'exif'). More robust code
        // should instead read the namespace and translate to a prefix.
        xpathStack.push(qName);
        attrStack.push(attributes);
        textStack.push(new StringBuilder());

        for (int i = 0; i < attributes.getLength(); i++)
          {
            final String attrName = attributes.getQName(i);

            if (!IGNORED_ATTRS.contains(attrName))
              {
                processXpath(stackToXpath(xpathStack) + "/@" + attrName, attributes.getValue(i));
              }
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void endElement (@Nonnull final String uri, @Nonnull final String localName, @Nonnull final String qName)
      {
        final String text = textStack.pop().toString().replaceAll("\n", " ").trim();

        if (!"".equals(text))
          {
            processXpath(stackToXpath(xpathStack), text);
          }

        attrStack.pop();
        xpathStack.pop();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void characters (@Nonnull final char[] chars, @Nonnegative final int start, @Nonnegative final int length)
      {
        textStack.peek().append(new String(chars, start, length));
      }

    /*******************************************************************************************************************
     *
     * Processes an Xpath.
     *
     * @param xpath   the XPath of the metadata item
     * @param value   the metadata item value
     *
     ******************************************************************************************************************/
    private void processXpath (@Nonnull final String xpath, @Nonnull final String value)
      {
        // Ignored xpaths helps in reducing these warnings, which could have a dramatic performance hit
        PATH_MAPPER.getTokenFor(xpath).ifPresentOrElse(token -> addItem(token, value),
                                                       () -> log.warn("Ignoring {} = *{}*", xpath, value));
      }

    /*******************************************************************************************************************
     *
     * Processes a metadata item.
     *
     * @param item    the metadata item
     * @param value   the metadata item value
     *
     ******************************************************************************************************************/
    private void addItem (@Nonnull final String item, @Nonnull final String value)
      {
        if (!item.equals(""))
          {
            try
              {
                metadataProcessor.accept(item, value);
              }
            catch (Exception e)
              {
                log.warn("Can't handle {} because of {}", item, e.toString());
              }
          }
      }

    /*******************************************************************************************************************
     *
     * Converts a stack of element names into a Xpath.
     *
     * @param stack   the stack
     * @return        the Xpath
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String stackToXpath (@Nonnull final Collection<String> stack)
      {
        final List<String> tmp = new ArrayList<>(stack);
        Collections.reverse(tmp);
        return "/" + String.join("/", tmp);
      }

    /*******************************************************************************************************************
     *
     * Loads a list of strings from a resource file.
     *
     * @param resourceName    the name of the file
     * @return                the list of strings
     * @throws IOException    in case of error
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<String> loadStrings (@Nonnull final String resourceName)
            throws IOException
      {
        return Arrays.asList(IOUtils.toString(XmpMetadataExtractor.class.getResource(resourceName), UTF_8).split("\n"));
      }
  }
