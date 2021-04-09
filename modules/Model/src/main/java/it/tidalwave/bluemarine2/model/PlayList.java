/*
 * *********************************************************************************************************************
 *
 * blueMarine II: Semantic Media Centre
 * http://tidalwave.it/projects/bluemarine2
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine2-src
 * git clone https://github.com/tidalwave-it/bluemarine2-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.bluemarine2.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.experimental.Accessors;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class PlayList<ENTITY>
  {
    @Getter @Nonnull
    private Optional<ENTITY> currentItem;

    @Nonnull
    private final List<ENTITY> items;

    @Getter @Nonnegative
    private int index;

    @Getter @Accessors(fluent = true)
    private final BooleanProperty hasPreviousProperty = new SimpleBooleanProperty();

    @Getter @Accessors(fluent = true)
    private final BooleanProperty hasNextProperty = new SimpleBooleanProperty();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private PlayList()
      {
        currentItem = Optional.empty();
        items = Collections.emptyList();
      }

    /*******************************************************************************************************************
     *
     * Creates a new instance out of a collection of items, with the given current item.
     *
     * @param   currentItem     the item designated to be current
     * @param   items           all the items - if empty, a playlist with a single element will be created
     *
     ******************************************************************************************************************/
    public PlayList (final @Nonnull ENTITY currentItem, final @Nonnull Collection<ENTITY> items)
      {
        this.items = new ArrayList<>(items.isEmpty() ? List.of(currentItem) : items);
        this.currentItem = Optional.of(currentItem);
        this.index = this.items.indexOf(currentItem);
        update();
      }

    /*******************************************************************************************************************
     *
     * Returns an empty playlist.
     *
     * @return  an empty playlist
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <T> PlayList<T> empty()
      {
        return new PlayList<>();
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} if there is a previous item.
     *
     * @return  {@code true} if there is a previous item
     *
     ******************************************************************************************************************/
    public boolean hasPrevious()
      {
        return hasPreviousProperty.get();
      }

    /*******************************************************************************************************************
     *
     * Returns {@code true} if there is a next item.
     *
     * @return  {@code true} if there is a next item
     *
     ******************************************************************************************************************/
    public boolean hasNext()
      {
        return hasNextProperty.get();
      }

    /*******************************************************************************************************************
     *
     * Moves back to the previous item, if present, and returns it.
     *
     * @return  the previous item
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<ENTITY> previous()
      {
        currentItem = Optional.of(items.get(--index));
        update();
        return currentItem;
      }

    /*******************************************************************************************************************
     *
     * Moves forward to the next item, if present, and returns it.
     *
     * @return  the next item
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<ENTITY> next()
      {
        currentItem = Optional.of(items.get(++index));
        update();
        return currentItem;
      }

    /*******************************************************************************************************************
     *
     * Returns the next item, if present, without making it the current one.
     *
     * @return  the next item
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<ENTITY> peekNext()
      {
        return hasNext() ? Optional.of(items.get(index + 1)) : Optional.empty();
      }

    /*******************************************************************************************************************
     *
     * Return the number of items in this play list.
     *
     * @return  the number of items
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getSize()
      {
        return items.size();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void update()
      {
        hasPreviousProperty.set(index > 0);
        hasNextProperty.set(index < items.size() - 1);
      }
  }
