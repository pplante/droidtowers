/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.platform.Display;

class AchievementListViewItem extends Table {
  private AchievementListView achievementListView;

  public AchievementListViewItem(AchievementListView achievementListView, Achievement achievement, final Drawable itemSelectBackground) {
    this.achievementListView = achievementListView;

    row().pad(Display.devicePixel(16), Display.devicePixel(8), Display.devicePixel(16), Display.devicePixel(8)).fillX();
    add(FontManager.Roboto18.makeLabel(achievement.getName())).expandX().left();

    Actor actor;
    if (achievement.isCompleted()) {
      if (achievement.hasGivenReward()) {
        actor = FontManager.Roboto18.makeLabel("Completed!");
      } else {
        actor = FontManager.Roboto18.makeLabel("Tap to Complete!");
      }
    } else if (achievement.isLocked()) {
      actor = FontManager.Roboto18.makeLabel("Locked.");
    } else {
      actor = new ProgressBar(achievement.getPercentComplete());
    }
    add(actor).width(Display.devicePixel(200));

    Image arrowImg = new Image(TowerAssetManager.drawableFromAtlas("right-arrow", "hud/menus.txt"), Scaling.fit);
    add(arrowImg).width((int) arrowImg.getWidth());

    row().fillX();
    add(new HorizontalRule(Color.DARK_GRAY, 1)).expandX().colspan(3);

    addListener(new EventListener() {
      @Override
      public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) {
          return false;
        }
        InputEvent event = (InputEvent) e;

        if (event.getType().equals(InputEvent.Type.touchDown)) {
          addAction(Actions.sequence(Actions.delay(0.125f), Actions.run(new Runnable() {
            @Override
            public void run() {
              setBackground(itemSelectBackground);
            }
          })));
        } else {
          clearActions();
          setBackground((Drawable) null);
        }

        return false;
      }
    });
  }

  private void setChildrenColor(Color color) {
    for (Actor child : getChildren()) {
      if (child instanceof Label) {
        child.setColor(color);
      }
    }
  }
}
