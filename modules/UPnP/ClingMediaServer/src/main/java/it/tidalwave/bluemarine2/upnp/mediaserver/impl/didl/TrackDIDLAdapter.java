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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.io.IOException;
import java.nio.file.Files;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;
import org.fourthline.cling.support.model.item.MusicTrack;
import it.tidalwave.role.Displayable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.AudioFile;
import it.tidalwave.bluemarine2.model.Track;
import it.tidalwave.bluemarine2.upnp.mediaserver.impl.ResourceServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Identifiable.Identifiable;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier.AudioFileSupplier;
import java.time.Duration;
import java.time.temporal.TemporalUnit;

/***********************************************************************************************************************
 *
 * The {@link DIDLAdapter} for {@link Track}.
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
@Immutable @DciRole(datumType = Track.class)
public class TrackDIDLAdapter implements DIDLAdapter
  {
    @Nonnull
    private final Track datum;

    @Nonnull
    private final ResourceServer resourceServer;

    @Override @Nonnull
    public DIDLObject toObject()
      throws IOException
      {
        log.debug("toObject() - {}", datum);
        // parentID not set here

        final ProtocolInfo protocolInfo = new DLNAProtocolInfo(Protocol.HTTP_GET, "*", "audio/mpeg", "*"); // FIXME: MIME
        final AudioFile audioFile = datum.as(AudioFileSupplier).getAudioFile();

        final Long size = Files.exists(audioFile.getPath()) ? Files.size(audioFile.getPath()) : null; // FIXME: should be in metadata
        
        final Res resource = new Res(protocolInfo, size, resourceServer.urlForResource(audioFile));
        resource.setDuration(durationToString(datum.getDuration()));
//        resource.setBitrate(size); // TODO
//        resource.setBitsPerSample(size); // TODO
//        resource.setNrAudioChannels(size); // TODO
//        resource.setSampleFrequency(size); // TODO

        final MusicTrack item = new MusicTrack();
        item.setId(datum.as(Identifiable).getId().stringValue());
        item.setTitle(datum.asOptional(Displayable.Displayable).map(d -> d.getDisplayName()).orElse("???"));
        item.setOriginalTrackNumber(datum.getTrackNumber());
        item.setResources(Collections.singletonList(resource));
//        datum.getDiskNumber();
        item.setRestricted(false);

        return item;
      }

    @Nonnull
    private static String durationToString (final @Nonnull Duration duration)
      {
        final int d = (int)duration.getSeconds();
        final int seconds = d % 60;
        final int minutes = (d / 60) % 60;
        final int hours = d / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
      }

    /*
        // TODO     <desc id="cdudn" nameSpace="urn:schemas-rinconnetworks-com:metadata-1-0/">SA_RINCON5127_42????35</desc>

          <item id="T:\public_html\Jukebox\mp3\_Presets\10.dpl/0" parentID="T:\public_html\Jukebox\mp3\_Presets\10.dpl" restricted="False">
        <dc:title xmlns:dc="http://purl.org/dc/elements/1.1/">Mr. Rock &amp; Roll</dc:title>
            <upnp:class xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">object.item.audioItem.musicTrack</upnp:class>
            <res duration="00:03:38" protocolInfo="http-get:*:taglib/mp3:*">http://eng.linn.co.uk/~joshh/Jukebox/mp3/Amy%20Macdonald/This%20Is%20The%20Life/Mr.%20Rock%20&amp;%20Roll.mp3</res>
        <upnp:albumArtURI xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">http://eng.linn.co.uk/~joshh/Jukebox/mp3/Amy%20Macdonald/This%20Is%20The%20Life/Folder.jpg</upnp:albumArtURI>
        <upnp:artworkURI xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">http://eng.linn.co.uk/~joshh/Jukebox/mp3/Amy%20Macdonald/This%20Is%20The%20Life/Folder.jpg</upnp:artworkURI>
        <upnp:genre xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">Pop</upnp:genre>
        <upnp:artist role="Performer" xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">Amy Macdonald</upnp:artist>
        <upnp:artist role="Composer" xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">Unknown</upnp:artist>
        <upnp:artist role="AlbumArtist" xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">Unknown</upnp:artist>
        <upnp:artist role="Conductor" xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">Unknown</upnp:artist>
        <upnp:album xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">This Is The Life</upnp:album>
            <upnp:originalTrackNumber xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">1</upnp:originalTrackNumber>
        <dc:date xmlns:dc="http://purl.org/dc/elements/1.1/">Unknown</dc:date>
        <upnp:originalDiscNumber xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">0</upnp:originalDiscNumber>
        <upnp:originalDiscCount xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">0</upnp:originalDiscCount>
      </item>

    */
  }
