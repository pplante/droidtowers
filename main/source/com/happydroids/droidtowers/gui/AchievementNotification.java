/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.tween.TweenSystem;

public class AchievementNotification extends Table {
  private final Achievement achievement;

  public AchievementNotification(Achievement achievement) {
    this.achievement = achievement;

    setBackground(HeadsUpDisplay.instance().getGuiSkin().getPatch("default-round"));

    defaults().top().left().pad(4);

    add(new Image(new Texture(Gdx.files.internal("hud/trophy.png")), Scaling.none)).minWidth(64).padRight(8);

    Table textTable = new Table();
    textTable.defaults().left().top();
    add(textTable);

    textTable.add(new Label(achievement.getName(), HeadsUpDisplay.instance().getGuiSkin())).top();
    textTable.row();
    textTable.add(new Label(achievement.toRewardString(), HeadsUpDisplay.instance().getGuiSkin())).top();
    textTable.pack();
    setClip(true);
    pack();
  }

  public void show() {
    HeadsUpDisplay.instance().getNotificationStack().addActor(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.OPACITY).target(0.0f))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 200).target(1.0f))
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                hide(true);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.getTweenManager());
  }

  public void hide(final boolean useDelay) {
    TweenSystem.getTweenManager().killTarget(this);

    final WidgetGroup targetParent = (WidgetGroup) AchievementNotification.this.parent;

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.SIZE).target(this.width, this.height).delay(useDelay ? 4000 : 0))
            .beginParallel()
            .push(Tween.to(this, WidgetAccessor.SIZE, 300).target(this.width, 0))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 300).target(0))
            .end()
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                if (targetParent != null) {
                  targetParent.removeActor(AchievementNotification.this);
                  targetParent.invalidate();
                  targetParent.layout();
                }
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.getTweenManager());
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    hide(false);
    return true;
  }
}
