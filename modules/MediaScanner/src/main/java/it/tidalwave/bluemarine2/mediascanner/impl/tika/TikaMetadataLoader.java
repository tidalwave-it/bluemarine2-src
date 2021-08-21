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
package it.tidalwave.bluemarine2.mediascanner.impl.tika;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.image.ImageMetadataExtractor;
import org.apache.tika.parser.xmp.JempboxExtractor;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TikaMetadataLoader
  {
    /*******************************************************************************************************************
     *
     * A default content reader that just reads data in the simplest way.
     *
     ******************************************************************************************************************/
    public static final Function<Path, byte[]> DEFAULT_CONTENT_READER = _f(Files::readAllBytes);

    /*******************************************************************************************************************
     *
     * An experimental reader that wraps contents in a xpacket section - it can be used to try parser that extract
     * embedded XMP, such as Jempbox.
     *
     ******************************************************************************************************************/
    public static final Function<Path, byte[]> EXP_XMP_PACKET_WRAPPER_CONTENT_READER = path ->
      {
        try
          {
            return ("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>"
                    + Files.readString(path, UTF_8)
                    + "<?xpacket end=\"w\"?>").getBytes(UTF_8);
          }
        // FIXME: handle RuntimeException, IOException - make FunctionalCheckedExceptionWrappers's method public
        catch (Exception e)
          {
            throw new RuntimeException(e);
          }
      };

    /*******************************************************************************************************************
     *
     * A strategy to extract metadata.
     *
     ******************************************************************************************************************/
    public static interface MetadataExtractor
      {
        /***************************************************************************************************************
         *
         * Extracts metadata.
         *
         * @param path            the path of the file to extract metadata from
         * @param bytes           the contents of the file
         * @param config          the configuration of the extraction
         * @param metadata        the object where to store metadata to
         * @throws TikaException  in case of error
         * @throws IOException    in case of error
         * @throws SAXException   in case of error
         *
         **************************************************************************************************************/
        public void extractMetadata (@Nonnull final Path path,
                                     @Nonnull final byte[] bytes,
                                     @Nonnull final Config config,
                                     @Nonnull final Metadata metadata)
                throws TikaException, IOException, SAXException;
      }

    /*******************************************************************************************************************
     *
     * This seems to be the standard way to use Tika with all defaults.
     *
     ******************************************************************************************************************/
    public static final MetadataExtractor DEFAULT_METADATA_EXTRACTOR = (path, bytes, params, metadata) ->
      {
        metadata.set(Metadata.CONTENT_TYPE, Files.probeContentType(path));
        final Parser parser = params.parserSupplier.get();
        final DefaultHandler handler = params.handlerSupplier.get();
        final ParseContext context = params.parseContextSupplier.get();
        parser.parse(new ByteArrayInputStream(bytes), handler, metadata, context);
      };

    /*******************************************************************************************************************
     *
     * An experimental alternate way to extract data from XMP sidecar files.
     *
     ******************************************************************************************************************/
    public static final MetadataExtractor EXP_XMP_METADATA_EXTRACTOR = (path, bytes, params, metadata) ->
      {
        // metadata.set(Metadata.CONTENT_TYPE, "application/rdf+xml");
        metadata.set(Metadata.CONTENT_TYPE, "application/xml");
        final ImageMetadataExtractor ime = new ImageMetadataExtractor(metadata);
        ime.parseRawXMP(bytes);
      };

    /*******************************************************************************************************************
     *
     * An experimental alternate way to extract data from XMP sidecar files.
     *
     ******************************************************************************************************************/
    public static final MetadataExtractor EXP_XMP_METADATA_EXTRACTOR_JEMPBOX = (path, bytes, params, metadata) ->
      {
        try (final InputStream is = new ByteArrayInputStream(bytes))
          {
            new JempboxExtractor(metadata).parse(is);
          }
      };

    /*******************************************************************************************************************
     *
     * The configuration for the Tika metadata loader.
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor @Getter @ToString @Immutable
    public static class Config
      {
        /** The default configuration. */
        public static final Config DEFAULT = new Config(TikaMetadataLoader.DEFAULT_CONTENT_READER,
                                                        AutoDetectParser::new,
                                                        DefaultHandler::new,
                                                        ParseContext::new,
                                                        TikaMetadataLoader.DEFAULT_METADATA_EXTRACTOR);

        /** The configuration for XMP sidecar files. */
        public static final Config XMP_SIDECAR = new Config(TikaMetadataLoader.DEFAULT_CONTENT_READER,
                                                            XmpParser::new,
                                                            DefaultHandler::new,
                                                            ParseContext::new,
                                                            TikaMetadataLoader.DEFAULT_METADATA_EXTRACTOR);

        /** A map of default config overrides. */
        public static final Map<ExtensionAndMimeType, Config> DEFAULT_ASSOCIATIONS =
                Map.of(ExtensionAndMimeType.ofExtension("xmp"), XMP_SIDECAR);

        /** The object that loads file contents. */
        @Nonnull @With
        private final Function<Path, byte[]> contentReader;

        /** The supplier of a parser. */
        @Nonnull @With
        private final Supplier<Parser> parserSupplier;

        /** The SAX handler. */
        @Nonnull @With
        public Supplier<DefaultHandler> handlerSupplier;

        /** The parse context. */
        @Nonnull @With
        public Supplier<ParseContext> parseContextSupplier;

        /** The metadata extractor. */
        @Nonnull @With
        private final MetadataExtractor metadataExtractor;
      }

    /*******************************************************************************************************************
     *
     * Loads metadata from a given file.
     *
     * @param path            the path of the file to extract metadata from
     * @param config          the Tika configuration
     * @return                the metadata
     * @throws TikaException  in case of error
     * @throws IOException    in case of error
     * @throws SAXException   in case of error
     *
     ******************************************************************************************************************/
    @Nonnull
    public MetadataWithPath loadMetadata (@Nonnull final Path path, @Nonnull final Config config)
            throws TikaException, IOException, SAXException
      {
        log.info("======== {}", path);
        final byte[] bytes = config.contentReader.apply(path);
        final Metadata metadata = new Metadata();
        config.metadataExtractor.extractMetadata(path, bytes, config, metadata);

        return new MetadataWithPath(path, metadata);
      }
  }
