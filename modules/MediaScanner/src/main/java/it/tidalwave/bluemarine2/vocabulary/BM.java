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
package it.tidalwave.bluemarine2.vocabulary;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/***********************************************************************************************************************
 *
 * See http://musicontology.com/specification/
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BM 
  {
    private final static ValueFactory factory = ValueFactoryImpl.getInstance();
    
    private static final String PREFIX = "http://bluemarine.tidalwave.it/2015/04/mo/";
    
    public static final URI FULL_CREDITS         = factory.createURI(PREFIX + "fullCredits");
    
    /** The file timestamp the latest time it was indexed. */
    public static final URI LATEST_INDEXING_TIME = factory.createURI(PREFIX + "latestIndexingTime");
    
    /** Means that the file couldn't be associated to metadata. The object is the timestamp of the latest attempt. */
    public static final URI MISSED_MB_METADATA      = factory.createURI(PREFIX + "missedMusicBrainzMetadata");

    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
    public static final URI LATEST_MB_METADATA      = factory.createURI(PREFIX + "latestMusicBrainzMetadata");
    
    /** Means that the file couldn't download metadata. The object is the timestamp of the latest attempt. */
    public static final URI FAILED_MB_METADATA      = factory.createURI(PREFIX + "failedMusicBrainzMetadata");
  }
