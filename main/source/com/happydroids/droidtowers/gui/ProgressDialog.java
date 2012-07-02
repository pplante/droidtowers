/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.happydroids.droidtowers.graphics.ResolutionIndependentAtlas;
import com.happydroids.droidtowers.tween.TweenSystem;

public class ProgressDialog extends Dialog {
  public ProgressDialog() {
    super();

    ResolutionIndependentAtlas resolutionIndependentAtlas = new ResolutionIndependentAtlas(Gdx.files.internal("hud/skin.txt"));
    Image progressSpinner = new Image(resolutionIndependentAtlas.findRegion("progress-indeterminate"));
    progressSpinner.layout();
    progressSpinner.originX = progressSpinner.getImageWidth() / 2;
    progressSpinner.originY = progressSpinner.getImageHeight() / 2;
    setView(progressSpinner);

    Tween.to(progressSpinner, WidgetAccessor.ROTATION, 1000)
            .target(-360.0f)
            .repeat(Tween.INFINITY, 350)
            .start(TweenSystem.manager());
  }
}
