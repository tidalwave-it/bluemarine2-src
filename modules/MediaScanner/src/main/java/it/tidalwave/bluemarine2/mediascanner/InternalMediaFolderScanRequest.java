/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.mediascanner;

import it.tidalwave.bluemarine2.model.MediaFolder;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @RequiredArgsConstructor @Getter @ToString
public class InternalMediaFolderScanRequest 
  {
    @Nonnull
    private final MediaFolder folder;
  }
