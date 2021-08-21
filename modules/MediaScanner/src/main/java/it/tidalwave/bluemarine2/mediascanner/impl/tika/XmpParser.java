package it.tidalwave.bluemarine2.mediascanner.impl.tika;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.PropertyTypeException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XMPContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class XmpParser extends AbstractParser
  {
    @Override
    public Set<MediaType> getSupportedTypes (@Nonnull final ParseContext context)
      {
        return Set.of(MediaType.application("rdf+xml"));
      }

    @Override
    public void parse (@Nonnull final InputStream stream,
                       @Nonnull final ContentHandler handler,
                       @Nonnull final Metadata metadata,
                       @Nonnull final ParseContext context)
            throws IOException, SAXException, TikaException
      {
        metadata.add(Metadata.CONTENT_TYPE, "application/rdf+xml");
        context.getSAXParser().parse(stream, new XMPContentHandler(new DefaultHandler()
          {
            private final List<String> stack = new ArrayList<>();

            @Override
            public void startElement (String uri, String localName, String qName, Attributes attributes)
              {
                //log.debug("START {}", qName);
                stack.add(0, qName);
                //log.debug("XPATH {}", String.join(" / ", stack));

                for (int i = 0; i < attributes.getLength(); i++)
                  {
                    // FIXME: this assumes QName is using the standard prefix (e.g. 'exif'). More robust code
                    // should instead read the namespace and translate to a prefix.
                    final String key = attributes.getQName(i);
                    final String value = attributes.getValue(i);
                    // log.debug("   ATTRS {} = {}", key, value);

                    try
                      {
                        metadata.add(key, value);
                      }
                    catch (PropertyTypeException e)
                      {
                        log.error("{}: {}", e.toString(), key);
                      }
                  }
              }

            public void endElement (String uri, String localName, String qName)
              // throws SAXException
              {
                //log.debug("END {}", qName);
                //log.debug("XPATH {}", String.join(" / ", stack));
                stack.remove(0);
              }

            @Override
            public void characters (char[] ch, int start, int length)
              // throws SAXException
              {
                final String value = new String(ch, start, length);
                final String key = stack.get(0);
                //log.debug("PROPERTY {} = {}", key, value);
              }
          }));
      }
  }
