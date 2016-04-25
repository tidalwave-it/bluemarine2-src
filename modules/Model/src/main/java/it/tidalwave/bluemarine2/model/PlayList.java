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
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.annotation.Nonnegative;
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

    @Getter
    private int index;
    
    @Getter @Accessors(fluent = true)
    private final BooleanProperty hasPreviousProperty = new SimpleBooleanProperty();
    
    @Getter @Accessors(fluent = true)
    private final BooleanProperty hasNextProperty = new SimpleBooleanProperty();
    
    private PlayList()
      {
        currentFile = Optional.empty(); 
        list = Collections.emptyList();
      }
    
    public PlayList (final @Nonnull AudioFile audioFile, final @Nonnull List<AudioFile> list) 
      {
        this.list = new ArrayList<>(list.isEmpty() ? Arrays.asList(audioFile) : list);
        index = this.list.indexOf(audioFile);
        this.currentFile = Optional.of(audioFile);
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
        currentFile = Optional.of(list.get(--index));
        update();
        return currentFile;
      }
    
    @Nonnull
    public Optional<AudioFile> next() 
      {
        currentFile = Optional.of(list.get(++index));
        update();
        return currentFile;
      }
    
    @Nonnull
    public Optional<AudioFile> peekNext() 
      {
        return hasNext() ? Optional.of(list.get(index + 1)) : Optional.empty();
      }
    
    @Nonnegative
    public int getSize()
      {
        return list.size();
      }
    
    private void update()
      {
        hasPreviousProperty.set(index > 0);
        hasNextProperty.set(index < list.size() - 1);
      }
  }
