/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.gamestate.server.RunnableQueue;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;

import java.util.Iterator;

public class GameSoundController {
  private static final String TAG = GameSoundController.class.getSimpleName();

  public static final String CONSTRUCTION_PLACEMENT = "sound/effects/construction-placement-1.wav";
  public static final String CONSTRUCTION_DESTROY = "sound/effects/construction-destroy-1.wav";

  private static RunnableQueue afterInitRunnables = new RunnableQueue();
  private boolean backgroundMusicEnabled;
  public boolean audioState;
  private Sound constructionSound;
  private Sound destructionSound;
  private Music activeSong;
  private final Iterator<String> availableSongs;


  public GameSoundController() {
    audioState = TowerGameService.instance().getAudioState();

    availableSongs = Iterables.cycle(TowerAssetManager.getAssetList().musicFiles).iterator();

    moveToNextSong();

    constructionSound = Gdx.audio.newSound(Gdx.files.internal(CONSTRUCTION_PLACEMENT));
    destructionSound = Gdx.audio.newSound(Gdx.files.internal(CONSTRUCTION_DESTROY));

    if (afterInitRunnables != null) {
      afterInitRunnables.runAll();
      afterInitRunnables = null;
    }
  }

  private void moveToNextSong() {
    if (activeSong != null) {
      activeSong.dispose();
      activeSong = null;
    }

    if (availableSongs.hasNext()) {
      String songFilename = availableSongs.next();
      Gdx.app.debug(TAG, "Now playing: " + songFilename);
      activeSong = Gdx.audio.newMusic(Gdx.files.internal(songFilename));
      activeSong.setVolume(0.5f);

      if (audioState) {
        activeSong.play();
      }
    }
  }

  private void playSound(Sound sound) {
    if (!audioState || sound == null) {
      return;
    }

    sound.play();
  }

  public void toggleAudio() {
    audioState = !audioState;

    if (activeSong != null) {
      activeSong.dispose();
    }

    if (audioState) {
      moveToNextSong();
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

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
    playSound(constructionSound);
  }

  @Subscribe
  public void GameGrid_onGridObjectRemoved(GridObjectRemovedEvent event) {
    playSound(destructionSound);
  }

  public static void runAfterInit(Runnable runnable) {
    if (afterInitRunnables != null) {
      afterInitRunnables.push(runnable);
    } else {
      runnable.run();
    }
  }
}
