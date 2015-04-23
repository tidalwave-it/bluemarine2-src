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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.experimental.Accessors;

/***********************************************************************************************************************
 *
 * FIXME: make it of MediaItem, not AudioFile
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PlayList
  {
    public static final PlayList EMPTY = new PlayList();
    
    @Getter
    private Optional<AudioFile> currentFile;
    
    private final List<AudioFile> list;
    
    private final ListIterator<AudioFile> iterator;
    
    @Getter @Accessors(fluent = true)
    private final BooleanProperty hasPreviousProperty = new SimpleBooleanProperty();
    
    @Getter @Accessors(fluent = true)
    private final BooleanProperty hasNextProperty = new SimpleBooleanProperty();
    
    private PlayList()
      {
        currentFile = Optional.empty(); 
        list = Collections.emptyList();
        iterator = list.listIterator();
      }
    
    public PlayList (final @Nonnull AudioFile audioFile, final @Nonnull List<AudioFile> list) 
      {
        this.list = new ArrayList<>(list.isEmpty() ? Arrays.asList(audioFile) : list);
        iterator = this.list.listIterator(this.list.indexOf(audioFile));
        currentFile = Optional.of(iterator.next());
        update();
      }
    
    public boolean hasPrevious()
      {
        return hasPreviousProperty.get();
      }

    public boolean hasNext()
      {
        return hasNextProperty.get();
      }

    @Nonnull
    public Optional<AudioFile> previous() 
      {
        currentFile = Optional.of(iterator.previous());
        update();
        return currentFile;
      }
    
    @Nonnull
    public Optional<AudioFile> next() 
      {
        currentFile = Optional.of(iterator.next());
        update();
        return currentFile;
      }
    
    private void update()
      {
        hasPreviousProperty.set(iterator.hasPrevious());
        hasNextProperty.set(iterator.hasNext());
      }
  }
