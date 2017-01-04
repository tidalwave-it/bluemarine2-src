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
package it.tidalwave.bluemarine2.model.impl.catalog.finder;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.util.StreamUtils;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Id;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.util.Task;
import it.tidalwave.role.ContextManager;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryMusicArtist;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryRecord;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryTrack;
import it.tidalwave.util.Finder;
import static java.util.stream.Collectors.joining;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.IRI;

/***********************************************************************************************************************
 *
 * A base class for creating {@link Finder}s.
 *
 * @param <ENTITY>  the entity the {@code Finder} should find
 * @param <FINDER>  the subclass
 *
 * @stereotype      Finder
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @RequiredArgsConstructor(access = AccessLevel.PROTECTED) @Slf4j
public class RepositoryFinderSupport<ENTITY, FINDER extends Finder8<ENTITY>>
              extends Finder8Support<ENTITY, FINDER>
  {
    private static final String REGEX_BINDING_TAG = "^@([A-Za-z0-9]*)@.*";

    protected static final String PREFIXES =
                  "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rel:   <http://purl.org/vocab/relationship/>\n"
                + "PREFIX bm:    <http://bluemarine.tidalwave.it/2015/04/mo/>\n"
                + "PREFIX mo:    <http://purl.org/ontology/mo/>\n"
                + "PREFIX vocab: <http://dbtune.org/musicbrainz/resource/vocab/>\n"
                + "PREFIX xs:    <http://www.w3.org/2001/XMLSchema#>\n";

    private static final long serialVersionUID = 1896412264314804227L;

    @Nonnull
    protected final Repository repository;

    @Inject
    private ContextManager contextManager;

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    protected RepositoryFinderSupport (final @Nonnull RepositoryFinderSupport<ENTITY, FINDER> other,
                                       final @Nonnull Object override)
      {
        super(other, override);
        final RepositoryFinderSupport<ENTITY, FINDER> source = getSource(RepositoryFinderSupport.class, other, override);
        this.repository = source.repository;
      }

    /*******************************************************************************************************************
     *
     * Performs a query.
     *
     * @param <E>               the static type of the entity to query
     * @param entityClass       the dynamic type of the entity to query
     * @param originalSparql    the SPARQL of the query
     * @param bindings          an optional set of bindings of the query ("name", value, "name", value ,,,)
     * @return                  the found entities
     *
     ******************************************************************************************************************/
    @Nonnull
//    protected <E extends Entity> List<E> query (final @Nonnull Class<E> entityClass,
    protected <E> List<E> query (final @Nonnull Class<E> entityClass,
                                 final @Nonnull String originalSparql,
                                 final @Nonnull Object ... bindings)
      {
        try
          {
            log.info("query({}, ...)", entityClass);

            final String sparql = PREFIXES +
                                  Stream.of(originalSparql.split("\n"))
                                    .filter(s -> matches(s, bindings))
                                    .map(s -> s.replaceAll("^@[A-Za-z0-9]*@", ""))
                                    .collect(Collectors.joining("\n"));

            try (final RepositoryConnection connection = repository.getConnection())
              {
                final TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);

                for (int i = 0; i < bindings.length; i += 2)
                  {
                    query.setBinding((String)bindings[i], (Value)bindings[i + 1]);
                  }

                log(originalSparql, sparql, bindings);
                final long baseTime = System.nanoTime();

                try (final TupleQueryResult result = query.evaluate())
                  {
                    final List<E> entities = createEntities(repository, entityClass, result);
                    final long elapsedTime = System.nanoTime() - baseTime;
                    log.info(">>>> query returned {} entities in {} msec", entities.size(), elapsedTime / 1E6);
                    return entities;
                  }
              }
          }
        catch (RepositoryException | MalformedQueryException | QueryEvaluationException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     * Facility method that creates an {@link IRI} given an {@link Id}.
     *
     * @param   id  the {@code Id}
     * @return      the {@code IRI}
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Value iriFor (final @Nonnull Id id)
      {
        return SimpleValueFactory.getInstance().createIRI(id.stringValue());
      }

    /*******************************************************************************************************************
     *
     * Reads a SPARQL statement from a named resource
     *
     * @param   clazz   the reference class
     * @param   name    the resource name
     * @return          the SPARQL statement
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static String readSparql (final @Nonnull Class<?> clazz, final @Nonnull String name)
      {
        try (final InputStream is = clazz.getResourceAsStream(name))
          {
            return StreamUtils.copyToString(is, Charset.forName("UTF-8"));
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} if the given string contains a binding tag (in the form {@code @TAG@}) that matches one
     * of the bindings; or if there are no binding tags. This is used as a filter to eliminate portions of SPARQL
     * queries that don't match any binding.
     *
     * @param   string      the string
     * @param   bindings    the bindings
     * @return              {@code true} if there is a match
     *
     ******************************************************************************************************************/
    private static boolean matches (final @Nonnull String string, final @Nonnull Object[] bindings)
      {
        final Pattern p = Pattern.compile(REGEX_BINDING_TAG);
        final Matcher matcher = p.matcher(string);

        if (!matcher.matches())
          {
            return true;
          }

        final String tag = matcher.group(1);

        for (int i = 0; i < bindings.length; i+= 2)
          {
            if (tag.equals(bindings[i]))
              {
                return true;
              }
          }

        return false;
      }

    /*******************************************************************************************************************
     *
     * Instantiates an entity for each given {@link TupleQueryResult}. Entities are instantiated in the DCI contents
     * associated to this {@link Finder} - see {@link #getContexts()}.
     *
     * @param   <E>             the static type of the entities to instantiate
     * @param   repository      the repository we're querying
     * @param   entityClass     the dynamic type of the entities to instantiate
     * #param   queryResult     the {@code TupleQueryResult}
     * @return                  the instantiated entities
     *
     ******************************************************************************************************************/
    @Nonnull
    private <E> List<E> createEntities (final @Nonnull Repository repository,
                                        final @Nonnull Class<E> entityClass,
                                        final @Nonnull TupleQueryResult queryResult)
      throws QueryEvaluationException
      {
        return contextManager.runWithContexts(getContexts(), new Task<List<E>, QueryEvaluationException>()
          {
            @Override @Nonnull
            public List<E> run()
              throws QueryEvaluationException
              {
                final List<E> entities = new ArrayList<>();

                while (queryResult.hasNext())
                  {
                    entities.add(createEntity(repository, entityClass, queryResult.next()));
                  }

                return entities;
              }
          });
      }

    /*******************************************************************************************************************
     *
     * Instantiates an entity populating attributes from the given {@link BindingSet}.
     *
     * @param   <E>             the static type of the entity to instantiate
     * @param   repository      the repository we're querying
     * @param   entityClass     the dynamic type of the entity to instantiate
     * #param   bindingSet      the {@code BindingSet}
     * @return                  the instantiated entity
     *
     ******************************************************************************************************************/
    @Nonnull
    private static <E> E createEntity (final @Nonnull Repository repository,
                                       final @Nonnull Class<E> entityClass,
                                       final @Nonnull BindingSet bindingSet)
      {
//        log.trace("createEntity(.., {}, ..)", entityClass.getSimpleName());
//        log.trace(">>>> bindingSet: {}", bindingSet.getBindingNames());
        // FIXME: use a map
        if (entityClass.equals(String.class))
          {
            return (E)bindingSet.iterator().next().getValue().stringValue();
          }

        if (entityClass.equals(URL.class))
          {
            try
              {
                return (E)new URL(bindingSet.iterator().next().getValue().stringValue());
              }
            catch (MalformedURLException e)
              {
                throw new RuntimeException(e);
              }
          }

        if (entityClass.equals(RepositoryMusicArtist.class))
          {
            return (E)new RepositoryMusicArtist(repository, bindingSet);
          }

        if (entityClass.equals(RepositoryRecord.class))
          {
            return (E)new RepositoryRecord(repository, bindingSet);
          }

        if (entityClass.equals(RepositoryTrack.class))
          {
            return (E)new RepositoryTrack(repository, bindingSet);
          }

        throw new RuntimeException("Unknown entity: " + entityClass);
      }

    /*******************************************************************************************************************
     *
     * Logs the query at various detail levels.
     *
     * @param   originalSparql  the original SPARQL statement
     * @param   sparql          the SPARQL statement after binding tag filtering
     * @param   bindings        the bindings
     *
     ******************************************************************************************************************/
    private void log (final @Nonnull String originalSparql,
                      final @Nonnull String sparql,
                      final @Nonnull Object ... bindings)
      {
        if (log.isTraceEnabled())
          {
            Stream.of(originalSparql.split("\n")).forEach(s -> log.trace(">>>> original query: {}", s));
          }

        if (log.isDebugEnabled())
          {
            Stream.of(sparql.split("\n")).forEach(s -> log.debug(">>>> query: {}", s));
          }

        if (!log.isDebugEnabled() && log.isInfoEnabled())
          {
            log.info(">>>> query: {}", sparql.replace("\n", " ").replaceAll("\\s+", " "));
          }

        if (log.isInfoEnabled())
          {
            log.info(">>>> query parameters: {}", Arrays.toString(bindings));
          }
     }
  }
