package com.unhappyrobot.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GridObjectChangedEvent;
import com.unhappyrobot.gui.HeadsUpDisplay;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AchievementEngine {
  private static AchievementEngine instance;
  private List<Achievement> achievements;
  private Set<Achievement> completedAchievements;

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
    GameEvents.register(this);
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
      if (achievement.isCompleted() && !achievement.alreadyGaveReward()) {
        String rewardSummary = achievement.giveReward();

        if (rewardSummary != null) {
          summary.add(rewardSummary);
        }

        completedAchievements.add(achievement);

        achievementIterator.remove();
      }
    }

    if (!summary.isEmpty()) {
      String formattedSummary = "";

      for (String s : summary) {
        formattedSummary += "\n" + s;
      }

      HeadsUpDisplay.getInstance().showToast("Awesome Job!\n%s", formattedSummary);
    }
  }

  public Set<Achievement> getCompletedAchievements() {
    return completedAchievements;
  }

  public void loadCompletedAchievements(List<String> completedAchievements) {
    if (completedAchievements == null) {
      return;
    }

    Iterator<Achievement> achievementIterator = achievements.iterator();
    while (achievementIterator.hasNext()) {
      Achievement achievement = achievementIterator.next();
      if (completedAchievements.contains(achievement.getId())) {
        achievement.setCompleted(true);
        achievement.giveReward();
      }
    }
  }
}
