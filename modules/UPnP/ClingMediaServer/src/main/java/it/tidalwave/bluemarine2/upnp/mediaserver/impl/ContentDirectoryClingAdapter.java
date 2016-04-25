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

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.util.PrettyPrint.*;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.DIDLAdapter.DIDLAdapter;

/***********************************************************************************************************************
 *
 * This class implements a minimal "ContentDirectory" compliant to the UPnP specifications. It acts as an adapter with
 * respect to a {@link ContentDirectory}, which provides contents.
 *
 * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
 *
 * @stereotype Adapter
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
    @Inject
    private ContentDirectory contentDirectory;

    @Immutable
    @AllArgsConstructor @Getter
    public static class BrowseResult
      {
        private final String result;
        private final int numberReturned;
        private final int totalMatches;
        private final int updateID;

        @Override
        public String toString()
          {
            return String.format("BrowseResult(numberReturned=%d, totalMatches=%d, updateID=%d, result=%s)",
                    numberReturned, totalMatches, updateID, xmlPrettyPrinted(result));
          }
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

    /*******************************************************************************************************************
     *
     * Returns information about an object.
     *
     * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
     *
     * @param   objectId        the id of the object to browse
     * @param   browseFlag      whether metadata or children content are requested
     * @param   filter          a filter for returned data
     * @param   startingIndex   the first index of the items to return
     * @param   requestedCount  the maximum number of items to return
     * @param   sortCriteria    the sort criteria
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
    public BrowseResult browse (final @UpnpInputArgument(name = "ObjectID") String objectId,
                                final @UpnpInputArgument(name = "BrowseFlag") String browseFlag,
                                final @UpnpInputArgument(name = "Filter") String filter,
                                final @UpnpInputArgument(name = "StartingIndex") int startingIndex,
                                final @UpnpInputArgument(name = "RequestedCount") int requestedCount,
                                final @UpnpInputArgument(name = "SortCriteria") String sortCriteria)
      {
        try
          {
            log.info("browse({}, {}, filter: {}, startingIndex: {}, requestedCount: {}, sortCriteria: {})",
                     objectId, browseFlag, filter, startingIndex, requestedCount, sortCriteria);

            final Path path = Paths.get(objectId.equals("0") ? "/" : objectId);
            final Entity entity = contentDirectory.findRoot().findChildren().withPath(path).result();
            final DIDLAdapter didlAdapter = entity.as(DIDLAdapter);
            final DIDLContent content = didlAdapter.toContent(BrowseFlag.valueOrNullOf(browseFlag),
                                                              startingIndex,
                                                              maxCount(requestedCount));
            final int numberReturned = didlAdapter.getNumberReturned();
            final int totalMatches = didlAdapter.getTotalMatches();
            final DIDLParser parser = new DIDLParser();
            final BrowseResult result = new BrowseResult(parser.generate(content), numberReturned, totalMatches, 1);

            if (log.isDebugEnabled()) // result.toString() is expensive
              {
                log.debug(">>>> returning {}", result);
              }

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
     * Returns the search capabilities
     *
     * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
     *
     * @return      the search capabilities
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
     * Returns the sort capabilities
     *
     * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
     *
     * @return      the sort capabilities
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
     * Returns the System UpdateID
     *
     * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
     *
     * @return      the System UpdateID
     *
     ******************************************************************************************************************/
    @UpnpAction(name = "GetSystemUpdateID",
                out = @UpnpOutputArgument(name = "Id"))
    public String getSystemUpdateID()
      {
        log.info("getSystemUpdateID");
        return systemUpdateId;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnegative
    private static int maxCount (final @Nonnegative int value)
      {
        return (value == 0) ? Integer.MAX_VALUE : value;
      }
  }
