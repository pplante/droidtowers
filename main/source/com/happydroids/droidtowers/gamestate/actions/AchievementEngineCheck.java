/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.badlogic.gdx.Gdx;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.events.ElevatorHeightChangeEvent;
import com.happydroids.droidtowers.events.GridObjectEvent;
import com.happydroids.droidtowers.grid.GameGrid;

public class AchievementEngineCheck extends GameGridAction {
  private static final String TAG = AchievementEngineCheck.class.getSimpleName();

  public AchievementEngineCheck(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);

    gameGrid.events().register(this);
  }

  @Override
  public void run() {
    AchievementEngine.instance().checkAchievements(gameGrid);
    TutorialEngine.instance().checkAchievements(gameGrid);
  }

  @Subscribe
  public void GameEvent_handleGridObjectEvent(GridObjectEvent event) {
    if (event instanceof ElevatorHeightChangeEvent) {
      TutorialEngine.instance().moveToStepWhenReady("tutorial-build-pizza-place");
    } else if (event.getGridObject().isPlaced()) {
      Gdx.app.debug(TAG, "GameEvent_handleGridObjectEvent triggered by: " + event);
      scheduleToRunIn(0.25f);
    }
  }
}
