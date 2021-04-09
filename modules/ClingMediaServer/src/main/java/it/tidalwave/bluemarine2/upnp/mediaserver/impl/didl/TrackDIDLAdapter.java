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
package it.tidalwave.bluemarine2.upnp.mediaserver.impl.didl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.io.IOException;
import it.tidalwave.role.ui.Displayable;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;
import org.fourthline.cling.support.model.item.MusicTrack;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.audio.Track;
import it.tidalwave.bluemarine2.rest.spi.ResourceServer;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier._AudioFileSupplier_;
import static it.tidalwave.role.ui.Displayable._Displayable_;

/***********************************************************************************************************************
 *
 * The {@link DIDLAdapter} for {@link Track}.
 *
 * @stereotype Role
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
@Immutable @DciRole(datumType = Track.class)
public class TrackDIDLAdapter extends DIDLAdapterSupport<Track>
  {
    public TrackDIDLAdapter (final @Nonnull Track track ,final @Nonnull ResourceServer server)
      {
        super(track, server);
      }

    @Override @Nonnull
    public DIDLObject toObject()
      throws IOException
      {
        log.debug("toObject() - {}", datum);
        // parentID not set here
        final MusicTrack item = setCommonFields(new MusicTrack());
        final AudioFile audioFile = datum.as(_AudioFileSupplier_).getAudioFile();
        final Metadata trackMetadata = datum.getMetadata();
        item.addResource(audioResourceOf(audioFile));
        trackMetadata.get(TRACK_NUMBER).ifPresent(item::setOriginalTrackNumber);

        datum.getRecord().flatMap(record -> record.maybeAs(_Displayable_))
                         .map(Displayable::getDisplayName)
                         .ifPresent(item::setAlbum);

//        trackMetadata.get(DISK_COUNT);
//        trackMetadata.get(DISK_NUMBER);
//        trackMetadata.get(COMPOSER);
//        trackMetadata.get(ARTIST);
//        item.setContributors(contributors);
//        item.setCreator(creator);
//        item.setDate(date);
//        item.setDescMetadata(descMetadata);
//        item.setDescription(description);
//        item.setLanguage(language);
//        item.setLongDescription(description);
        item.setStorageMedium(StorageMedium.HDD);

//        item.setArtists(new PersonWithRole[] { new PersonWithRole("xyz", "AlbumArtist") });
//        item.setGenres(new String[] { "Classical" });
//        final Person publisher = new Person("Unknown");
//        item.setPublishers(new Person[] { publisher });

//        datum.getDiskNumber();

        return item;
      }

    @Nonnull
    private Res audioResourceOf (final @Nonnull AudioFile audioFile)
      {
        final ProtocolInfo protocolInfo = new DLNAProtocolInfo(Protocol.HTTP_GET, "*", "audio/mpeg", "*"); // FIXME: MIME
        final Metadata audioFileMetadata = audioFile.getMetadata();
        final Res resource = new Res(protocolInfo,
                                     audioFileMetadata.get(FILE_SIZE).orElse(null),
                                     server.absoluteUrl(String.format("rest/audiofile/%s/content", audioFile.getId().stringValue())));
        audioFileMetadata.get(DURATION).ifPresent(duration -> resource.setDuration(durationToString(duration)));
        audioFileMetadata.get(BIT_RATE).ifPresent(bitRate -> resource.setBitrate((long)(int)bitRate));
        audioFileMetadata.get(BITS_PER_SAMPLE).ifPresent(bitPerSample -> resource.setBitsPerSample((long)(int)bitPerSample));
        audioFileMetadata.get(CHANNELS).ifPresent(channels -> resource.setNrAudioChannels((long)(int)channels));
        audioFileMetadata.get(SAMPLE_RATE).ifPresent(sampleRate -> resource.setSampleFrequency((long)(int)sampleRate));

        return resource;
      }

    @Nonnull
    private static String durationToString (final @Nonnull Duration duration)
      {
        final int d = (int)duration.getSeconds();
        final int seconds = d % 60;
        final int minutes = (d / 60) % 60;
        final int hours = d / 3600;
        return String.format("%d:%02d:%02d.000", hours, minutes, seconds);
      }

    /*
        // TODO     <desc id="cdudn" nameSpace="urn:schemas-rinconnetworks-com:metadata-1-0/">SA_RINCON5127_42????35</desc>

    <item id="T:\public_html\Jukebox\mp3\_Presets\10.dpl/0" parentID="T:\public_html\Jukebox\mp3\_Presets\10.dpl" restricted="False">
        <upnp:albumArtURI">http://eng.linn.co.uk/~joshh/Jukebox/mp3/Amy%20Macdonald/This%20Is%20The%20Life/Folder.jpg</upnp:albumArtURI>
        <upnp:artworkURI">http://eng.linn.co.uk/~joshh/Jukebox/mp3/Amy%20Macdonald/This%20Is%20The%20Life/Folder.jpg</upnp:artworkURI>
        <upnp:genre">Pop</upnp:genre>
        <upnp:artist role="Performer"">Amy Macdonald</upnp:artist>
        <upnp:artist role="Composer"">Unknown</upnp:artist>
        <upnp:artist role="AlbumArtist"">Unknown</upnp:artist>
        <upnp:artist role="Conductor"">Unknown</upnp:artist>
        <dc:date >Unknown</dc:date>
        <upnp:originalDiscNumber">0</upnp:originalDiscNumber>
        <upnp:originalDiscCount">0</upnp:originalDiscCount>
    </item>

    */
  }
