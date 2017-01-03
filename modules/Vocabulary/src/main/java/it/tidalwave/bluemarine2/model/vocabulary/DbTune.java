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
package it.tidalwave.bluemarine2.model.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
    private final static ValueFactory factory = SimpleValueFactory.getInstance();
    
    public static final String PREFIX = "http://dbtune.org/musicbrainz/resource/vocab/";
    
    public static final String S_ALBUMMETA_COVERART_URL     = PREFIX + "albummeta_coverarturl";
    public static final String S_ARTIST_TYPE                = PREFIX + "artist_type";
    public static final String S_SORT_NAME                  = PREFIX + "sortname";
    
    public static final IRI ARTIST_TYPE                     = factory.createIRI(S_ARTIST_TYPE);

    public static final IRI SORT_NAME                       = factory.createIRI(S_SORT_NAME);
    
    public static final IRI ALBUMMETA_COVERART_URL          = factory.createIRI(S_ALBUMMETA_COVERART_URL);
  }
