/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.GridObjectPlacementState;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.grid.GameGrid;

public class GameGridSoundDispatcher {
  private final Sound constructionSound;
  private Sound destructionSound;
  private GameGrid gameGrid;

  public GameGridSoundDispatcher() {
    constructionSound = Gdx.audio.newSound(Gdx.files.internal("sound/effects/construction-placement-1.wav"));
    destructionSound = Gdx.audio.newSound(Gdx.files.internal("sound/effects/construction-destroy-1.wav"));
  }

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
    if (!TowerGame.isAudioEnabled()) return;

    constructionSound.play();
  }

  @Subscribe
  public void GameGrid_onGridObjectRemoved(GridObjectRemovedEvent event) {
    if (!TowerGame.isAudioEnabled()) return;

    if (event.gridObject.getPlacementState().equals(GridObjectPlacementState.PLACED)) {
      destructionSound.play();
    }
  }

  public void setGameGrid(GameGrid gameGrid_) {
    if (gameGrid != null) {
      try {
        gameGrid.events().unregister(this);
      } catch (IllegalArgumentException ignored) {
      } finally {
        gameGrid = null;
      }
    }

    gameGrid = gameGrid_;

    if (gameGrid != null) {
      gameGrid.events().register(this);
    }
  }
}
