/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.TutorialStep;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.tween.TweenSystem;

public class TutorialStepNotification extends Table {
  private boolean allowDismiss;
  private final TutorialStep tutorialStep;

  public TutorialStepNotification(final TutorialStep tutorialStep) {
    super();
    this.tutorialStep = tutorialStep;

    setBackground(TowerAssetManager.ninePatchDrawable("hud/dialog-bg.png", Color.WHITE, 1, 1, 1, 1));

    pad(Display.devicePixel(8));
    defaults().top().left().space(Display.devicePixel(6));

    row();
    add(FontManager.Default.makeLabel(tutorialStep.getName().toUpperCase(), Colors.ICS_BLUE));

    row();
    add(new HorizontalRule()).fillX();

    Label descLabel = FontManager.Default.makeLabel(tutorialStep.getDescription());
    row();
    add(descLabel);


    if (tutorialStep.requiresTapToGiveReward()) {
      final boolean isTutorialCompleteStep = tutorialStep.getId().equals("tutorial-finished");

      TextButton tapToDismissButton = FontManager.Default
                                              .makeTextButton(isTutorialCompleteStep ? "tap to dismiss" : "continue");
      tapToDismissButton.addListener(new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          tutorialStep.giveReward();

          if (isTutorialCompleteStep) {
            hide();
          }
        }
      });

      row();
      add(tapToDismissButton).center();
    }
  }

  public void show() {
    HeadsUpDisplay.instance().setTutorialStepNotification(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.OPACITY).target(0.0f))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 200).target(1.0f))
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.manager());
  }

  public void hide() {
    TweenSystem.manager().killTarget(this);

    Timeline.createSequence()
            .push(Tween.set(this, WidgetAccessor.SIZE).target(this.getWidth(), this.getHeight()))
            .beginParallel()
            .push(Tween.to(this, WidgetAccessor.SIZE, 300).target(this.getWidth(), 0))
            .push(Tween.to(this, WidgetAccessor.OPACITY, 300).target(0))
            .end()
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                TutorialStepNotification.this.remove();
                HeadsUpDisplay.instance().getAchievementButton().setVisible(true);
                HeadsUpDisplay.instance().toggleViewNeighborsButton(true);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.manager());
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    super.drawBackground(batch, parentAlpha);
  }
}
