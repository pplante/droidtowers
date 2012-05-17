/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;

import static com.happydroids.droidtowers.platform.Display.scale;


public class AchievementListView extends ScrollableTowerWindow {
  private NinePatch itemSelectBackground;

  public AchievementListView(Stage stage, Skin skin) {
    super("Achievements", stage);

    itemSelectBackground = TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.ICS_BLUE);

    defaults();

    for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
      makeItem(achievement);
    }

    shoveContentUp();
  }

  public void makeItem(final Achievement achievement) {
    row().expandX();
    AchievementListViewItem actor = new AchievementListViewItem(achievement);
    actor.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(final Actor actor, float x, float y) {
        if (achievement.isCompleted() && !achievement.hasGivenReward()) {
          dismiss();
          achievement.giveReward();
          new AchievementNotification(achievement).show();
        } else {
          new AchievementDetailView(achievement, stage).show();
        }
      }
    });
    add(actor).fill();
  }

  private class AchievementListViewItem extends Table {
    public AchievementListViewItem(Achievement achievement) {

      row().pad(scale(16), scale(8), scale(16), scale(8));
      add(FontManager.Roboto18.makeLabel(achievement.getName())).expandX().left();

      Actor actor;
      if (achievement.isCompleted() && !achievement.hasGivenReward()) {
        actor = FontManager.Roboto18.makeLabel("Tap to Complete!");
      } else {
        actor = new ProgressBar(achievement.getPercentComplete());
      }
      add(actor).width(scale(200));

      Image arrowImg = new Image(TowerAssetManager.textureFromAtlas("right-arrow", "hud/menus.txt"), Scaling.fit);
      add(arrowImg).width((int) arrowImg.width);

      row();
      add(new HorizontalRule(Colors.DARK_GRAY, 1)).colspan(3);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer) {
      action(Delay.$(0.125f).setCompletionListener(new OnActionCompleted() {
        public void completed(Action action) {
          setBackground(itemSelectBackground);
        }
      }));

      return super.touchDown(x, y, pointer);
    }

    @Override
    public void touchDragged(float x, float y, int pointer) {
      clearActions();
      setBackground(null);

      super.touchDragged(x, y, pointer);
    }

    @Override
    public void touchUp(float x, float y, int pointer) {
      clearActions();
      setBackground(null);

      super.touchUp(x, y, pointer);
    }

    private void setChildrenColor(Color color) {
      for (Actor child : children) {
        if (child instanceof Label) {
          ((Label) child).setColor(color);
        }
      }
    }
  }
}
