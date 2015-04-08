/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.util.Id;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
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

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class QueryUtilities 
  {
    public static final String PREFIXES =
                  "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n" 
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" 
                + "PREFIX mo:    <http://purl.org/ontology/mo/>\n" 
                + "PREFIX vocab: <http://dbtune.org/musicbrainz/resource/vocab/>\n" 
                + "PREFIX xs:    <http://www.w3.org/2001/XMLSchema#>\n";

    
    @Nonnull
    public static <E extends Entity> List<E> query (final @Nonnull Repository repository, 
                                                    final @Nonnull Class<E> entityClass,
                                                    final @Nonnull String sparql,
                                                    final @Nonnull Object ... bindings)
      throws RepositoryException, QueryEvaluationException, MalformedQueryException
      {
        final RepositoryConnection connection = repository.getConnection();
        final TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
        
        for (int i = 0; i < bindings.length; i += 2)
          {
            query.setBinding((String)bindings[i], (Value)bindings[i + 1]);
          }
        
        final TupleQueryResult result = query.evaluate();
        final List<E> entities = QueryUtilities.toEntities(repository, entityClass, result);
        result.close();
        connection.close();
        
        return entities;
      }
    
    @Nonnull
    static <E extends Entity> List<E> toEntities (final @Nonnull Repository repository, 
                                                  final @Nonnull Class<E> entityClass,
                                                  final @Nonnull TupleQueryResult result) 
      throws QueryEvaluationException 
      {
        final List<E> entities = new ArrayList<>();
        
        while (result.hasNext())
          {
            final BindingSet bs = result.next();
            log.debug(">>>> {} {}", bs, bs.getBindingNames());
            entities.add(QueryUtilities.toEntity(repository, entityClass, bs));
//            log.info("{}", entity);
          }
        
        return entities;
      }
    
    @Nonnull
    public static <E extends Entity> E toEntity (final @Nonnull Repository repository, 
                                                 final @Nonnull Class<E> entityClass,
                                                 final @Nonnull BindingSet bindingSet)
      {
        if (entityClass.equals(MusicArtistEntity.class))
          {
            return (E)new MusicArtistEntity(repository,
                    new Id(toString(bindingSet.getBinding("artist"))), 
                           toString(bindingSet.getBinding("label")), null);
          }
        
        if (entityClass.equals(TrackEntity.class))
          {
            return (E)new TrackEntity(repository,
                    new Id(toString(bindingSet.getBinding("track"))), 
                           Paths.get(toString(bindingSet.getBinding("audioFile"))),
                           toString(bindingSet.getBinding("label")),
                           toDuration(bindingSet.getBinding("duration")),
                           toInteger(bindingSet.getBinding("track_number")),
                           toString(bindingSet.getBinding("record_label")),
                           null);
//                           toInteger(bindingSet.getBinding("track_number")));
          }
        
        throw new RuntimeException("Unknown entity: " + entityClass);
      }
    
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
