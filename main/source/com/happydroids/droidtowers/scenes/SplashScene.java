/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HeyZapCheckInButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.SplashCloudLayer;
import com.happydroids.droidtowers.gui.AudioControl;
import com.happydroids.droidtowers.gui.Sunburst;
import com.happydroids.droidtowers.gui.WidgetAccessor;
import com.happydroids.droidtowers.gui.controls.AnimatedHappyDroid;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.AssetLoadProgressPanel;
import com.happydroids.droidtowers.scenes.components.ProgressPanel;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.platform.Platform;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.utils.Scaling.fit;
import static com.badlogic.gdx.utils.Scaling.stretchX;
import static com.happydroids.droidtowers.TowerAssetManager.isLoaded;

public abstract class SplashScene extends Scene {
  public static final int CAMERA_PAN_DOWN_DURATION = 1000;
  private Sprite happyDroid;
  protected SplashCloudLayer cloudLayer;
  private TextureAtlas atlas1;
  private TextureAtlas atlas2;
  protected Image droidTowersLogo;
  protected ProgressPanel progressPanel;
  private Image mainBuilding;
  private boolean createdAudioControls;
  private Group container;
  private boolean createdSplashScene;


  @Override
  public void create(Object... args) {
    makeSkyGradient();
    if (Gdx.graphics.isGL20Available()) {
      makeSunburst();
    }

    container = new Group();
    addActor(container);

    progressPanel = new AssetLoadProgressPanel();
    center(progressPanel);
    addActor(progressPanel);
  }

  private void makeSkyGradient() {
    Texture skyGradient = new Texture("backgrounds/splash-skygradient.png");
    skyGradient.setFilter(Linear, Linear);
    Image skyImage = new Image(new TextureRegionDrawable(new TextureRegion(skyGradient)), Scaling.stretch);
    skyImage.setWidth(getStage().getWidth() * 1.05f);
    skyImage.setHeight(getStage().getHeight() * 1.05f);
    skyImage.setX(-getStage().getWidth() * 0.025f);
    skyImage.setY(-getStage().getHeight() * 0.025f);
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
    container.addActor(cloudLayer);
  }

  private void makeCityScape() {

    Group cityScape = new Group();

    Image cityScapeLeft = new Image(new TextureRegionDrawable(atlas2.findRegion("cityscape-left")), fit, Align.bottom);
    Image cityScapeRight = new Image(new TextureRegionDrawable(atlas2.findRegion("cityscape-right")), fit, Align.bottom);
    Image cityScapeMiddle = new Image(TowerAssetManager.drawable("backgrounds/cityscape-middle.png"), stretchX, Align.bottom);
    cityScapeLeft.setWidth((int) Math.min(getStage().getWidth() * 0.33f, cityScapeLeft.getImageWidth()));
    cityScapeLeft.setHeight((int) Math.min(getStage().getHeight() * 0.33f, cityScapeLeft.getImageHeight()));
    cityScapeLeft.pack();

    cityScapeRight.setWidth((int) Math.min(getStage().getWidth() * 0.33f, cityScapeRight.getImageWidth()));
    cityScapeRight.setHeight((int) Math.min(getStage().getHeight() * 0.33f, cityScapeRight.getImageHeight()));
    cityScapeRight.pack();

    cityScapeRight.setX(getStage().getWidth() - cityScapeRight.getWidth());

    cityScapeMiddle.setWidth((int) getStage().getWidth() * 0.33f);
    cityScapeMiddle.setHeight((int) getStage().getHeight() * 0.33f);
    cityScapeMiddle.setX((int) cityScapeLeft.getWidth());
    cityScapeMiddle.setWidth((int) getStage().getWidth() - cityScapeLeft.getWidth() - cityScapeRight.getWidth());

    cityScape.addActor(cityScapeLeft);
    cityScape.addActor(cityScapeMiddle);
    cityScape.addActor(cityScapeRight);

    container.addActor(cityScape);

    cityScape.setY(-getStage().getHeight());
    Tween start = Tween.to(cityScape, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
                          .delay(50)
                          .target(cityScape.getX(), 0)
                          .ease(TweenEquations.easeInOutExpo)
                          .start(TweenSystem.manager());
  }

  private void makeMainBuilding(boolean animateBuildOut) {
    Image mainBuilding = new Image(new TextureRegionDrawable(atlas1.findRegion("main-building")), fit);
    mainBuilding.setHeight(getStage().getHeight() * 0.85f);
    mainBuilding.layout();
    mainBuilding.setX(getStage().getWidth() / 2 - (mainBuilding.getWidth() / 2));
    mainBuilding.setY(0);
    container.addActor(mainBuilding);

    if (animateBuildOut) {
      mainBuilding.setY(-getStage().getHeight());
      Tween.to(mainBuilding, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
              .delay(50)
              .target(mainBuilding.getX(), 0)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.manager());
    }
  }

  private void makeDroidTowersLogo(boolean animateBuildOut) {
    boolean purchasedUnlimited = Platform.getPurchaseManager().hasPurchasedUnlimitedVersion();
    TextureAtlas.AtlasRegion droidTowersLogoTexture = purchasedUnlimited ? atlas2.findRegion("droid-towers-logo-unlimited") : atlas2.findRegion("droid-towers-logo");
    droidTowersLogo = new Image(new TextureRegionDrawable(droidTowersLogoTexture), fit);
    droidTowersLogo.setWidth(Math.min(getStage().getWidth() * 0.5f, droidTowersLogo.getWidth()));
    droidTowersLogo.layout();
    droidTowersLogo.setY(getStage().getHeight() - droidTowersLogo.getImageHeight() - 75);
    droidTowersLogo.setX(50);

    if (animateBuildOut) {
      droidTowersLogo.setX(-droidTowersLogo.getImageWidth());
      Tween.to(droidTowersLogo, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
              .delay(50)
              .target(50, droidTowersLogo.getY())
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.manager());
    }

    container.addActor(droidTowersLogo);
  }

  private void makeHappyDroid(boolean animateBuildOut) {
    AnimatedHappyDroid happyDroidImage = new AnimatedHappyDroid();
    happyDroidImage.setHeight(getStage().getHeight() * 0.33f);
    happyDroidImage.setPosition(getStage().getWidth() / 2, 0);

    if (animateBuildOut) {
      happyDroidImage.setY(-getStage().getHeight());
      Tween.to(happyDroidImage, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
              .target(happyDroidImage.getX(), 0)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.manager());
    }

    container.addActor(happyDroidImage);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
    createdSplashScene = false;
    container.clear();
  }

  @Override
  public void render(float deltaTime) {
    if (!createdSplashScene) {
      if (isLoaded("backgrounds/splash1.txt") &&
                  isLoaded("backgrounds/splash1.png") &&
                  isLoaded("backgrounds/splash2.txt") &&
                  isLoaded("backgrounds/splash2.png") &&
                  isLoaded("happydroid.txt") &&
                  isLoaded("happydroid.png") &&
                  isLoaded("backgrounds/cityscape-middle.png")) {
        buildSplashScene();
      }
    }


    if (!createdAudioControls &&
                isLoaded("hud/buttons.txt") &&
                isLoaded("hud/heyzap-checkin.txt") &&
                DroidTowersGame.getSoundController() != null) {
      createdAudioControls = true;

      TextureAtlas buttonsAtlas = TowerAssetManager.textureAtlas("hud/buttons.txt");
      AudioControl audioControl = new AudioControl(buttonsAtlas);

      Table c = new Table();
      c.row().space(Display.devicePixel(8));
      if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
        c.add(new HeyZapCheckInButton());
      }
      c.add(audioControl);

      c.pack();
      c.setX(getStage().getWidth() - c.getWidth() - 16);
      c.setY(getStage().getHeight() - c.getHeight() - 32);

      addActor(c);
    }
  }

  private void buildSplashScene() {
    createdSplashScene = true;

    atlas1 = TowerAssetManager.textureAtlas("backgrounds/splash1.txt");
    atlas2 = TowerAssetManager.textureAtlas("backgrounds/splash2.txt");

    makeCloudLayer();
    makeCityScape();
    makeMainBuilding(true);
    makeDroidTowersLogo(true);
    makeHappyDroid(true);
  }

  @Override
  public void dispose() {
  }
}
