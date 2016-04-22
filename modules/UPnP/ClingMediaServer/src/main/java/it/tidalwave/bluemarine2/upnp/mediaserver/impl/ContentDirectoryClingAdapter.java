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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.StorageFolder;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.Finder;
import it.tidalwave.util.Id;
import it.tidalwave.util.spi.AsSupport;
import it.tidalwave.role.Identifiable;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.spi.DefaultSimpleComposite;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.mediaserver.DefaultContentDirectory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.Identifiable.Identifiable;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;

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
    private ContentDirectory contentDirectory = new DefaultContentDirectory(); // FIXME: @Inject

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

            final DIDLContent content = new DIDLContent();

            if ("BrowseMetadata".equals(browseFlag))
              {
                final Container container = new Container();
                content.addContainer(container);
                container.setRestricted(false);
                container.setChildCount(0);
                container.setId(objectId);
                container.setParentID("-1");
                container.setClazz(StorageFolder.CLASS);
                container.setTitle("Root");
                container.setCreator("blueMarine II");
                container.setItems(Collections.emptyList());
//                container.getSearchClasses().add(PhotoAlbum.CLASS);
//                container.getSearchClasses().add(MusicAlbum.CLASS);
//                container.getSearchClasses().add("object.item.imageItem.photo");
              }
            else if ("BrowseDirectChildren".equals(browseFlag))
              {
                final As root = new AsSupport(null,
                    (Identifiable) () -> new Id("0"),
                    new DefaultDisplayable("Root"),
                    new DefaultSimpleComposite<>(contentDirectory.findObjects())
                );

                final Container c1 = asContainer(root, objectId, false);

                c1.getContainers().stream().forEach(ccc -> content.addContainer(ccc));
              }

            final DIDLParser dp = new DIDLParser();

            final int n = (int)content.getCount();
            final BrowseResult result = new BrowseResult(dp.generate(content), n, n, ++xxxId);
            log.info(">>>> returning {}", result);

            return result;
          }
        catch (Exception e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }
      }

    @UpnpAction(name = "GetSearchCapabilities",
                out = @UpnpOutputArgument(name = "SearchCaps"))
    public String getSearchCapabilities()
      {
        log.info("getSearchCapabilities");
        return searchCapabilities;
      }

    @UpnpAction(name = "GetSortCapabilities",
                out = @UpnpOutputArgument(name = "SortCaps"))
    public String getSortCapabilities()
      {
        log.info("getSortCapabilities");
        return sortCapabilities;
      }

    @UpnpAction(name = "GetSystemUpdateID",
                out = @UpnpOutputArgument(name = "Id"))
    public String getSystemUpdateID()
      {
        log.info("getSystemUpdateID");
        return systemUpdateId;
      }

    @Nonnull
    private Container asContainer (final @Nonnull As asObject,
                                   final @Nonnull String parentId,
                                   final boolean metadataOnly) // FIXME: use Enum
      {
        final Container container = new Container();
        container.setRestricted(false);
        container.setId(asObject.as(Identifiable).getId().stringValue());
        container.setParentID(parentId);
        container.setClazz(StorageFolder.CLASS); // FIXME: container
        container.setTitle(asObject.as(Displayable).getDisplayName());
        container.setCreator("blueMarine II"); // FIXME

        try
          {
            final Finder<As> children = asObject.as(SimpleComposite).findChildren();
            container.setChildCount(children.count());

            if (!metadataOnly)
              {
                final List<Container> childContainers = container.getContainers();
                children.results().stream().forEach(child ->
                        childContainers.add(asContainer(child, container.getId(), metadataOnly)));
              }
          }
        catch (AsException e)
          {
            container.setChildCount(0);
            container.setItems(Collections.emptyList());
          }

        return container;
      }
  }
