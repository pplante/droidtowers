/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;

import static com.happydroids.droidtowers.ColorUtil.rgba;
import static com.happydroids.droidtowers.platform.Display.scale;


public class AchievementViewer extends ScrollableTowerWindow {
  public AchievementViewer(Stage stage, Skin skin) {
    super("Achievements", stage, skin);

    defaults();

    for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
      makeItem(achievement);
    }

    for (Achievement achievement : TutorialEngine.instance().getAchievements()) {
      makeItem(achievement);
    }
  }

  public void makeItem(Achievement achievement) {
    row().expand();
    add(new AchievementViewerItem(achievement)).fill();
  }

  private static class AchievementViewerItem extends Table {
    private static final Color DARKER_GRAY = rgba("#393939");

    public AchievementViewerItem(Achievement achievement) {
      row().padTop(15).padBottom(15);
      add(FontManager.Roboto18.makeLabel(achievement.getName())).expandX().left();
      ProgressBar progressBar = new ProgressBar();
      progressBar.setValue(achievement.getPercentComplete());
      add(progressBar).width(200);


      Image arrowImg = new Image(TowerAssetManager.textureFromAtlas("right-arrow", "hud/menus.txt"), Scaling.none);
      add(arrowImg).width(scale(arrowImg.width * 2)).right();

      row();
      add(new HorizontalRule(DARKER_GRAY, 1)).expandX().colspan(3);
    }
  }
}
