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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.net.MalformedURLException;
import java.net.URL;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.n3.N3ParserFactory;
import it.tidalwave.util.Id;
import it.tidalwave.bluemarine2.downloader.DownloadComplete;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Utilities 
  {
    private final static ValueFactory FACTORY = ValueFactoryImpl.getInstance(); // FIXME
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Model parseModel (final @Nonnull DownloadComplete message)
        throws RDFHandlerException, RDFParseException, IOException
      {
        final N3ParserFactory n3ParserFactory = new N3ParserFactory();
        final RDFParser parser = n3ParserFactory.getParser();
        final Model model = new LinkedHashModel();
        
        parser.setRDFHandler(new RDFHandlerBase()
          {
            @Override
            public void handleStatement (final @Nonnull Statement statement)
              {
                model.add(statement);
              }
          });

        // FIXME
        final byte[] bytes = new String(message.getBytes()).replaceAll(" = ", "owl:sameAs")
                                                           .replaceAll("/ASIN/ *>", "/ASIN>")
                                                           .getBytes(); 
        final String uri = message.getUrl().toString();          
        parser.parse(new ByteArrayInputStream(bytes), uri);

        return model;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final Path path) 
      {
        return FACTORY.createLiteral(path.toString());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final String string) 
      {
        return FACTORY.createLiteral(string);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Optional<Value> literalFor (final Optional<String> optionalString) 
      {
        return optionalString.map(s -> literalFor(s));
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final Id id) 
      {
        return FACTORY.createLiteral(id.stringValue());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final int value) 
      {
        return FACTORY.createLiteral(value);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final short value) 
      {
        return FACTORY.createLiteral(value);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final float value) 
      {
        return FACTORY.createLiteral(value);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final @Nonnull Instant instant) 
      {
        return FACTORY.createLiteral(new Date(instant.toEpochMilli()));
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static URI uriFor (final @Nonnull Id id)
      {
        return uriFor(id.stringValue());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static URI uriFor (final @Nonnull String id)
      {
        return FACTORY.createURI(id);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static URI uriFor (final @Nonnull URL url)
      {
        return FACTORY.createURI(url.toString());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static URL urlFor (final @Nonnull URI uri)
      throws MalformedURLException
      {
        return new URL(uri.toString());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static String emptyWhenNull (final @Nullable String string)
      {
        return (string != null) ? string : "";  
      }
  }
