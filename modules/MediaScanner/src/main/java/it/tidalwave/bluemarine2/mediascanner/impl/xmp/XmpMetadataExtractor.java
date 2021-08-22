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
import java.util.Map;
import java.util.function.BiConsumer;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
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
    /** A map from XPath expressions to mapped tags. */
    private static final Map<String, String> XPATH_TO_TAG_MAP;

    static
      {
        try
          {
            // TODO: use regexp to dramatically shrink config files.
            // E.g.: /x:xmpmeta/rdf:RDF/rdf:Description/exif:([.*]) = exif:$1
            // Note that : is part of the regexp syntax, it should be escaped.
            XPATH_TO_TAG_MAP = loadStrings("mapped-tags.txt")
                    .stream()
                    .filter(s -> !s.isBlank())
                    .map(s -> s.split("="))
                    .collect(toMap(p -> p[0].trim(), p -> p[1].trim()));
            loadStrings("ignored-tags.txt").forEach(s -> XPATH_TO_TAG_MAP.put(s, ""));
          }
        catch (IOException e)
          {
            throw new ExceptionInInitializerError(e);
          }
      }

    @Nonnull
    private final BiConsumer<String, String> metadataProcessor;

    private final Deque<String> xpathStack = new LinkedList<>();

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
        xpathStack.push(qName);
        textStack.push(new StringBuilder());

        for (int i = 0; i < attributes.getLength(); i++)
          {
            // FIXME: this assumes QName is using the standard prefix (e.g. 'exif'). More robust code
            // should instead read the namespace and translate to a prefix.
            final String key = attributes.getQName(i);
            final String value = attributes.getValue(i);
            processItem(stackToString(xpathStack) + "/@" + key, value);
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
        final String value = textStack.pop().toString().replaceAll("\n", " ").trim();

        if (!"".equals(value))
          {
            processItem(stackToString(xpathStack), value);
          }

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
     * Processes a metadata item.
     *
     * @param xpath   the XPath of the metadata item
     * @param value   the metadata item value
     *
     ******************************************************************************************************************/
    private void processItem (@Nonnull final String xpath, @Nonnull final String value)
      {
        final String key = XPATH_TO_TAG_MAP.get(xpath);

        if (key == null)
          {
            // Ignored xpaths helps in reducing these warnings, which could have a dramatic performance hit
            log.warn("Ignoring {} = *{}*", xpath, value);
          }
        else if (!key.equals(""))
          {
            try
              {
                metadataProcessor.accept(key, value);
              }
            catch (Exception e)
              {
                log.error("{}: {}", e.toString(), key);
              }
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String stackToString (@Nonnull final Collection<String> stack)
      {
        final List<String> tmp = new ArrayList<>(stack);
        Collections.reverse(tmp);
        return "/" + String.join("/", tmp);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<String> loadStrings (@Nonnull final String resourceName)
            throws IOException
      {
        return Arrays.asList(IOUtils.toString(XmpMetadataExtractor.class.getResource(resourceName), UTF_8).split("\n"));
      }
  }
