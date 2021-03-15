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
package it.tidalwave.bluemarine2.model.impl;

import javax.annotation.Nonnull;
import java.util.Comparator;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.DefaultFilterSortCriterion;
import it.tidalwave.bluemarine2.model.MediaItem;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.role.ui.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class MediaItemComparator extends DefaultFilterSortCriterion<As>
  {
    private static final long serialVersionUID = 3413093735254009245L;

    private final static Comparator<As> COMPARATOR = (o1, o2) ->
      {
        try
          {
            // FIXME: use comparators chaining for lambdas
            final MediaItem mi1 = o1.as(MediaItem.class);
            final MediaItem mi2 = o2.as(MediaItem.class);
            final MediaItem.Metadata m1 = mi1.getMetadata();
            final MediaItem.Metadata m2 = mi2.getMetadata();
            final int d1 = m1.get(DISK_NUMBER).orElse(1);
            final int d2 = m2.get(DISK_NUMBER).orElse(1);
            final int t1 = m1.get(TRACK_NUMBER).orElse(0);
            final int t2 = m2.get(TRACK_NUMBER).orElse(0);

            if (d1 != d2)
              {
                return d1 - d2;
              }

            if (t1 != t2)
              {
                return t1 - t2;
              }
          }
        catch (AsException e) // FIXME: useless?
          {
          }

        return displayNameOf(o1).compareTo(displayNameOf(o2));
      };

    public MediaItemComparator()
      {
        super(COMPARATOR, "MediaItemComparator");
      }

    @Nonnull
    private static String displayNameOf (final @Nonnull As object)
      {
        return object.asOptional(Displayable).map(d -> d.getDisplayName()).orElse("???");
      }
  }
