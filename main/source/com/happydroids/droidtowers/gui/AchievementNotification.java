/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.tween.TweenSystem;

public class AchievementNotification extends Table {

  public AchievementNotification(Achievement achievement) {
    setBackground(TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));

    defaults().top().left().pad(Display.devicePixel(4));

    add(new Image(TowerAssetManager.drawableFromAtlas("achievements-active", "hud/buttons.txt"), Scaling.none)).minWidth(Display.devicePixel(64)).padRight(Display.devicePixel(8));

    Table textTable = new Table();
    textTable.defaults().left().top();
    add(textTable);

    textTable.add(FontManager.Roboto18.makeLabel(achievement.getName())).top();
    textTable.row();
    textTable.add(FontManager.Default.makeLabel(achievement.toRewardString())).top();
    textTable.pack();
    setClip(true);
    pack();

    addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        hide(false);
      }
    });
  }

  public void show() {
    HeadsUpDisplay.getNotificationStack().addActor(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.OPACITY).target(0.0f))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 200).target(1.0f))
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                hide(true);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.manager());
  }

  public void hide(final boolean useDelay) {
    TweenSystem.manager().killTarget(this);

    final WidgetGroup targetParent = (WidgetGroup) AchievementNotification.this.getParent();

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.SIZE).target(this.getWidth(), this.getHeight()).delay(useDelay ? 4000 : 0))
            .beginParallel()
            .push(Tween.to(this, WidgetAccessor.SIZE, 300).target(this.getWidth(), 0))
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
            .start(TweenSystem.manager());
  }
}
