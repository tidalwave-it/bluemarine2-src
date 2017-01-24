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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.n3.N3Writer;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.common.iteration.Iteration;
import it.tidalwave.util.Id;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.text.Normalizer.Form.NFC;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;
import static it.tidalwave.bluemarine2.util.Formatters.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE) @Slf4j
public final class RdfUtilities
  {
    private static final String ALGORITHM = "SHA1";

    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance(); // FIXME

    /*******************************************************************************************************************
     *
     * Exports the repository to the given file. FIXME: duplicated in DefaultPerstistence
     *
     ******************************************************************************************************************/
    public static void exportToFile (final @Nonnull Model model, final @Nonnull Path path)
      throws RDFHandlerException, IOException, RepositoryException
      {
        log.info("exportToFile({})", path);
        Files.createDirectories(path.getParent());

        try (final PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path, UTF_8)))
          {
            final RDFHandler writer = new SortingRDFHandler(new N3Writer(pw));
            writer.startRDF();
//            FIXME: use Iterations - and sort
//            for (final Namespace namespace : connection.getNamespaces().asList())
//              {
//                writer.handleNamespace(namespace.getPrefix(), namespace.getName());
//              }

            writer.handleNamespace("bio",   "http://purl.org/vocab/bio/0.1/");
            writer.handleNamespace("bmmo",  "http://bluemarine.tidalwave.it/2015/04/mo/");
            writer.handleNamespace("dc",    "http://purl.org/dc/elements/1.1/");
            writer.handleNamespace("foaf",  "http://xmlns.com/foaf/0.1/");
            writer.handleNamespace("owl",   "http://www.w3.org/2002/07/owl#");
            writer.handleNamespace("mo",    "http://purl.org/ontology/mo/");
            writer.handleNamespace("rdfs",  "http://www.w3.org/2000/01/rdf-schema#");
            writer.handleNamespace("rel",   "http://purl.org/vocab/relationship/");
            writer.handleNamespace("vocab", "http://dbtune.org/musicbrainz/resource/vocab/");
            writer.handleNamespace("xs",    "http://www.w3.org/2001/XMLSchema#");

            model.stream().forEachOrdered(writer::handleStatement);
            writer.endRDF();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <T, X extends RuntimeException> Stream<T> streamOf (final @Nonnull Iteration<T, X> iteration)
      {
        return stream(spliteratorUnknownSize(iteratorOf(iteration), 0), false);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static <T, X extends RuntimeException> Iterator<T> iteratorOf (final @Nonnull Iteration<T, X> iteration)
      {
        final Iterator<T> iterator = new Iterator<T>()
          {
            @Override
            public boolean hasNext()
              {
                return iteration.hasNext();
              }

            @Override
            public T next()
              {
                return iteration.next();
              }
          };

        return iterator;
      }

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
