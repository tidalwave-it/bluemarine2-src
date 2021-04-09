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
package it.tidalwave.bluemarine2.persistence;

import java.nio.file.Path;
import it.tidalwave.util.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistencePropertyNames
  {
    private static final String PREFIX = PersistencePropertyNames.class.getPackage().getName();

    /** The path where to place disk storage. */
    public static final Key<Path> STORAGE_FOLDER = Key.of(PREFIX + ".storagePath", Path.class);

    /** The path of a file to import data from - used by tests. */
    public static final Key<Path> IMPORT_FILE = Key.of(PREFIX + ".importFile", Path.class);

    /** Whether the import file must be renamed after being used. */
    public static final Key<Boolean> RENAME_IMPORT_FILE = Key.of(PREFIX + ".renameImportFile", Boolean.class);
  }
