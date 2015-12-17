/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
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

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DbTune 
  {
    private final static ValueFactory factory = ValueFactoryImpl.getInstance();
    
    public static final String PREFIX = "http://dbtune.org/musicbrainz/resource/vocab/";
    
    public static final String S_ALBUMMETA_COVERART_URL     = PREFIX + "albummeta_coverarturl";
    public static final String S_ARTIST_TYPE                = PREFIX + "artist_type";
    public static final String S_SORT_NAME                  = PREFIX + "sortname";
    
    public static final URI ARTIST_TYPE                     = factory.createURI(S_ARTIST_TYPE);

    public static final URI SORT_NAME                       = factory.createURI(S_SORT_NAME);
    
    public static final URI ALBUMMETA_COVERART_URL          = factory.createURI(S_ALBUMMETA_COVERART_URL);
  }
