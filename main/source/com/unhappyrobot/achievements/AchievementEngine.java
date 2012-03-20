package com.unhappyrobot.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.events.GridObjectChangedEvent;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.gui.AchievementNotification;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AchievementEngine {
  private static AchievementEngine instance;
  private List<Achievement> achievements;
  private Set<Achievement> completedAchievements;
  private GameGrid gameGrid;

  public static AchievementEngine instance() {
    if (instance == null) {
      instance = new AchievementEngine();
    }

    return instance;
  }

  private AchievementEngine() {
    try {
      FileHandle fileHandle = Gdx.files.internal("params/achievements.json");
      ObjectMapper mapper = new ObjectMapper();
      achievements = mapper.readValue(fileHandle.reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Achievement.class));
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    completedAchievements = Sets.newHashSet();
  }

  public List<Achievement> getAchievements() {
    return achievements;
  }

  @Subscribe
  public void GameEvent_handleGridObjectEvent(GridObjectChangedEvent event) {
    if (!event.nameOfParamChanged.equals("placementState")) {
      return;
    }

    List<String> summary = Lists.newArrayList();

    Iterator<Achievement> achievementIterator = achievements.iterator();
    while (achievementIterator.hasNext()) {
      Achievement achievement = achievementIterator.next();
      if (achievement.isCompleted() && !completedAchievements.contains(achievement)) {
        String rewardSummary = achievement.giveReward();

        if (rewardSummary != null) {
          summary.add(rewardSummary);
        }

        AchievementNotification notification = new AchievementNotification(achievement);
        notification.show();

        completedAchievements.add(achievement);

        achievementIterator.remove();
      }
    }
  }

  public Set<Achievement> getCompletedAchievements() {
    return completedAchievements;
  }

  public void loadCompletedAchievements(List<String> achievementIds) {
    if (achievementIds == null) {
      return;
    }

    Iterator<Achievement> achievementIterator = achievements.iterator();
    while (achievementIterator.hasNext()) {
      Achievement achievement = achievementIterator.next();
      if (achievementIds.contains(achievement.getId())) {
        achievement.setCompleted(true);
        achievement.giveReward();
        completedAchievements.add(achievement);
      }
    }
  }

  public void add(Achievement achievement) {
    achievements.add(achievement);
  }

  public boolean hasGameGrid() {
    return gameGrid != null;
  }

  public void unregisterGameGrid() {
    if (gameGrid != null) {
      gameGrid.events().unregister(AchievementEngine.instance());
    }

    gameGrid = null;
  }

  public void registerGameGrid(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    if (gameGrid != null) {
      gameGrid.events().register(AchievementEngine.instance());
    }
  }
}
