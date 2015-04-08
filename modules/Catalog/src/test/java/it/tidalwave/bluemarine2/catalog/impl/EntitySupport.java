/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog.impl;

import javax.annotation.Nonnull;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.role.Identifiable;
import it.tidalwave.bluemarine2.model.Entity;
import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openrdf.repository.Repository;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class EntitySupport implements Entity, Identifiable
  {
    @Delegate
    private AsSupport asSupport; // FIXME = new AsSupport(this);
    
    @Nonnull
    protected final Repository repository;
    
    @Getter @Nonnull
    protected final Id id;
  }
