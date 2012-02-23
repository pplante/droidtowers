package com.unhappyrobot.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class AchievementEngine {
  private static AchievementEngine instance;
  private List<Achievement> achievements;

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
  }

  public List<Achievement> getAchievements() {
    return achievements;
  }
}
