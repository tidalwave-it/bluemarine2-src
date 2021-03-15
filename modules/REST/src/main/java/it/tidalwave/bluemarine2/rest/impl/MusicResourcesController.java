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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import it.tidalwave.util.Finder;
import it.tidalwave.util.Id;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.message.PersistenceInitializedNotification;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.spi.SourceAwareFinder;
import it.tidalwave.bluemarine2.rest.impl.resource.TrackResource;
import it.tidalwave.bluemarine2.rest.impl.resource.RecordResource;
import it.tidalwave.bluemarine2.rest.impl.resource.DetailedRecordResource;
import it.tidalwave.bluemarine2.rest.impl.resource.AudioFileResource;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static it.tidalwave.role.ui.Displayable.Displayable;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.ARTWORK;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier.AudioFileSupplier;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RestController @SimpleMessageSubscriber @Slf4j
public class MusicResourcesController
  {
    static interface Streamable<ENTITY, FINDER extends SourceAwareFinder<FINDER, ENTITY>> extends SourceAwareFinder<ENTITY, FINDER>
      {
        public Stream<ENTITY> stream();
      }

    @ResponseStatus(value = NOT_FOUND)
    static class NotFoundException extends RuntimeException
      {
        private static final long serialVersionUID = 3099300911009857337L;
      }

    @ResponseStatus(value = SERVICE_UNAVAILABLE)
    static class UnavailableException extends RuntimeException
      {
        private static final long serialVersionUID = 3644567083880573896L;
      }

    @Inject
    private MediaCatalog catalog;

    private volatile boolean persistenceInitialized;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPersistenceInitializedNotification (final @ListensTo PersistenceInitializedNotification notification)
      throws IOException
      {
        log.info("onPersistenceInitializedNotification({})", notification);
        persistenceInitialized = false;
      }

    /*******************************************************************************************************************
     *
     * Exports record resources.
     *
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the records
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/record", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public List<RecordResource> getRecords (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                            final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getRecords({}, {})", source, fallback);
        checkStatus();
        return finalized(catalog.findRecords(), source, fallback, RecordResource::new);
      }

    /*******************************************************************************************************************
     *
     * Exports a single record resource.
     *
     * @param   id          the record id
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the record
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/record/{id}", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public DetailedRecordResource getRecord (final @PathVariable String id,
                                             final @RequestParam(required = false, defaultValue = "embedded") String source,
                                             final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getRecord({}, {}, {})", id, source, fallback);
        checkStatus();
        final List<TrackResource> tracks = finalized(catalog.findTracks().inRecord(new Id(id)), source, fallback, TrackResource::new);
        return single(finalized(catalog.findRecords().withId(new Id(id)), source, fallback,
                                record -> new DetailedRecordResource(record, tracks)));
      }

    /*******************************************************************************************************************
     *
     * Exports the cover art of a record.
     *
     * @param   id          the record id
     * @return              the cover art image
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/record/{id}/coverart")
    public ResponseEntity<byte[]> getRecordCoverArt (final @PathVariable String id)
      {
        log.info("getRecordCoverArt({})", id);
        checkStatus();
        return catalog.findTracks().inRecord(new Id(id))
                                   .stream()
                                   .flatMap(track -> track.asMany(AudioFileSupplier).stream())
                                   .map(afs -> afs.getAudioFile())
                                   .flatMap(af -> af.getMetadata().getAll(ARTWORK).stream())
                                   .findAny()
                                   .map(bytes -> bytesResponse(bytes, "image", "jpeg", "coverart.jpg"))
                                   .orElseThrow(NotFoundException::new);
      }

    /*******************************************************************************************************************
     *
     * Exports track resources.
     *
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the tracks
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/track", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public List<TrackResource> getTracks (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                          final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getTracks({}, {})", source, fallback);
        checkStatus();
        return finalized(catalog.findTracks(), source, fallback, TrackResource::new);
      }

    /*******************************************************************************************************************
     *
     * Exports a single track resource.
     *
     * @param   id          the track id
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the track
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/track/{id}", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public TrackResource getTrack (final @PathVariable String id,
                                   final @RequestParam(required = false, defaultValue = "embedded") String source,
                                   final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getTrack({}, {}, {})", id, source, fallback);
        checkStatus();
        return single(finalized(catalog.findTracks().withId(new Id(id)), source, fallback, TrackResource::new));
      }

    /*******************************************************************************************************************
     *
     * Exports audio file resources.
     *
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the audio files
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/audiofile", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public List<AudioFileResource> getAudioFiles (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                                  final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getAudioFiles({}, {})", source, fallback);
        checkStatus();
        return finalized(catalog.findAudioFiles(), source, fallback, AudioFileResource::new);
      }

    /*******************************************************************************************************************
     *
     * Exports a single audio file resource.
     *
     * @param   id          the audio file id
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the audio file
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/audiofile/{id}", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public AudioFileResource getAudioFile (final @PathVariable String id,
                                           final @RequestParam(required = false, defaultValue = "embedded") String source,
                                           final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getAudioFile({}, {}, {})", id, source, fallback);
        checkStatus();
        return single(finalized(catalog.findAudioFiles().withId(new Id(id)), source, fallback, AudioFileResource::new));
      }

    /*******************************************************************************************************************
     *
     * @param   id          the audio file id
     * @param   rangeHeader the "Range" HTTP header
     * @return              the binary contents
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/audiofile/{id}/content")
    public ResponseEntity<ResourceRegion> getAudioFileContent (
            final @PathVariable String id,
            final @RequestHeader(name = "Range", required = false) String rangeHeader)
      {
        log.info("getAudioFileContent({})", id);
        checkStatus();
        return catalog.findAudioFiles().withId(new Id(id)).optionalResult()
                                                          .map(_f(af -> audioFileContentResponse(af, rangeHeader)))
                                                          .orElseThrow(NotFoundException::new);
      }

    /*******************************************************************************************************************
     *
     * @param   id          the audio file id
     * @return              the binary contents
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/audiofile/{id}/coverart")
    public ResponseEntity<byte[]> getAudioFileCoverArt (final @PathVariable String id)
      {
        log.info("getAudioFileCoverArt({})", id);
        checkStatus();
        final Optional<AudioFile> audioFile = catalog.findAudioFiles().withId(new Id(id)).optionalResult();
        log.debug(">>>> audioFile: {}", audioFile);
        return audioFile.flatMap(file -> file.getMetadata().getAll(ARTWORK).stream().findFirst())
                        .map(bytes -> bytesResponse(bytes, "image", "jpeg", "coverart.jpg"))
                        .orElseThrow(NotFoundException::new);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private <T> T single (final @Nonnull List<T> list)
      {
        if (list.isEmpty())
          {
            throw new NotFoundException();
          }

        return list.get(0);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private <ENTITY, FINDER extends SourceAwareFinder<ENTITY, FINDER>, JSON>
        List<JSON> finalized (final @Nonnull FINDER finder,
                              final @Nonnull String source,
                              final @Nonnull String fallback,
                              final @Nonnull Function<ENTITY, JSON> mapper)
      {
        final FINDER f = finder.importedFrom(new Id(source))
                                .withFallback(new Id(fallback));
        return ((Finder<ENTITY>)f) // FIXME: hacky, because SourceAwareFinder does not extends Finder
                     .stream()
                     .map(mapper)
                     .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<ResourceRegion> audioFileContentResponse (final @Nonnull AudioFile file,
                                                                     final @CheckForNull String rangeHeader)
      throws IOException
      {
        final long length = file.getSize();
        final List<Range> ranges = Range.fromHeader(rangeHeader, length);

        if (ranges.size() > 1)
          {
            throw new RuntimeException("Can't support multi-range" + ranges); // FIXME
          }

        // E.g. HTML5 <audio> crashes if fed with too many data.
        final long maxSize = (rangeHeader != null) ? 1024*1024 : length;
        final Range fullRange = Range.full(length);
        final Range range = ranges.stream().findFirst().orElse(fullRange).subrange(maxSize);

        final String displayName = file.as(Displayable).getDisplayName(); // FIXME: getRdfsLabel()
        final HttpStatus status = range.equals(fullRange) ? OK : PARTIAL_CONTENT;
        return file.getContent().map(resource -> ResponseEntity.status(status)
                                                            .contentType(new MediaType("audio", "mpeg"))
                                                            .header(CONTENT_DISPOSITION, contentDisposition(displayName))
                                                            .body(range.getRegion(resource)))
                                .orElseThrow(NotFoundException::new);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<byte[]> bytesResponse (final @Nonnull byte[] bytes,
                                                  final @Nonnull String type,
                                                  final @Nonnull String subtype,
                                                  final @Nonnull String contentDisposition)
      {
        return ResponseEntity.ok()
                             .contentType(new MediaType(type, subtype))
                             .contentLength(bytes.length)
                             .header(CONTENT_DISPOSITION, contentDisposition(contentDisposition))
                             .body(bytes);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String contentDisposition (final @Nonnull String string)
      {
        try
          {
            // See https://tools.ietf.org/html/rfc6266#section-5
            return String.format("filename=\"%s\"; filename*=utf-8''%s", string, URLEncoder.encode(string, "UTF-8"));
          }
        catch (UnsupportedEncodingException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void checkStatus()
      {
        if (persistenceInitialized)
          {
            throw new UnavailableException();
          }
      }
  }
