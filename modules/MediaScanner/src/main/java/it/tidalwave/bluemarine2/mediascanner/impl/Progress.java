/*
 * #%L
 * *********************************************************************************************************************
 *
 * blueMarine2 - lightweight MediaCenter
 * http://bluemarine.tidalwave.it - hg clone https://bitbucket.org/tidalwave/bluemarine2-src
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
package it.tidalwave.bluemarine2.mediascanner.impl;

import javax.inject.Inject;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.bluemarine2.mediascanner.ScanCompleted;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString @Slf4j
public class Progress
  {
    @Inject
    private MessageBus messageBus;
    
    private volatile int totalFolders;
    private volatile int scannedFolders;
    private volatile int totalMediaItems;
    private volatile int importedMediaItems;
    private volatile int totalArtists;
    private volatile int importedArtists;
    private volatile int totalRecords;
    private volatile int importedRecords;
    private volatile int totalDownloads;
    private volatile int completedDownloads;
    
    // TODO: should also collect errors

    public synchronized void reset()
      {
        totalFolders = scannedFolders =
        totalMediaItems = importedMediaItems =
        totalDownloads = completedDownloads = 0;  
      }

    public synchronized void incrementTotalFolders()
      {
        totalFolders++;  
        check();
      }

    public synchronized void incrementScannedFolders()
      {
        scannedFolders++;  
        check();
      }

    public synchronized void incrementTotalMediaItems()
      {
        totalMediaItems++;  
        check();
      }

    public synchronized void incrementImportedMediaItems()
      {
        importedMediaItems++;  
        check();
      }

    public synchronized void incrementTotalArtists() 
      {
        totalArtists++;  
        check();
      }

    public synchronized void incrementImportedArtists()
      {
        importedArtists++;  
        check();
      }

    public synchronized void incrementTotalDownloads() 
      {
        totalDownloads++;  
        check();
      }

    public synchronized void incrementCompletedDownloads() 
      {
        completedDownloads++;  
        check();
      }

    public synchronized void incrementTotalRecords()
      {
        totalRecords++;  
        check();
      }

    public synchronized void incrementImportedRecords() 
      {
        importedRecords++;  
        check();
      }
    
    private void check()
      {
        log.debug("{}", this); // FIXME: is called from sync block

        if (isCompleted())
          {
            messageBus.publish(new ScanCompleted());
          }
      }

    public synchronized boolean isCompleted()
      {
        return (scannedFolders == totalFolders) 
            && (importedMediaItems == totalMediaItems)
            && (importedArtists == totalArtists)
            && (importedRecords == totalRecords)
            && (completedDownloads == totalDownloads);
      }
  }
