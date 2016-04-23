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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.concurrent.Immutable;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import it.tidalwave.bluemarine2.model.MediaFolder;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.mediaserver.impl.DefaultContentDirectory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.fourthline.cling.support.model.BrowseFlag;

/***********************************************************************************************************************
 *
 * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@UpnpService
  (
    serviceId = @UpnpServiceId("ContentDirectory"),
    serviceType = @UpnpServiceType(value = "ContentDirectory", version = 1)
  )
@Slf4j
public class ContentDirectoryClingAdapter
  {
//    @Inject FIXME doesn't work
    private ContentDirectory contentDirectory = new DefaultContentDirectory();

    @Immutable
    @AllArgsConstructor @Getter @ToString
    public static class BrowseResult
      {
        private final String result;
        private final int numberReturned;
        private final int totalMatches;
        private final int updateID;
      }

    @UpnpStateVariable(name = "ObjectID", defaultValue = "", sendEvents = false, datatype = "string")
    private String objectId;

    @UpnpStateVariable(name = "BrowseFlag", defaultValue = "", sendEvents = false, datatype = "string")
    private String browseFlag;

    @UpnpStateVariable(name = "Filter", defaultValue = "", sendEvents = false, datatype = "string")
    private String filter;

    @UpnpStateVariable(name = "StartingIndex", defaultValue = "0", sendEvents = false, datatype = "i4")
    private int startingIndex;

    @UpnpStateVariable(name = "RequestedCount", defaultValue = "0", sendEvents = false, datatype = "i4")
    private int requestedCount;

    @UpnpStateVariable(name = "SortCriteria", defaultValue = "", sendEvents = false, datatype = "string")
    private String sortCriteria;




    @UpnpStateVariable(name = "Result", defaultValue = "", sendEvents = false)
    private String result;

    @UpnpStateVariable(name = "NumberReturned", defaultValue = "0", sendEvents = false)
    private int numberReturned;

    @UpnpStateVariable(name = "TotalMatches", defaultValue = "0", sendEvents = false)
    private int totalMatches;

    @UpnpStateVariable(name = "UpdateID", defaultValue = "0", sendEvents = false)
    private int updateID;

    @UpnpStateVariable(name = "SearchCaps", defaultValue = "", sendEvents = false)
    private String searchCapabilities = "";

    @UpnpStateVariable(name = "SortCaps", defaultValue = "", sendEvents = false)
    private String sortCapabilities = "";

    @UpnpStateVariable(name = "Id", defaultValue = "", sendEvents = false)
    private String systemUpdateId = "";

    int xxxId = 3;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @UpnpAction(name = "Browse",
                out =
                  {
                    @UpnpOutputArgument(name="Result", getterName = "getResult"),
                    @UpnpOutputArgument(name="NumberReturned", getterName = "getNumberReturned"),
                    @UpnpOutputArgument(name="TotalMatches", getterName = "getTotalMatches"),
                    @UpnpOutputArgument(name="UpdateID", getterName = "getUpdateID")
                  })
    public BrowseResult browse (@UpnpInputArgument(name = "ObjectID") String objectId,
                                @UpnpInputArgument(name = "BrowseFlag") String browseFlag,
                                @UpnpInputArgument(name = "Filter") String filter,
                                @UpnpInputArgument(name = "StartingIndex") int startingIndex,
                                @UpnpInputArgument(name = "RequestedCount") int requestedCount,
                                @UpnpInputArgument(name = "SortCriteria") String sortCriteria)
      {
        try
          {
            log.info("browse(objectId: {}, browseFlag: {}, filter: {}, startingIndex: {}, requestedCount: {}, sortCriteria: {})",
                     objectId, browseFlag, filter, startingIndex, requestedCount, sortCriteria);

            // FIXME: search the child with the given objectId
            final MediaFolder root = contentDirectory.findRoot();
            final DIDLAdapter didlAdapter = new DIDLAdapter(root);
            final DIDLContent content = didlAdapter.toContent(BrowseFlag.valueOrNullOf(browseFlag));
            final DIDLParser parser = new DIDLParser();
            final int n = (int)content.getCount();
            final BrowseResult result = new BrowseResult(parser.generate(content), n, n, ++xxxId);
            log.info(">>>> returning {}", result);

            return result;
          }
        catch (Exception e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @UpnpAction(name = "GetSearchCapabilities",
                out = @UpnpOutputArgument(name = "SearchCaps"))
    public String getSearchCapabilities()
      {
        log.info("getSearchCapabilities");
        return searchCapabilities;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @UpnpAction(name = "GetSortCapabilities",
                out = @UpnpOutputArgument(name = "SortCaps"))
    public String getSortCapabilities()
      {
        log.info("getSortCapabilities");
        return sortCapabilities;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @UpnpAction(name = "GetSystemUpdateID",
                out = @UpnpOutputArgument(name = "Id"))
    public String getSystemUpdateID()
      {
        log.info("getSystemUpdateID");
        return systemUpdateId;
      }
  }
