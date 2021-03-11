/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DbTune
  {
    private final static ValueFactory FACTORY = SimpleValueFactory.getInstance();

    public static final String NS = "http://dbtune.org/musicbrainz/resource/vocab/";

    public static final String S_P_ALBUMMETA_COVERART_URL   = NS + "albummeta_coverarturl";
    public static final String S_P_ARTIST_TYPE              = NS + "artist_type";
    public static final String S_P_SORT_NAME                = NS + "sortname";

    public static final IRI P_ARTIST_TYPE                   = FACTORY.createIRI(S_P_ARTIST_TYPE);

    public static final IRI P_SORT_NAME                     = FACTORY.createIRI(S_P_SORT_NAME);

    public static final IRI P_ALBUMMETA_COVERART_URL        = FACTORY.createIRI(S_P_ALBUMMETA_COVERART_URL);
  }
