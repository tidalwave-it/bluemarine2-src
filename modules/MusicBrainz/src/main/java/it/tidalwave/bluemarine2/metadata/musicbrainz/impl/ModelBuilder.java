/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.metadata.musicbrainz.impl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.TreeModel;

/***********************************************************************************************************************
 *
 * Unlike the similar class in RDF4J, this is thread-safe and can merge to similar objects.
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@ThreadSafe
public class ModelBuilder
  {
    private final Model model = new TreeModel();

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized Model toModel()
      {
        return new TreeModel(model);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public synchronized ModelBuilder with (final @Nonnull Resource subjext,
                                  final @Nonnull IRI predicate,
                                  final @Nonnull Value object,
                                  final @Nonnull Resource... contexts)
      {
        model.add(subjext, predicate, object, contexts);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public synchronized ModelBuilder merge (final @Nonnull ModelBuilder other)
      {
        other.toModel().forEach(statement -> model.add(statement));
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public synchronized ModelBuilder merge (final @Nonnull List<ModelBuilder> others)
      {
        others.stream().map(other -> other.toModel()).forEach(m -> m.forEach(statement -> model.add(statement)));
        return this;
      }
  }
