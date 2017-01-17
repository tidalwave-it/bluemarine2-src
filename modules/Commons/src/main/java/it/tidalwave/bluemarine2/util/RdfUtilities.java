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
package it.tidalwave.bluemarine2.util;

import static it.tidalwave.bluemarine2.util.Formatters.toHexString;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.text.Normalizer;
import java.util.Date;
import java.util.Optional;
import java.nio.file.Path;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import it.tidalwave.util.Id;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.NoArgsConstructor;
import static java.text.Normalizer.Form.NFC;
import java.util.Base64;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class RdfUtilities
  {
    private static final String ALGORITHM = "SHA1";

    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance(); // FIXME

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final Path path)
      {
        return FACTORY.createLiteral(Normalizer.normalize(path.toString(), NFC));
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
    public static Optional<Value> literalForInt (final Optional<Integer> optionalInteger)
      {
        return optionalInteger.map(i -> literalFor(i));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Value literalFor (final long value)
      {
        return FACTORY.createLiteral(value);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Optional<Value> literalForLong (final Optional<Long> optionalLong)
      {
        return optionalLong.map(l -> literalFor(l));
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
    public static Optional<Value> literalForFloat (final Optional<Float> optionalFloat)
      {
        return optionalFloat.map(f -> literalFor(f));
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
    public static IRI uriFor (final @Nonnull Id id)
      {
        return uriFor(id.stringValue());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static IRI uriFor (final @Nonnull String id)
      {
        return FACTORY.createIRI(id);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static IRI uriFor (final @Nonnull URL url)
      {
        return FACTORY.createIRI(url.toString());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static URL urlFor (final @Nonnull IRI uri)
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

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Id createSha1Id (final @Nonnull String string)
      {
        try
          {
            final MessageDigest digestComputer = MessageDigest.getInstance(ALGORITHM);
            digestComputer.update(string.getBytes(UTF_8));
            return new Id(toHexString(digestComputer.digest()));
          }
        catch (NoSuchAlgorithmException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Id createSha1IdNew (final @Nonnull String string)
      {
        try
          {
            final MessageDigest digestComputer = MessageDigest.getInstance(ALGORITHM);
            digestComputer.update(string.getBytes(UTF_8));
            return new Id(Base64.getUrlEncoder().encodeToString(digestComputer.digest()));
          }
        catch (NoSuchAlgorithmException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
