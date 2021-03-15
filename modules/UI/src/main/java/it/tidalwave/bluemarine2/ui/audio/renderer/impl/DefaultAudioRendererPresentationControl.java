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
package it.tidalwave.bluemarine2.ui.audio.renderer.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Duration;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.messagebus.annotation.ListensTo;
import it.tidalwave.messagebus.annotation.SimpleMessageSubscriber;
import it.tidalwave.bluemarine2.model.audio.AudioFile;
import it.tidalwave.bluemarine2.model.MediaItem.Metadata;
import it.tidalwave.bluemarine2.model.PlayList;
import it.tidalwave.bluemarine2.ui.commons.RenderAudioFileRequest;
import it.tidalwave.bluemarine2.ui.commons.OnDeactivate;
import it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer;
import it.tidalwave.bluemarine2.ui.audio.renderer.AudioRendererPresentation;
import it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer.Status;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.Displayable.Displayable;
import static it.tidalwave.bluemarine2.util.Formatters.format;
import static it.tidalwave.bluemarine2.ui.audio.renderer.MediaPlayer.Status.*;
import static it.tidalwave.bluemarine2.model.MediaItem.Metadata.*;
import static it.tidalwave.util.PropertyWrapper.wrap;

/***********************************************************************************************************************
 *
 * The Control of the {@link AudioRendererPresentation}.
 *
 * @stereotype  Control
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SimpleMessageSubscriber @Slf4j
public class DefaultAudioRendererPresentationControl
  {
    @Inject
    private AudioRendererPresentation presentation;

    @Inject
    private MediaPlayer mediaPlayer;

    private final AudioRendererPresentation.Properties properties = new AudioRendererPresentation.Properties();

    private Duration duration = Duration.ZERO;

    private PlayList<AudioFile> playList = PlayList.empty();

    // Discriminates a forced stop from media player just terminating
    private boolean stopped;

    private final UserAction prevAction = UserAction.of(() -> changeTrack(playList.previous().get()));

    private final UserAction nextAction = UserAction.of(() -> changeTrack(playList.next().get()));

    private final UserAction rewindAction = UserAction.of(() -> mediaPlayer.rewind());

    private final UserAction fastForwardAction = UserAction.of(() -> mediaPlayer.fastForward());

    private final UserAction pauseAction = UserAction.of(() -> mediaPlayer.pause());

    private final UserAction playAction = UserAction.of(this::play);

    private final UserAction stopAction = UserAction.of(this::stop);

    // FIXME: use expression binding
    // e.g.  properties.progressProperty().bind(mediaPlayer.playTimeProperty().asDuration().dividedBy/duration));
    // FIXME: weak, remove previous listeners
    private final ChangeListener<Duration> l =
                (ObservableValue<? extends Duration> observable,
                 Duration oldValue,
                 Duration newValue) ->
        {
          // FIXME: the control shouldn't mess with JavaFX stuff
          Platform.runLater(() ->
            {
              properties.playTimeProperty().setValue(format(newValue));
              properties.progressProperty().setValue((double)newValue.toMillis() / duration.toMillis());
            });
        };

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    /* VisibleForTesting */ void initialize()
      {
        presentation.bind(properties,
                          prevAction, rewindAction, stopAction, pauseAction, playAction, fastForwardAction, nextAction);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void onRenderAudioFileRequest (final @ListensTo @Nonnull RenderAudioFileRequest request)
      throws MediaPlayer.Exception
      {
        log.info("onRenderAudioFileRequest({})", request);

        playList = request.getPlayList();
        setAudioFile(playList.getCurrentItem().get());
        bindMediaPlayer();
        presentation.showUp(this);
        presentation.focusOnPlayButton();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @OnDeactivate
    /* VisibleForTesting */ OnDeactivate.Result onDeactivate()
      throws MediaPlayer.Exception
      {
        stop();
        unbindMediaPlayer();
        playList = PlayList.empty();
        return OnDeactivate.Result.PROCEED;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void setAudioFile (final @Nonnull AudioFile audioFile)
      throws MediaPlayer.Exception
      {
        log.info("setAudioFile({})", audioFile);
        final Metadata metadata = audioFile.getMetadata();
        log.info(">>>> metadata:  {}", metadata);

        // FIXME: the control shouldn't mess with JavaFX stuff
        // FIXME: this performs some (short) queries that are executed in the JavaFX thread
        Platform.runLater(() ->
          {
            properties.titleProperty().setValue(metadata.get(TITLE).orElse(""));
            properties.artistProperty().setValue(audioFile.findMakers().stream()
                    .map(maker -> maker.as(Displayable).getDisplayName())
                    .collect(Collectors.joining(", ")));
            properties.composerProperty().setValue(audioFile.findComposers().stream()
                    .map(composer -> composer.as(Displayable).getDisplayName())
                    .collect(Collectors.joining(", ")));
            duration = metadata.get(DURATION).orElse(Duration.ZERO);
            properties.durationProperty().setValue(format(duration));
            properties.folderNameProperty().setValue(
                    audioFile.getRecord().map(record -> record.as(Displayable).getDisplayName()).orElse(""));
            properties.nextTrackProperty().setValue(
                    ((playList.getSize() == 1) ? "" : String.format("%d / %d", playList.getIndex() + 1, playList.getSize()) +
                    playList.peekNext().map(t -> " - Next track: " + t.getMetadata().get(TITLE).orElse("")).orElse("")));
          });

        mediaPlayer.setMediaItem(audioFile);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void onMediaPlayerStarted()
      {
        log.info("onMediaPlayerStarted()");
//        presentation.focusOnStopButton();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void onMediaPlayerStopped()
      {
        log.info("onMediaPlayerStopped()");

        if (!stopped)
          {
            presentation.focusOnPlayButton();
          }

        if (!stopped && playList.hasNext())
          {
            // FIXME: check whether the disk is not gapless, and eventually pause
            try
              {
                setAudioFile(playList.next().get());
                play();
              }
            catch (MediaPlayer.Exception e)
              {
                log.error("", e);
              }
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void play()
      throws MediaPlayer.Exception
      {
        stopped = false;
        mediaPlayer.play();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void stop()
      throws MediaPlayer.Exception
      {
        stopped = true;
        mediaPlayer.stop();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void changeTrack (final @Nonnull AudioFile audioFile)
      throws MediaPlayer.Exception
      {
        final boolean wasPlaying = mediaPlayer.statusProperty().get().equals(PLAYING);

        if (wasPlaying)
          {
            stop();
          }

        setAudioFile(audioFile);

        if (wasPlaying)
          {
            play();
          }
      }

    /*******************************************************************************************************************
     *
     * Binds to the {@link MediaPlayer}.
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void bindMediaPlayer()
      {
        log.debug("bindMediaPlayer()");
        final ObjectProperty<Status> status = mediaPlayer.statusProperty();
        wrap(stopAction.enabled()).bind(status.isEqualTo(PLAYING));
        wrap(pauseAction.enabled()).bind(status.isEqualTo(PLAYING));
        wrap(playAction.enabled()).bind(status.isNotEqualTo(PLAYING));
        wrap(prevAction.enabled()).bind(playList.hasPreviousProperty());
        wrap(nextAction.enabled()).bind(playList.hasNextProperty());
        mediaPlayer.playTimeProperty().addListener(l);

        status.addListener((observable, oldValue, newValue) ->
          {
            switch (newValue)
              {
                case STOPPED:
                    onMediaPlayerStopped();
                    break;

                case PLAYING:
                    onMediaPlayerStarted();
                    break;
              }
          });
      }

    /*******************************************************************************************************************
     *
     * Unbinds from the {@link MediaPlayer}.
     *
     ******************************************************************************************************************/
    /* VisibleForTesting */ void unbindMediaPlayer()
      {
        log.debug("unbindMediaPlayer()");
        wrap(stopAction.enabled()).unbind();
        wrap(pauseAction.enabled()).unbind();
        wrap(playAction.enabled()).unbind();
        wrap(prevAction.enabled()).unbind();
        wrap(nextAction.enabled()).unbind();
        mediaPlayer.playTimeProperty().removeListener(l);
      }
  }
