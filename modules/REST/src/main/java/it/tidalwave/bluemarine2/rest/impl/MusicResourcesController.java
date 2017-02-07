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
package it.tidalwave.bluemarine2.rest.impl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import it.tidalwave.util.Finder8;
import it.tidalwave.util.Id;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.message.PersistenceInitializedNotification;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.MediaCatalog;
import it.tidalwave.bluemarine2.model.finder.SourceAwareFinder;
import it.tidalwave.bluemarine2.model.impl.catalog.RepositoryMediaCatalog;
import it.tidalwave.bluemarine2.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.ARTWORK;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier.AudioFileSupplier;
import static it.tidalwave.bluemarine2.util.FunctionWrappers._f;
import static it.tidalwave.role.Displayable.Displayable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@RestController @SimpleMessageSubscriber @Slf4j
public class MusicResourcesController
  {
    static interface Streamable<ENTITY, FINDER extends SourceAwareFinder<FINDER, ENTITY>> extends SourceAwareFinder<ENTITY, FINDER>
      {
        public Stream<ENTITY> stream();
      }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    static class NotFoundException extends RuntimeException
      {
        private static final long serialVersionUID = 3099300911009857337L;
      }

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    static class UnavailableException extends RuntimeException
      {
        private static final long serialVersionUID = 3644567083880573896L;
      }

    private MediaCatalog catalog; // FIXME: directly inject the Catalog

    @Inject
    public Persistence persistence;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onPersistenceInitializedNotification (final @ListensTo PersistenceInitializedNotification notification)
      throws IOException
      {
        log.info("onPersistenceInitializedNotification({})", notification);
        catalog = new RepositoryMediaCatalog(persistence.getRepository());
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
    public RecordsJson getRecords (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                   final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getRecords({}, {})", source, fallback);
        checkStatus();
        return new RecordsJson(finalized(catalog.findRecords(), source, fallback, RecordJson::new));
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
    public RecordsJson getRecord (final @PathVariable String id,
                                  final @RequestParam(required = false, defaultValue = "embedded") String source,
                                  final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getRecord({}, {}, {})", id, source, fallback);
        checkStatus();
        return new RecordsJson(finalized(catalog.findRecords().withId(new Id(id)), source, fallback, RecordJson::new));
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
     * Exports track resources in the given record.
     *
     * @param   id          the record id
     * @param   source      the data source
     * @param   fallback    the fallback data source
     * @return              the JSON representation of the tracks
     *
     ******************************************************************************************************************/
    @ResponseBody
    @RequestMapping(value = "/record/{id}/track", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public TracksJson getRecordTracks (final @PathVariable String id,
                                       final @RequestParam(required = false, defaultValue = "embedded") String source,
                                       final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getRecordTracks({}, {}, {})", id, source, fallback);
        checkStatus();
        return new TracksJson(finalized(catalog.findTracks().inRecord(new Id(id)), source, fallback, TrackJson::new));
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
    public TracksJson getTracks (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                 final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getTracks({}, {})", source, fallback);
        checkStatus();
        return new TracksJson(finalized(catalog.findTracks(), source, fallback, TrackJson::new));
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
    public TracksJson getTrack (final @PathVariable String id,
                                final @RequestParam(required = false, defaultValue = "embedded") String source,
                                final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getTrack({}, {}, {})", id, source, fallback);
        checkStatus();
        return new TracksJson(finalized(catalog.findTracks().withId(new Id(id)), source, fallback, TrackJson::new));
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
    public AudioFilesJson getAudioFiles (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                         final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getAudioFiles({}, {})", source, fallback);
        checkStatus();
        return new AudioFilesJson(finalized(catalog.findAudioFiles(), source, fallback, AudioFileJson::new));
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
    public AudioFilesJson getAudioFile (final @PathVariable String id,
                                        final @RequestParam(required = false, defaultValue = "embedded") String source,
                                        final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        log.info("getAudioFile({}, {}, {})", id, source, fallback);
        checkStatus();
        return new AudioFilesJson(finalized(catalog.findAudioFiles().withId(new Id(id)), source, fallback, AudioFileJson::new));
      }

    /*******************************************************************************************************************
     *
     * @param   id          the audio file id
     * @return              the binary contents
     *
     * FIXME: support ranges, use ResourceRegionHttpMessageConverter? Then drop the RangeServlet in favour of it.
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/audiofile/{id}/content")
    public ResponseEntity<byte[]> getAudioFileContent (final @PathVariable String id)
      {
        log.info("getAudioFileContent({})", id);
        checkStatus();
        return catalog.findAudioFiles().withId(new Id(id)).optionalResult()
                                                          .map(_f(this::audioFileContentResponse))
                                                          .orElseThrow(NotFoundException::new);
      }

    /*******************************************************************************************************************
     *
     * @param   id          the audio file id
     * @return              the binary contents
     *
     ******************************************************************************************************************/
    @RequestMapping(value = "/audiofile/{id}/coverart")
    public ResponseEntity<byte[]>  getAudioFileCoverArt (final @PathVariable String id)
      {
        log.info("getAudioFileCoverArt({})", id);
        checkStatus();
        final Optional<AudioFile> audioFile = catalog.findAudioFiles().withId(new Id(id)).optionalResult();
        return audioFile.flatMap(file -> file.getMetadata().getAll(ARTWORK).stream().findFirst())
                        .map(bytes -> bytesResponse(bytes, "image", "jpeg", "coverart.jpg"))
                        .orElseThrow(NotFoundException::new);
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
        return ((Finder8<ENTITY>)f) // FIXME: hacky, because SourceAwareFinder does not extends Finder8
                     .stream()
                     .map(mapper)
                     .collect(toList());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private ResponseEntity<byte[]> audioFileContentResponse (final @Nonnull AudioFile file)
      throws IOException
      {
        final String displayName = file.as(Displayable).getDisplayName(); // FIXME: getRdfsLabel()
        return file.getContent().map(bytes -> bytesResponse(bytes, "audio", "mpeg", displayName))
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
                             .header(CONTENT_DISPOSITION, contentDisposition)
                             .body(bytes);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void checkStatus()
      {
        if (catalog == null)
          {
            throw new UnavailableException();
          }
      }
  }
