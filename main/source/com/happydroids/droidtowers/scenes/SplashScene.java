/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.SplashCloudLayer;
import com.happydroids.droidtowers.gui.AnimatedImage;
import com.happydroids.droidtowers.gui.AudioControl;
import com.happydroids.droidtowers.gui.Sunburst;
import com.happydroids.droidtowers.gui.WidgetAccessor;
import com.happydroids.droidtowers.scenes.components.AssetLoadProgressPanel;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.utils.Scaling.fit;
import static com.badlogic.gdx.utils.Scaling.stretchX;
import static com.happydroids.droidtowers.TowerAssetManager.assetManager;

public abstract class SplashScene extends Scene {
  public static final int CAMERA_PAN_DOWN_DURATION = 1000;
  private Sprite happyDroid;
  protected SplashCloudLayer cloudLayer;
  private TextureAtlas atlas1;
  private TextureAtlas atlas2;
  protected Image droidTowersLogo;
  protected AssetLoadProgressPanel progressPanel;
  private Image mainBuilding;
  private boolean createdAudioControls;

  @Override
  public void create(Object... args) {
    atlas1 = new TextureAtlas("backgrounds/splash1.txt");
    for (Texture texture : atlas1.getTextures()) {
      texture.setFilter(Linear, Linear);
    }
    atlas2 = new TextureAtlas("backgrounds/splash2.txt");
    for (Texture texture : atlas2.getTextures()) {
      texture.setFilter(Linear, Linear);
    }

    makeSkyGradient();
    makeSunburst();
    makeCloudLayer();
    makeCityScape();
    makeMainBuilding(true);
    makeDroidTowersLogo(true);
    makeHappyDroid(true);

    progressPanel = new AssetLoadProgressPanel();
    center(progressPanel);
    addActor(progressPanel);
  }

  private void makeSkyGradient() {
    Texture skyGradient = new Texture("backgrounds/splash-skygradient.png");
    skyGradient.setFilter(Linear, Linear);
    Image skyImage = new Image(skyGradient, Scaling.stretch);
    skyImage.width = getStage().width();
    skyImage.height = getStage().height();
    skyImage.x = -1;
    skyImage.y = -1;
    addActor(skyImage);
  }

  private void makeSunburst() {
    final Sunburst sunburst = new Sunburst(getStage());

    Tween.to(sunburst, WidgetAccessor.ROTATION, 5000)
            .target(8)
            .repeatYoyo(Tween.INFINITY, 0)
            .start(TweenSystem.manager());

    addActor(sunburst);
  }

  private void makeCloudLayer() {
    cloudLayer = new SplashCloudLayer(getStage(), atlas2.findRegions("cloud"));
    addActor(cloudLayer);
  }

  private void makeCityScape() {
    Group cityScape = new Group();

    Image cityScapeLeft = new Image(atlas2.findRegion("cityscape-left"), fit, Align.BOTTOM | Align.LEFT);
    cityScapeLeft.height = ((int) Math.min(cityScapeLeft.getRegion().getRegionHeight(), getStage().height() * 0.45f));
    cityScapeLeft.layout();

    Image cityScapeRight = new Image(atlas2.findRegion("cityscape-right"), fit, Align.BOTTOM);
    cityScapeRight.height = ((int) Math.min(cityScapeRight.getRegion().getRegionHeight(), getStage().height() * 0.45f));
    cityScapeRight.layout();
    cityScapeRight.x = ((int) (getStage().right() - cityScapeRight.getImageWidth()));

    Image cityScapeMiddle = new Image(atlas1.findRegion("cityscape-middle"), stretchX, Align.BOTTOM);
    cityScapeMiddle.x = ((int) (cityScapeLeft.getImageWidth() + 1));
    cityScapeMiddle.width = ((int) getStage().width() - cityScapeLeft.getImageWidth() - cityScapeRight.getImageWidth() - 2);
    cityScapeMiddle.layout();

    cityScape.addActor(cityScapeLeft);
    cityScape.addActor(cityScapeMiddle);
    cityScape.addActor(cityScapeRight);

    addActor(cityScape);

    cityScape.y = -getStage().height();
    Tween.to(cityScape, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
            .delay(50)
            .target(cityScape.x, 0)
            .ease(TweenEquations.easeInOutExpo)
            .start(TweenSystem.manager());
  }

  private void makeMainBuilding(boolean animateBuildOut) {
    Image mainBuilding = new Image(atlas1.findRegion("main-building"), fit);
    mainBuilding.height = getStage().height() * 0.85f;
    mainBuilding.layout();
    mainBuilding.x = getStage().centerX() - (mainBuilding.width / 2);
    mainBuilding.y = 0;
    addActor(mainBuilding);

    if (animateBuildOut) {
      mainBuilding.y = -getStage().height();
      Tween.to(mainBuilding, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
              .delay(50)
              .target(mainBuilding.x, 0)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.manager());
    }
  }

  private void makeDroidTowersLogo(boolean animateBuildOut) {
    TextureAtlas.AtlasRegion droidTowersLogoTexture = atlas2.findRegion("droid-towers-logo");
    droidTowersLogo = new Image(droidTowersLogoTexture, fit);
    droidTowersLogo.width = Math.min(getStage().width() * 0.5f, droidTowersLogo.getRegion().getRegionWidth());
    droidTowersLogo.layout();
    droidTowersLogo.y = getStage().height() - droidTowersLogo.getImageHeight() - 75;
    droidTowersLogo.x = 50;

    if (animateBuildOut) {
      droidTowersLogo.x = -droidTowersLogo.getImageWidth();
      Tween.to(droidTowersLogo, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
              .delay(50)
              .target(50, droidTowersLogo.y)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.manager());
    }

    addActor(droidTowersLogo);
  }

  private void makeHappyDroid(boolean animateBuildOut) {
    AnimatedImage happyDroidImage = new AnimatedImage(atlas2.findRegions("happy-droid"), 0.05f, true);
    happyDroidImage.setAlign(Align.RIGHT);
    happyDroidImage.setScaling(fit);
    happyDroidImage.delayAfterPlayback(5f);
    happyDroidImage.height = (int) Math.min(getStage().height() * 0.25f, happyDroidImage.getRegion().getRegionHeight());
    happyDroidImage.layout();
    happyDroidImage.x = (int) getStage().centerX();

    if (animateBuildOut) {
      happyDroidImage.y = -getStage().height();
      Tween.to(happyDroidImage, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
              .target(happyDroidImage.x, 0)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.manager());
    }

    addActor(happyDroidImage);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {

  }

  @Override
  public void render(float deltaTime) {
    boolean assetManagerFinished = assetManager().update();
    Thread.yield();

    if (!createdAudioControls && assetManager().isLoaded("hud/buttons.txt", TextureAtlas.class)) {
      createdAudioControls = true;

      AudioControl audioControl = new AudioControl(TowerAssetManager.textureAtlas("hud/buttons.txt"));
      audioControl.x = getStage().right() - audioControl.width - 16;
      audioControl.y = getStage().top() - audioControl.height - 32;
      addActor(audioControl);
    }
  }

  @Override
  public void dispose() {
  }
}
