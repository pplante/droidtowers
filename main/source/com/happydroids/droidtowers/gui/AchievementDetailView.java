/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.Requirement;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AchievementDetailView extends ScrollableTowerWindow {
  public AchievementDetailView(Achievement achievement, Stage stage, Skin skin) {
    super("Achievements > " + achievement.getName(), stage, skin);

    defaults().top().left().space(6);

    row().height(18);
    add(new NoOpWidget());

    String description = achievement.getDescription();
    if (!StringUtils.isEmpty(description)) {
      addHeaderRow("description");

      row();
      Label descLabel = FontManager.Roboto18.makeLabel(description);
      descLabel.setWrap(true);
      add(descLabel).fill();

      row().height(18);
      add(new NoOpWidget());
    }


    List<Requirement> requirements = achievement.getRequirements();
    if (requirements != null) {
      row().expandX();
      add(new RequirementsTable(requirements)).fill();
    }

    row().fill().space(0);
    add().expand();
  }

  private void addHeaderRow(String headerText) {
    row();
    add(FontManager.Default.makeLabel(headerText, Color.GRAY));

    row();
    add(new HorizontalRule(Color.DARK_GRAY, 1));
  }

}
