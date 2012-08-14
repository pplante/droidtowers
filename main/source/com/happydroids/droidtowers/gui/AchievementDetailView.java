/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.Requirement;
import com.happydroids.droidtowers.achievements.Reward;
import com.happydroids.droidtowers.platform.Display;
import org.apach3.commons.lang3.StringUtils;

import java.util.List;

public class AchievementDetailView extends ScrollableTowerWindow {
  public AchievementDetailView(Achievement achievement, Stage stage) {
    super(achievement.getName(), stage);

    defaults().top().left().space(Display.devicePixel(6));

    makeDivider();

    String description = achievement.getDescription();
    if (!StringUtils.isEmpty(description)) {
      addHeaderRow("description");

      row();
      Label descLabel = FontManager.Roboto18.makeLabel(description);
      descLabel.setWrap(true);
      add(descLabel).fill();

      makeDivider();
    }

    List<Requirement> requirements = achievement.getRequirements();
    List<Reward> rewards = achievement.getRewards();

    if (requirements != null) {
      row().expandX();
      add(new RequirementsTable(requirements)).fill();
    }

    if (requirements != null && rewards != null) {
      makeDivider();
    }

    if (rewards != null) {
      row().expandX();
      add(new RewardsTable(rewards)).fill();
    }

    shoveContentUp();
  }

  private void makeDivider() {
    row().height(Display.devicePixel(22));
    add(new NoOpWidget());
  }

  private void addHeaderRow(String headerText) {
    row();
    add(FontManager.Default.makeLabel(headerText, Color.GRAY));

    row();
    add(new HorizontalRule(Color.DARK_GRAY, 1));
  }

}
