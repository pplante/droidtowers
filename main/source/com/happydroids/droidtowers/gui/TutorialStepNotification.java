/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.achievements.TutorialStep;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.happydroids.droidtowers.platform.Display.scale;

public class TutorialStepNotification extends Table {
  private boolean allowDismiss;

  public TutorialStepNotification(TutorialStep step) {
    setBackground(HeadsUpDisplay.instance().getGuiSkin().getPatch("default-round"));

    defaults();

    row();
    add(FontManager.RobotoBold18.makeLabel(step.getName())).top();
    row().pad(scale(6));
    add(new HorizontalRule());
    row().pad(scale(6));
    add(FontManager.Default.makeLabel(step.getDescription())).top();

    if (step.getId().equalsIgnoreCase("tutorial-finished")) {
      row().pad(scale(6));
      add(FontManager.Default.makeLabel("[ tap to dismiss ]"));

      allowDismiss = true;
    }

    pack();
  }

  public void show() {
    HeadsUpDisplay.instance().setTutorialStep(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.OPACITY).target(0.0f))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 200).target(1.0f))
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.getTweenManager());
  }

  public void hide() {
    TweenSystem.getTweenManager().killTarget(this);

    final WidgetGroup targetParent = (WidgetGroup) this.parent;

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.SIZE).target(this.width, this.height))
            .beginParallel()
            .push(Tween.to(this, WidgetAccessor.SIZE, 300).target(this.width, 0))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 300).target(0))
            .end()
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                if (targetParent != null) {
                  targetParent.removeActor(TutorialStepNotification.this);
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
    if (allowDismiss) {
      hide();
    }

    return true;
  }
}
