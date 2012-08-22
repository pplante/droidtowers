/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.graphics.ResolutionIndependentAtlas;
import com.happydroids.droidtowers.tween.TweenSystem;

public class ProgressDialog extends Dialog {
  public ProgressDialog() {
    super();

    ResolutionIndependentAtlas resolutionIndependentAtlas = new ResolutionIndependentAtlas(Gdx.files
                                                                                                   .internal("hud/skin.txt"));
    Image progressSpinner = new Image(new TextureRegionDrawable(resolutionIndependentAtlas.findRegion("progress-indeterminate")), Scaling.none);
    progressSpinner.layout();
    progressSpinner.setOriginX(progressSpinner.getImageWidth() / 2);
    progressSpinner.setOriginY(progressSpinner.getImageHeight() / 2);

    Table c = new Table();
    c.add(progressSpinner).fill();

    setView(c);

    Tween.to(progressSpinner, WidgetAccessor.ROTATION, 1000)
            .target(-360.0f)
            .repeat(Tween.INFINITY, 350)
            .start(TweenSystem.manager());
  }
}
