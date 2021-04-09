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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.nio.file.Path;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import it.tidalwave.bluemarine2.model.spi.CacheManager;
import it.tidalwave.bluemarine2.model.spi.CacheManager.Cache;
import it.tidalwave.bluemarine2.model.spi.Entity;
import it.tidalwave.bluemarine2.mediaserver.ContentDirectory;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter.ContentHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode.*;
import static it.tidalwave.bluemarine2.util.Formatters.*;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.UpnpUtilities.*;
import static it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl.DIDLAdapter._DIDLAdapter_;

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
 *
 **********************************************************************************************************************/
@Slf4j
public class ContentDirectoryClingAdapter extends AbstractContentDirectoryService
  {
    @Inject
    private ContentDirectory contentDirectory;

    @Inject
    private CacheManager cacheManager;

    private final AtomicInteger updateId = new AtomicInteger(1); // FIXME: increment on database update

    @RequiredArgsConstructor @EqualsAndHashCode @ToString
    static class BrowseParams
      {
        @Nonnull
        private final String objectId;
        @Nonnull
        private final BrowseFlag browseFlag;
        private final String filter;
        private final long firstResult;
        private final long maxResults;
        private final SortCriterion[] orderby;
      }

    /*******************************************************************************************************************
     *
     * Returns information about an object.
     *
     * @see http://upnp.org/specs/av/UPnP-av-ContentDirectory-v1-Service.pdf
     *
     * @param   objectId                    the id of the object to browse
     * @param   browseFlag                  whether metadata or children content are requested
     * @param   filter                      a filter for returned data
     * @param   firstResult                 the first index of the items to return
     * @param   maxResults                  the maximum number of items to return
     * @param   orderby                     the sort criteria
     * @return                              the requested object
     * @throws  ContentDirectoryException   in case of error
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

        final long baseTime = System.nanoTime();
        final BrowseParams params = new BrowseParams(objectId, browseFlag, filter, firstResult, maxResults, orderby);
        final Cache cache = cacheManager.getCache(getClass());
        final Object result = cache.getCachedObject(params, () -> findEntity(params));
        log.info(">>>> result computed in {} msec", (System.nanoTime() - baseTime) / 1E6);

        if (result instanceof ContentDirectoryException)
          {
            throw (ContentDirectoryException)result;
          }

        log(">>>> returning", (BrowseResult)result);
        return (BrowseResult)result;
      }

    /*******************************************************************************************************************
     *
     * Searches an entity.
     *
     * @param   params  the search parameters
     * @return          a {@link BrowseResult} if the requested entity has been found; a
     *                  {@link ContentDirectoryException} in case of error
     *
     ******************************************************************************************************************/
    @Nonnull
    private Object findEntity (final @Nonnull BrowseParams params)
      {
        try
          {
            final Path path = didlIdToPath(params.objectId);
            final Entity entity = contentDirectory.findRoot()
                                                  .findChildren()
                                                  .withPath(path)
                                                  .optionalResult()
                                                  .orElseThrow(() ->
                                                          new ContentDirectoryException(NO_SUCH_OBJECT,
                                                                    String.format("%s -> %s", params.objectId, path)));
            log.debug(">>>> found {}", entity);
            final ContentHolder holder = entity.as(_DIDLAdapter_).toContent(params.browseFlag,
                                                                          (int)params.firstResult,
                                                                          maxCount(params.maxResults));
            final DIDLParser parser = new DIDLParser();
            return new BrowseResult(parser.generate(holder.getContent()),
                                    holder.getNumberReturned(),
                                    holder.getTotalMatches(),
                                    updateId.get());
          }
        // this method returns exceptions as a result, so they can be cached
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
        log.info("{} BrowseResult(..., {}, {}, {})",
                 message,
                 browseResult.getCountLong(),
                 browseResult.getTotalMatchesLong(),
                 browseResult.getContainerUpdateIDLong());

        if (log.isDebugEnabled())
          {
            Stream.of(xmlPrettyPrinted(browseResult.getResult()).split("\n"))
                                                                .forEach(s -> log.debug("{} {}", message, s));
          }
      }
  }
