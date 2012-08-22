/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.events.AchievementCompletionEvent;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.TutorialStepNotification;

import java.io.IOException;
import java.util.ArrayList;

public class TutorialEngine extends AchievementEngine {
  private static TutorialEngine instance;
  private boolean enabled;

  public static TutorialEngine instance() {
    if (instance == null) {
      instance = new TutorialEngine();
    }

    return instance;
  }

  protected TutorialEngine() {
    try {
      ObjectMapper mapper = TowerGameService.instance().getObjectMapper();
      achievements = mapper.readValue(Gdx.files.internal("params/tutorial-steps.json").reader(), mapper.getTypeFactory()
                                                                                                         .constructCollectionType(ArrayList.class, TutorialStep.class));
      achievementsById = Maps.newHashMap();

      for (int i = 0, achievementsSize = achievements.size(); i < achievementsSize; i++) {
        Achievement achievement = achievements.get(i);
        achievementsById.put(achievement.getId(), achievement);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void checkAchievements(GameGrid gameGrid) {
    if (enabled) {
      super.checkAchievements(gameGrid);
    }
  }

  public void moveToStepWhenReady(String stepId) {
    complete(stepId);
  }

  @Override
  protected void complete(Achievement achievement) {
    TutorialStep tutorialStep = (TutorialStep) achievement;

    if (tutorialStep.isLocked() || tutorialStep.hasGivenReward() || tutorialStep.hasShownNotification()) {
      return;
    }

    tutorialStep.setCompleted(true);

    if (!tutorialStep.requiresTapToGiveReward()) {
      tutorialStep.giveReward();
    }

    if (enabled) {
      tutorialStep.shownNotification();
      new TutorialStepNotification(tutorialStep).show();

      AchievementCompletionEvent event = Pools.obtain(AchievementCompletionEvent.class);
      event.setAchievement(tutorialStep);
      eventBus.post(event);
      Pools.free(event);
    }
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;

    resetState();
  }
}
