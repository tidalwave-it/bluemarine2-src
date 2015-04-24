/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - Semantic Media Center
 * http://bluemarine2.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Comparator;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.DefaultFilterSortCriterion;
import it.tidalwave.bluemarine2.model.MediaItem;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MediaItemComparator extends DefaultFilterSortCriterion<As>
  {
    private final static Comparator<As> COMPARATOR = (o1, o2) ->
      {
        try
          {
            final MediaItem mi1 = o1.as(MediaItem.class);
            final MediaItem mi2 = o2.as(MediaItem.class);
            final MediaItem.Metadata m1 = mi1.getMetadata();
            final MediaItem.Metadata m2 = mi2.getMetadata();
            final int d1 = m1.get(MediaItem.Metadata.DISK_NUMBER).orElse(1);
            final int d2 = m2.get(MediaItem.Metadata.DISK_NUMBER).orElse(1);
            final int t1 = m1.get(MediaItem.Metadata.TRACK).orElse(0);
            final int t2 = m2.get(MediaItem.Metadata.TRACK).orElse(0);

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
        
        return displayName(o1).compareTo(displayName(o2));
      };
    
    public MediaItemComparator() 
      {
        super(COMPARATOR, "MediaItemComparator");
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
