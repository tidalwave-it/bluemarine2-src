/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.catalog.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import org.springframework.util.StreamUtils;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import it.tidalwave.util.Id;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.bluemarine2.model.Entity;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public abstract class RepositoryFinderSupport<ENTITY, FINDER extends Finder8<ENTITY>>
              extends Finder8Support<ENTITY, FINDER> 
  {
    @Nonnull
    protected static String readSparql (final @Nonnull Class<?> clazz, final @Nonnull String name)
      {
        try 
          {
            @Cleanup InputStream is = clazz.getResourceAsStream(name);
            return StreamUtils.copyToString(is, Charset.forName("UTF-8"));
          } 
        catch (IOException e) 
          {
            throw new RuntimeException(e);
          }
      }
    
    protected static final String PREFIXES =
                  "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n" 
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" 
                + "PREFIX rel:   <http://purl.org/vocab/relationship/>\n" 
                + "PREFIX bm:    <http://bluemarine.tidalwave.it/2015/04/mo/>\n" 
                + "PREFIX mo:    <http://purl.org/ontology/mo/>\n" 
                + "PREFIX vocab: <http://dbtune.org/musicbrainz/resource/vocab/>\n" 
                + "PREFIX xs:    <http://www.w3.org/2001/XMLSchema#>\n";

    @Nonnull
    private Repository repository;
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public RepositoryFinderSupport clone()
      {
        final RepositoryFinderSupport clone = (RepositoryFinderSupport)super.clone();
        clone.repository  = this.repository;

        return clone;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    protected <E extends Entity> List<E> query (final @Nonnull Class<E> entityClass,
                                                final @Nonnull String sparql,
                                                final @Nonnull Object ... bindings)
      {
        try 
          {
            final RepositoryConnection connection = repository.getConnection();
            final TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, PREFIXES + sparql);

            for (int i = 0; i < bindings.length; i += 2)
              {
                query.setBinding((String)bindings[i], (Value)bindings[i + 1]);
              }

            final TupleQueryResult result = query.evaluate();
            final List<E> entities = toEntities(repository, entityClass, result);
            result.close();
            connection.close();

            return entities;
          }
        catch (RepositoryException | MalformedQueryException | QueryEvaluationException e)
          {
            throw new RuntimeException(e);
          }
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private <E extends Entity> List<E> toEntities (final @Nonnull Repository repository, 
                                                  final @Nonnull Class<E> entityClass,
                                                  final @Nonnull TupleQueryResult result) 
      throws QueryEvaluationException 
      {
        final List<E> entities = new ArrayList<>();
        
        while (result.hasNext())
          {
            final BindingSet bs = result.next();
//            log.debug(">>>> {} {}", bs, bs.getBindingNames());
            entities.add(toEntity(repository, entityClass, bs));
//            log.info("{}", entity);
          }
        
        return entities;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private static <E extends Entity> E toEntity (final @Nonnull Repository repository, 
                                                  final @Nonnull Class<E> entityClass,
                                                  final @Nonnull BindingSet bindingSet)
      {
        if (entityClass.equals(RepositoryMusicArtistEntity.class))
          {
            return (E)new RepositoryMusicArtistEntity(repository,
                    new Id(toString(bindingSet.getBinding("artist"))), 
                           toString(bindingSet.getBinding("label")), null);
          }
        
        if (entityClass.equals(RepositoryTrackEntity.class))
          {
            return (E)new RepositoryTrackEntity(repository,
                    new Id(toString(bindingSet.getBinding("track"))), 
                           Paths.get(toString(bindingSet.getBinding("path"))),
                           toString(bindingSet.getBinding("label")),
                           toDuration(bindingSet.getBinding("duration")),
                           toInteger(bindingSet.getBinding("track_number")),
                           toString(bindingSet.getBinding("record_label")),
                           null);
//                           toInteger(bindingSet.getBinding("track_number")));
          }
        
        throw new RuntimeException("Unknown entity: " + entityClass);
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    @Nullable
    private static String toString (final @Nullable Binding binding)
      {
        if (binding == null)
          {
            return null;  
          }
        
        final Value value = binding.getValue();
        
        return (value != null) ? value.stringValue() : null;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    @Nullable
    private static Integer toInteger (final @Nullable Binding binding)
      {
        if (binding == null)
          {
            return null;  
          }
        
        final Value value = binding.getValue();
        
        return (value != null) ? Integer.parseInt(value.stringValue()) : null;
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    @Nullable
    private static Duration toDuration (final @Nullable Binding binding)
      {
        if (binding == null)
          {
            return null;  
          }
        
        final Value value = binding.getValue();
        
        return (value != null) ? Duration.ofMillis((int)Float.parseFloat(value.stringValue())) : null;
      }
  }
