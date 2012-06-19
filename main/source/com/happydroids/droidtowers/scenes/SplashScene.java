/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.actions.RotateBy;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.SplashCloudLayer;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gui.AnimatedImage;
import com.happydroids.droidtowers.gui.Sunburst;
import com.happydroids.droidtowers.gui.WidgetAccessor;
import com.happydroids.droidtowers.scenes.components.AssetLoadProgressPanel;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.utils.Scaling.fit;
import static com.happydroids.droidtowers.SplashSceneStates.FULL_LOAD;
import static com.happydroids.droidtowers.SplashSceneStates.PRELOAD_ONLY;
import static com.happydroids.droidtowers.TowerAssetManager.assetManager;

public class SplashScene extends Scene {
  private Sprite happyDroid;
  private SplashSceneStates splashState = PRELOAD_ONLY;
  private GameSave gameSave;
  private Runnable postLoadRunnable;
  protected SplashCloudLayer cloudLayer;
  private TextureAtlas mainAtlas;
  protected Image droidTowersLogo;

  @Override
  public void create(Object... args) {
    if (args != null) {
      splashState = ((SplashSceneStates) args[0]);

      if (args.length > 1) {
        Object firstArg = args[1];
        if (firstArg instanceof GameSave) {
          gameSave = ((GameSave) firstArg);
          postLoadRunnable = new Runnable() {
            public void run() {
              TowerGame.changeScene(TowerScene.class, gameSave);
            }
          };
        } else if (firstArg instanceof Runnable) {
          postLoadRunnable = (Runnable) firstArg;
        }
      }
    }

    buildSplashScene(true);

    AssetLoadProgressPanel progressPanel = new AssetLoadProgressPanel();
    center(progressPanel);
    addActor(progressPanel);
  }

  protected void buildSplashScene(boolean animateBuildOut) {
    mainAtlas = new TextureAtlas("backgrounds/splash.txt");
    for (Texture texture : mainAtlas.getTextures()) {
      texture.setFilter(Linear, Linear);
    }

    makeSkyGradient();
    makeSunburst();
    makeCloudLayer();
    makeMainBuilding(animateBuildOut);
    makeDroidTowersLogo(animateBuildOut);
    makeHappyDroid(animateBuildOut);
  }

  private void makeSkyGradient() {
    TextureAtlas.AtlasRegion skyGradientTexture = mainAtlas.findRegion("sky-gradient");
    Image skyImage = new Image(skyGradientTexture, Scaling.fill);
    skyImage.width = Gdx.graphics.getWidth();
    skyImage.height = Gdx.graphics.getHeight();
    addActor(skyImage);
  }

  private void makeCloudLayer() {
    cloudLayer = new SplashCloudLayer(getStage(), mainAtlas.findRegions("cloud-"));
    addActor(cloudLayer);
  }

  private void makeMainBuilding(boolean animateBuildOut) {
    Image mainBuilding = new Image(mainAtlas.findRegion("main-building"), fit);
    mainBuilding.height = getStage().height() * 0.85f;
    mainBuilding.layout();
    mainBuilding.x = getStage().centerX() - (mainBuilding.width / 2);
    mainBuilding.y = 0;
    addActor(mainBuilding);

    if (animateBuildOut) {
      mainBuilding.y = -mainBuilding.height;
      Tween.to(mainBuilding, WidgetAccessor.POSITION, 1000)
              .delay(50)
              .target(mainBuilding.x, 0)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.getTweenManager());
    }
  }

  private void makeDroidTowersLogo(boolean animateBuildOut) {
    TextureAtlas.AtlasRegion droidTowersLogoTexture = mainAtlas.findRegion("droid-towers-logo");
    droidTowersLogo = new Image(droidTowersLogoTexture, fit);
    droidTowersLogo.width = Math.min(getStage().width() * 0.5f, droidTowersLogo.getRegion().getRegionWidth());
    droidTowersLogo.layout();
    droidTowersLogo.y = getStage().height() - droidTowersLogo.getImageHeight() - 150;
    droidTowersLogo.x = 50;

    if (animateBuildOut) {
      droidTowersLogo.x = -droidTowersLogo.getImageWidth();
      Tween.to(droidTowersLogo, WidgetAccessor.POSITION, 1000)
              .delay(50)
              .target(50, droidTowersLogo.y)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.getTweenManager());
    }

    addActor(droidTowersLogo);
  }

  private void makeHappyDroid(boolean animateBuildOut) {
    AnimatedImage happyDroidImage = new AnimatedImage(mainAtlas.findRegions("happy-droid"), 0.05f, true);
    happyDroidImage.delayAfterPlayback(5f);
    happyDroidImage.setScaling(Scaling.fill);
    happyDroidImage.width = (int) Math.min(getStage().width() * 0.15f, happyDroidImage.getRegion().getRegionWidth());
    happyDroidImage.x = (int) getStage().centerX();

    if (animateBuildOut) {
      happyDroidImage.y = -happyDroidImage.height;
      Tween.to(happyDroidImage, WidgetAccessor.POSITION, 500)
              .delay(500)
              .target(happyDroidImage.x, 0)
              .ease(TweenEquations.easeInOutExpo)
              .start(TweenSystem.getTweenManager());
    }

    addActor(happyDroidImage);
  }

  private void makeSunburst() {
    final Sunburst sunburst = new Sunburst(getStage());
    final RotateBy rotateBy = RotateBy.$(-180, 120f);
    rotateBy.setCompletionListener(new OnActionCompleted() {
      @Override
      public void completed(Action action) {
        sunburst.rotation = 0;
        sunburst.action(rotateBy);
      }
    });

    sunburst.action(rotateBy);
    addActor(sunburst);
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

    if (assetManagerFinished) {
      if (splashState == PRELOAD_ONLY) {
        if (!TowerAssetManager.hasFilesToPreload()) {
          if (postLoadRunnable != null) {
            postLoadRunnable.run();
          }
        }
      } else if (splashState == FULL_LOAD) {
        if (assetManagerFinished) {
          if (postLoadRunnable != null) {
            postLoadRunnable.run();
          } else {
            TowerGame.popScene();
          }
        }
      }
    }
  }

  @Override
  public void dispose() {
  }
}
