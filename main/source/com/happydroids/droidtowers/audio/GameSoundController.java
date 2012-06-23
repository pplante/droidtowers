/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;

import java.util.ArrayList;
import java.util.Iterator;

public class GameSoundController {
  private boolean backgroundMusicEnabled;
  public boolean audioState;
  private final Sound constructionSound;
  private Sound destructionSound;
  private Music activeSong;
  private final Iterator<FileHandle> availableSongs;
  private static boolean soundsAllowed;

  public GameSoundController() {
    audioState = TowerGameService.instance().getAudioState();

    constructionSound = Gdx.audio.newSound(Gdx.files.internal("sound/effects/construction-placement-1.wav"));
    destructionSound = Gdx.audio.newSound(Gdx.files.internal("sound/effects/construction-destroy-1.wav"));

    ArrayList<FileHandle> songs = Lists.newArrayList(Gdx.files.internal("sound/music/").list(".mp3"));
    availableSongs = Iterables.cycle(songs).iterator();

    moveToNextSong();
  }

  private void moveToNextSong() {
    if (activeSong != null) {
      activeSong.dispose();
      activeSong = null;
    }

    if (availableSongs.hasNext()) {
      activeSong = Gdx.audio.newMusic(availableSongs.next());
      activeSong.setVolume(0.5f);

      if (audioState) {
        activeSong.play();
      }
    }
  }

  public static void setSoundsAllowed(boolean soundsAllowed) {
    GameSoundController.soundsAllowed = soundsAllowed;
  }

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
    if (!audioState || !soundsAllowed) return;

    constructionSound.play();
  }

  @Subscribe
  public void GameGrid_onGridObjectRemoved(GridObjectRemovedEvent event) {
    if (!audioState || !soundsAllowed) return;

    if (event.gridObject.isPlaced()) {
      destructionSound.play();
    }
  }

  public void toggleAudio() {
    audioState = !audioState;

    if (audioState) {
      moveToNextSong();
    } else {
      activeSong.dispose();
    }

    TowerGameService.instance().setAudioState(audioState);
  }

  public void update(float deltaTime) {
    if (audioState && activeSong != null && !activeSong.isPlaying()) {
      moveToNextSong();
    }
  }

  public boolean isAudioState() {
    return audioState;
  }
}
