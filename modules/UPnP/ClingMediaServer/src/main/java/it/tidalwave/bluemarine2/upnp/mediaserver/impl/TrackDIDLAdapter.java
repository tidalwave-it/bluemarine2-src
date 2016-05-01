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

import it.tidalwave.bluemarine2.model.AudioFile;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.MusicTrack;
import it.tidalwave.role.Displayable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.bluemarine2.model.Track;
import static it.tidalwave.bluemarine2.model.role.AudioFileSupplier.AudioFileSupplier;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.role.Identifiable.Identifiable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;

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

        final ProtocolInfo protocolInfo = new DLNAProtocolInfo(Protocol.HTTP_GET, "*", "audio/mp3", "*"); // FIXME: MIME
        final AudioFile audioFile = datum.as(AudioFileSupplier).getAudioFile();

        final long size = Files.size(audioFile.getPath()); // FIXME: should be in metadata
        final Res resource = new Res(protocolInfo, size, resourceServer.urlForResource(audioFile));
        resource.setDuration("" + datum.getDuration().getSeconds());
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
//        datum.getTrackNumber();
        item.setRestricted(false);

        // TODO     <desc id="cdudn" nameSpace="urn:schemas-rinconnetworks-com:metadata-1-0/">SA_RINCON5127_42????35</desc>
        return item;
      }
  }
