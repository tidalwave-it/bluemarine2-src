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
import com.fasterxml.jackson.annotation.JsonView;
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
import static it.tidalwave.bluemarine2.util.FunctionWrappers._f;

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
    @JsonView(Profile.Master.class)
    @RequestMapping(value = "/record", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public RecordsJson getRecords (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                   final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
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
    @JsonView(Profile.Detail.class)
    @RequestMapping(value = "/record/{id}", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public RecordsJson getRecord (final @PathVariable String id,
                                  final @RequestParam(required = false, defaultValue = "embedded") String source,
                                  final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        return new RecordsJson(finalized(catalog.findRecords().withId(new Id(id)), source, fallback, RecordJson::new));
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
    @JsonView(Profile.Detail.class)
    @RequestMapping(value = "/record/{id}/track", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public TracksJson getRecordTracks (final @PathVariable String id,
                                       final @RequestParam(required = false, defaultValue = "embedded") String source,
                                       final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
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
    @JsonView(Profile.Master.class)
    @RequestMapping(value = "/track", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public TracksJson getTracks (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                 final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
        return new TracksJson(finalized(catalog.findTracks(), source, fallback, TrackJson::new));
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
    @JsonView(Profile.Master.class)
    @RequestMapping(value = "/audiofile", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public AudioFilesJson getAudioFiles (final @RequestParam(required = false, defaultValue = "embedded") String source,
                                         final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
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
    @JsonView(Profile.Master.class)
    @RequestMapping(value = "/audiofile/{id}", produces  = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    public AudioFilesJson getAudioFile (final @PathVariable String id,
                                        final @RequestParam(required = false, defaultValue = "embedded") String source,
                                        final @RequestParam(required = false, defaultValue = "embedded") String fallback)
      {
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
        final Optional<AudioFile> audioFile = catalog.findAudioFiles().withId(new Id(id)).optionalResult();
        return audioFile.flatMap(_f(AudioFile::getContent))
                        .map(bytes -> bytesResponse(bytes, "audio", "mpeg"))
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
        final Optional<AudioFile> audioFile = catalog.findAudioFiles().withId(new Id(id)).optionalResult();
        return audioFile.flatMap(file -> file.getMetadata().get(ARTWORK))
                        .flatMap(artworks -> artworks.stream().findFirst())
                        .map(bytes -> bytesResponse(bytes, "image", "jpeg"))
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
    private ResponseEntity<byte[]> bytesResponse (final @Nonnull byte[] bytes,
                                                  final @Nonnull String type,
                                                  final @Nonnull String subtype)
      {
        return ResponseEntity.ok().contentType(new MediaType(type, subtype)).body(bytes);
      }
  }
