/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.bluemarine2.catalog;

import it.tidalwave.util.Id;
import java.nio.file.Path;
import java.time.Duration;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.openrdf.repository.Repository;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
@Immutable @Getter @ToString
@Slf4j
public class TrackEntity extends EntitySupport
  {
    private final Integer trackNumber;
    
    @Nonnull
    private final String rdfsLabel;
    
    @Nonnull
    private final Duration duration;
    
//    private final String recordRdfsLabel;
//    
//    private final Integer trackCount;
    
    @Nonnull
    private final Path audioFile;

    public TrackEntity (final @Nonnull Repository repository, 
                        final @Nonnull Id id, 
                        final @Nonnull Path audioFile,
                        final @Nonnull String rdfsLabel,
                        final @Nonnull Duration duration,
                        final @Nonnull Integer trackNumber,
                        final @Nonnull String recordRdfsLabel,
                        final @Nonnull Integer trackCount)
      {
        super(repository, id);
        this.audioFile = audioFile;
        this.rdfsLabel = rdfsLabel;
        this.duration = duration;
        this.trackNumber = trackNumber;
//        this.recordRdfsLabel = recordRdfsLabel;
//        this.trackCount = trackCount;
      }
  }
