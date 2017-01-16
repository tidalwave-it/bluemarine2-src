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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.annotation.Nonnegative;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.mediascanner.ScanCompleted;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString(exclude = { "messageBus", "all" }) @Slf4j
public class ProgressHandler
  {
    @RequiredArgsConstructor @Getter
    static class Progress
      {
        @NonNull
        private final String name;

        @Nonnegative
        private volatile int total;

        @Nonnegative
        private volatile int done;

        public void reset()
          {
            total = done = 0;
          }

        public synchronized void incrementTotal()
          {
            total++;
          }

        public synchronized void incrementDone()
          {
            done++;
          }

        public boolean completed()
          {
            return done == total;
          }

        @Override
        public String toString()
          {
            return String.format("%d/%d (%d%%)", done, total, (total == 0) ? 0 : (100 * done) / total);
          }
      }

    @Inject
    private MessageBus messageBus;

    private final Progress folders = new Progress("folders");
    private final Progress fingerprints = new Progress("fingerprints");
    private final Progress mediaItems = new Progress("mediaItems");
    private final Progress artists = new Progress("artists");
    private final Progress records = new Progress("records");
    private final Progress downloads = new Progress("downloads");
    private final Progress insertions = new Progress("insertions");

    private final List<Progress> all = Arrays.asList(folders, fingerprints, mediaItems, artists, records, downloads, insertions);

    // TODO: should also collect errors

    public synchronized void reset()
      {
        all.stream().forEach(progress -> progress.reset());
      }

    public void incrementTotalFolders()
      {
        folders.incrementTotal();
        check();
      }

    public void incrementScannedFolders()
      {
        folders.incrementDone();
        check();
      }

    public void incrementTotalMediaItems()
      {
        mediaItems.incrementTotal();
        fingerprints.incrementTotal();
        check();
      }

    public void incrementDoneFingerprints()
      {
        fingerprints.incrementDone();
        check();
      }

    public void incrementImportedMediaItems()
      {
        mediaItems.incrementDone();
        check();
      }

    public void incrementTotalArtists()
      {
        artists.incrementTotal();
        check();
      }

    public void incrementImportedArtists()
      {
        artists.incrementDone();
        check();
      }

    public void incrementTotalDownloads()
      {
        downloads.incrementTotal();
        check();
      }

    public void incrementCompletedDownloads()
      {
        downloads.incrementDone();
        check();
      }

    public void incrementTotalRecords()
      {
        records.incrementTotal();
        check();
      }

    public void incrementImportedRecords()
      {
        records.incrementDone();
        check();
      }

    public void incrementTotalInsertions()
      {
        insertions.incrementTotal();
        check();
      }

    public void incrementCompletedInsertions()
      {
        insertions.incrementDone();
        check();
      }

    private void check()
      {
        log.debug("{}", this);

        if (isCompleted())
          {
            messageBus.publish(new ScanCompleted());
          }
      }

    public synchronized boolean isCompleted()
      {
        return all.stream().allMatch(Progress::completed);
      }
  }
