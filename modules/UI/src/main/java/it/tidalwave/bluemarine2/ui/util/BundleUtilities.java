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
package it.tidalwave.bluemarine2.ui.util;

import javax.annotation.Nonnull;
import java.util.ResourceBundle;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.spi.DefaultDisplayable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * A collection of static utility methods for managing internationalized labels.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BundleUtilities 
  {
    /*******************************************************************************************************************
     *
     * Creates a {@link Displayable} from a resource bundle. The bundle resource file is named {@code Bundle.properties}
     * and it should be placed in the same package as the owner class.
     * 
     * @param   ownerClass  the class that owns the bundle
     * @param   key         the resource key
     * @return              the {@code Displayable}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Displayable displayableFromBundle (final @Nonnull Class<?> ownerClass, final @Nonnull String key)
      {
        final String packageName = ownerClass.getPackage().getName();
        final ResourceBundle bundle = ResourceBundle.getBundle(packageName + ".Bundle");
    
        return new DefaultDisplayable(bundle.getString(key));
      }
  }
