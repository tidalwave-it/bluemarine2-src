/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - git clone https://tidalwave@bitbucket.org/tidalwave/bluemarine2-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Comparator;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.DefaultFilterSortCriterion;
import it.tidalwave.bluemarine2.model.Track;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class TrackComparator extends DefaultFilterSortCriterion<Track>
  {
    private final static Comparator<Track> COMPARATOR = (tr1, tr2) ->
      {
        try
          {
            final int d1 = tr1.getDiskNumber().orElse(1);
            final int d2 = tr2.getDiskNumber().orElse(1);
            final int t1 = tr1.getTrackNumber();
            final int t2 = tr2.getTrackNumber();

            if (d1 != d2)
              {
                return d1 - d2;
              }
            
            if (t1 != t2)
              {
                return t1 - t2;
              }
          }
        catch (AsException e)
          {
          }
        
        return displayName(tr1).compareTo(displayName(tr2));
      };
    
    public TrackComparator() 
      {
        super(COMPARATOR, "TrackComparator");
      }
    
    @Nonnull
    private static String displayName (final @Nonnull As object)
      {
        try
          {
            return object.as(Displayable).getDisplayName();  
          }
        catch (AsException e)
          {
            return "???";
          }
      }
  }
