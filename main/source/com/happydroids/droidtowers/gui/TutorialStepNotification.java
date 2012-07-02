/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.Strings;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.TutorialStep;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.happydroids.droidtowers.platform.Display.scale;

public class TutorialStepNotification extends Table {
  private boolean allowDismiss;

  public TutorialStepNotification(TutorialStep step) {
    super();

    setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));

    defaults().top().left();

    int padding = scale(10);
    row().pad(padding);
    add(FontManager.RobotoBold18.makeLabel(step.getName()));

    row();
    add(new HorizontalRule());


    Label descLabel = FontManager.Default.makeLabel(Strings.wrap(step.getDescription(), 40));

    row().pad(padding);
    add(descLabel).fillX();

    if (step.getId().equalsIgnoreCase("tutorial-finished")) {
      row().pad(padding);
      add(FontManager.Default.makeLabel("[ tap to dismiss ]"));

      allowDismiss = true;
    }

    pack();
  }

  public void show() {
    HeadsUpDisplay.setTutorialStepNotification(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.OPACITY).target(0.0f))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 200).target(1.0f))
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.manager());
  }

  public void hide() {
    TweenSystem.manager().killTarget(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.SIZE).target(this.width, this.height))
            .beginParallel()
            .push(Tween.to(this, WidgetAccessor.SIZE, 300).target(this.width, 0))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 300).target(0))
            .end()
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                TutorialStepNotification.this.markToRemove(true);
                HeadsUpDisplay.instance().getAchievementButton().visible = true;
                HeadsUpDisplay.instance().getViewNeighborsButton().visible = true;
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.manager());
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    if (allowDismiss) {
      hide();
    }

    return true;
  }
}
