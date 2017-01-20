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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.util.StreamUtils;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Id;
import it.tidalwave.util.Finder;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.util.Task;
import it.tidalwave.util.spi.ReflectionUtils;
import it.tidalwave.role.ContextManager;
import it.tidalwave.bluemarine2.model.finder.BaseFinder;
import it.tidalwave.bluemarine2.model.spi.CacheManager;
import it.tidalwave.bluemarine2.model.spi.CacheManager.Cache;
import it.tidalwave.bluemarine2.model.impl.catalog.factory.RepositoryEntityFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static java.util.stream.Collectors.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static it.tidalwave.bluemarine2.model.impl.catalog.finder.Rdf4jUtilities.*;
import static it.tidalwave.bluemarine2.model.vocabulary.BM.O_EMBEDDED;

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
@Configurable @Slf4j
public class RepositoryFinderSupport<ENTITY, FINDER extends Finder8<ENTITY>>
        extends Finder8Support<ENTITY, FINDER>
        implements BaseFinder<ENTITY, FINDER>
  {
    private static final String REGEX_BINDING_TAG = "^@([A-Za-z0-9]*)@";

    private static final String REGEX_BINDING_TAG_LINE = REGEX_BINDING_TAG + ".*$";

    private static final String PREFIXES = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
                                         + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                                         + "PREFIX rel:   <http://purl.org/vocab/relationship/>\n"
                                         + "PREFIX bmmo:  <http://bluemarine.tidalwave.it/2015/04/mo/>\n"
                                         + "PREFIX mo:    <http://purl.org/ontology/mo/>\n"
                                         + "PREFIX vocab: <http://dbtune.org/musicbrainz/resource/vocab/>\n"
                                         + "PREFIX xs:    <http://www.w3.org/2001/XMLSchema#>\n";

    private static final String QUERY_COUNT_HOLDER = "queryCount";

    private static final long serialVersionUID = 1896412264314804227L;

    @Nonnull
    protected final Repository repository;

    @Nonnull
    private final Class<ENTITY> entityClass;

    @Nonnull
    private final Optional<IRI> source;

    @Inject
    private transient ContextManager contextManager;

    @Inject
    private transient RepositoryEntityFactory entityFactory;

    @Inject
    private transient CacheManager cacheManager;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor(staticName = "withSparql") @EqualsAndHashCode @ToString
    protected static class QueryAndParameters
      {
        @Getter @Nonnull
        private final String sparql;

        @Nonnull
        private final List<Object> parameters = new ArrayList<>();

        @Nonnull
        public QueryAndParameters withParameter (final @Nonnull String name, final @Nonnull Optional<? extends Value> value)
          {
            return value.map(v -> withParameter(name, v)).orElse(this);
          }

        @Nonnull
        public QueryAndParameters withParameter (final @Nonnull String name, final @Nonnull Value value)
          {
            parameters.addAll(Arrays.asList(name, value));
            return this;
          }

        @Nonnull
        public Object[] getParameters()
          {
            return parameters.toArray();
          }

        @Nonnull
        private String getCountSparql()
          {
            return String.format("SELECT (COUNT(*) AS ?%s)%n  {%n%s%n  }",
                                 QUERY_COUNT_HOLDER,
                                 sparql.replaceAll("ORDER BY[\\s\\S]*", ""));
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected RepositoryFinderSupport (final @Nonnull Repository repository)
      {
        this.repository = repository;
        this.entityClass = (Class<ENTITY>)ReflectionUtils.getTypeArguments(RepositoryFinderSupport.class, getClass()).get(0);
        this.source = Optional.of(O_EMBEDDED); // FIXME: resets
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private RepositoryFinderSupport (final @Nonnull Repository repository, final @Nonnull Class<ENTITY> entityClass, final @Nonnull IRI source)
      {
        this.repository = repository;
        this.entityClass = entityClass;
        this.source = Optional.of(source);
      }

    /*******************************************************************************************************************
     *
     * Clone constructor.
     *
     ******************************************************************************************************************/
    public RepositoryFinderSupport (final @Nonnull RepositoryFinderSupport<ENTITY, FINDER> other,
                                    final @Nonnull Object override)
      {
        super(other, override);
        final RepositoryFinderSupport<ENTITY, FINDER> source = getSource(RepositoryFinderSupport.class, other, override);
        this.repository = source.repository;
        this.entityClass = source.entityClass;
        this.source = source.source;
     }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected final List<? extends ENTITY> computeNeededResults()
      {
        return query(QueryAndParameters::getSparql,
                     result -> createEntities(repository, entityClass, result),
                     result -> String.format("%d entities", result.size()));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnegative
    public final int count()
      {
        return query(QueryAndParameters::getCountSparql,
                     result -> Integer.parseInt(result.next().getValue(QUERY_COUNT_HOLDER).stringValue()),
                     result -> String.format("%d", result));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public FINDER importedFrom (final @Nonnull Optional<Id> optionalSource)
      {
        return optionalSource.map(this::importedFrom).orElse((FINDER)this);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public FINDER importedFrom (final @Nonnull Id source)
      {
        return clone(new RepositoryFinderSupport(repository, entityClass, SimpleValueFactory.getInstance().createIRI(source.toString())));
      }

    /*******************************************************************************************************************
     *
     * Prepares the SPARQL query and its parameters.
     *
     * @return      the SPARQL query and its parameters
     *
     ******************************************************************************************************************/
    @Nonnull
    protected /* abstract */ QueryAndParameters prepareQuery()
      {
        throw new UnsupportedOperationException("Must be implemented by subclasses");
      }

    /*******************************************************************************************************************
     *
     * Performs a query, eventually using the cache.
     *
     * @param   sparqlSelector  a function that select the SPARQL statement to use
     * @param   finalizer       a function to transform the query raw result into the final result
     * @param   resultToString  a function that provide the logging string for the result
     * @return                  the found entities
     *
     ******************************************************************************************************************/
    @Nonnull
    private <E> E query (final @Nonnull Function<QueryAndParameters, String> sparqlSelector,
                         final @Nonnull Function<TupleQueryResult, E> finalizer,
                         final @Nonnull Function<E, String> resultToString)
      {
        log.info("query() - {}", entityClass);
        final long baseTime = System.nanoTime();
        final QueryAndParameters queryAndParameters = prepareQuery().withParameter("source", source);
        final Object[] parameters = queryAndParameters.getParameters();
        final String originalSparql = sparqlSelector.apply(queryAndParameters);
        final String sparql = PREFIXES + Stream.of(originalSparql.split("\n"))
                                               .filter(s -> matchesTag(s, parameters))
                                               .map(s -> s.replaceAll(REGEX_BINDING_TAG, ""))
                                               .collect(joining("\n"));
        log(originalSparql, sparql, parameters);
        final E result = query(sparql, finalizer, parameters);
        final long elapsedTime = System.nanoTime() - baseTime;
        log.info(">>>> query returned {} in {} msec", resultToString.apply(result), elapsedTime / 1E6);
        return result;
      }

    /*******************************************************************************************************************
     *
     * Performs a query.
     *
     * @param   sparql          the SPARQL of the query
     * @param   finalizer       a function to transform the query raw result into the final result
     * @param   parameters      an optional set of parameters of the query ("name", value, "name", value ,,,)
     * @return                  the found entities
     *
     ******************************************************************************************************************/
    @Nonnull
    private <R> R query (final @Nonnull String sparql,
                         final @Nonnull Function<TupleQueryResult, R> finalizer,
                         final @Nonnull Object ... parameters)
      {
        try (final RepositoryConnection connection = repository.getConnection())
          {
            final TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);

            for (int i = 0; i < parameters.length; i += 2)
              {
                query.setBinding((String)parameters[i], (Value)parameters[i + 1]);
              }
            //
            // Don't cache entities because they are injected with DCI roles in function of the context.
            // Caching tuples is safe.
            final Cache cache = cacheManager.getCache(RepositoryFinderSupport.class);
            final String key = String.format("%s # %s", compacted(sparql), Arrays.toString(parameters));

            try (final ImmutableTupleQueryResult result = cache.getCachedObject(key,
                                                                () -> new ImmutableTupleQueryResult(query.evaluate())))
              {
                // MutableTupleQueryResult is not thread safe, so clone an eventually cached result
                final ImmutableTupleQueryResult clone = new ImmutableTupleQueryResult(result);
                return finalizer.apply(clone);
              }
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
            return StreamUtils.copyToString(is, UTF_8);
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
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
      {
        return contextManager.runWithContexts(getContexts(), new Task<List<E>, RuntimeException>()
          {
            @Override @Nonnull
            public List<E> run()
              {
                return streamOf(queryResult)
                            .map(bindingSet -> entityFactory.createEntity(repository, entityClass, bindingSet))
                            .collect(toList());
              }
          });
        // TODO: requires TheseFoolishThings 3.1-ALPHA-3
//        return contextManager.runWithContexts(getContexts(), () -> streamOf(queryResult)
//                .map(bindingSet -> entityFactory.createEntity(repository, entityClass, bindingSet))
//                .collect(toList()));
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
    private static boolean matchesTag (final @Nonnull String string, final @Nonnull Object[] bindings)
      {
        final Pattern patternBindingTagLine = Pattern.compile(REGEX_BINDING_TAG_LINE);
        final Matcher matcher = patternBindingTagLine.matcher(string);

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
            log.info(">>>> query: {}", compacted(sparql));
          }

        if (log.isInfoEnabled())
          {
            log.info(">>>> query parameters: {}", Arrays.toString(bindings));
          }
     }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String compacted (final @Nonnull String sparql)
      {
        return sparql.replace("\n", " ").replaceAll("\\s+", " ").trim();
      }
  }
