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
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.nio.file.Path;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.model.Entity;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter.ContentHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode.*;
import static it.tidalwave.bluemarine2.util.PrettyPrint.xmlPrettyPrinted;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter.DIDLAdapter;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.UpnpUtilities.*;

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
@Slf4j
public class ContentDirectoryClingAdapter extends AbstractContentDirectoryService
  {
    @Inject
    private ContentDirectory contentDirectory;

    @RequiredArgsConstructor @EqualsAndHashCode @ToString
    static class BrowseParams
      {
        private final String objectId;
        private final BrowseFlag browseFlag;
        private final String filter;
        private final long firstResult;
        private final long maxResults;
        private final SortCriterion[] orderby;
      }

    /**
     * This cache is just a palliative. An effective cache should be placed on the Repository finder.
     */
    private final Map<BrowseParams, Object> cache = new ConcurrentHashMap<>();

    /*******************************************************************************************************************
     *
     * Returns information about an object.
     *
     * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
     *
     * @param   objectId        the id of the object to browse
     * @param   browseFlag      whether metadata or children content are requested
     * @param   filter          a filter for returned data
     * @param   firstResult     the first index of the items to return
     * @param   maxResults      the maximum number of items to return
     * @param   orderby         the sort criteria
     *
     ******************************************************************************************************************/
    @Override
    public BrowseResult browse (final @Nonnull String objectId,
                                final @Nonnull BrowseFlag browseFlag,
                                final String filter,
                                final long firstResult,
                                final long maxResults,
                                final SortCriterion[] orderby)
      throws ContentDirectoryException
      {
        log.info("browse({}, {}, filter: {}, startingIndex: {}, requestedCount: {}, sortCriteria: {})",
                 objectId, browseFlag, filter, firstResult, maxResults, orderby);
        // this repeated log is for capturing test recordings
        log.trace("browse @@@ {} @@@ {} @@@ {} @@@ {} @@@ {} @@@ {})",
                 objectId, browseFlag, firstResult, maxResults, filter, orderby);

        final BrowseParams params = new BrowseParams(objectId, browseFlag, filter, firstResult, maxResults, orderby);
        final Object result = cache.computeIfAbsent(params, key ->
                computeResult(objectId, browseFlag, filter, firstResult, maxResults, orderby));

        if (result instanceof ContentDirectoryException)
          {
            throw (ContentDirectoryException)result;
          }

        log(">>>> returning", (BrowseResult) result);
        return (BrowseResult) result;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private Object computeResult (final @Nonnull String objectId,
                                  final @Nonnull BrowseFlag browseFlag,
                                  final String filter,
                                  final long firstResult,
                                  final long maxResults,
                                  final SortCriterion[] orderby)
      {
        try
          {
            final Path path = didlIdToPath(objectId);
            final Entity entity = contentDirectory.findRoot()
                                                  .findChildren()
                                                  .withPath(path)
                                                  .optionalResult()
                                                  .orElseThrow(() -> new ContentDirectoryException(NO_SUCH_OBJECT));
            log.debug(">>>> found {}", entity);
            final ContentHolder holder = entity.as(DIDLAdapter).toContent(browseFlag,
                                                                          (int)firstResult,
                                                                          maxCount(maxResults));
            final DIDLParser parser = new DIDLParser();
            return new BrowseResult(parser.generate(holder.getContent()),
                                                           holder.getNumberReturned(),
                                                           holder.getTotalMatches(),
                                                           1); /// FIXME: updateId
          }
        catch (ContentDirectoryException e)
          {
            log.error("", e);
            return e;
          }
        catch (Exception e)
          {
            log.error("", e);
            return new ContentDirectoryException(CANNOT_PROCESS);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private static void log (final @Nonnull String message, final @Nonnull BrowseResult browseResult)
      {
        if (log.isDebugEnabled())
          {
            log.debug("{} BrowseResult(xml below, {}, {}, {})",
                    message,
                    browseResult.getCountLong(),
                    browseResult.getTotalMatchesLong(),
                    browseResult.getContainerUpdateIDLong());
            Stream.of(xmlPrettyPrinted(browseResult.getResult()).split("\n"))
                                                                .forEach(s -> log.debug("{} {}", message, s));
          }
      }
  }
