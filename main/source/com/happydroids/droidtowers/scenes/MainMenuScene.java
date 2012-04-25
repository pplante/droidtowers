/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.MoveBy;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.interpolators.OvershootInterpolator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.DebugUtils;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.*;
import com.happydroids.droidtowers.tween.TweenSystem;

import java.lang.reflect.Constructor;

import static com.happydroids.droidtowers.platform.Display.scale;

public class MainMenuScene extends Scene {
  private static final String TAG = MainMenuScene.class.getSimpleName();
  public static final int BUTTON_WIDTH = scale(280);
  public static final int BUTTON_SPACING = scale(16);

  private SplashCloudLayer cloudLayer;

  @Override
  public void create(Object... args) {
    Table container = new Table(getGuiSkin());
    container.defaults().center().left();

    Label label = FontManager.Roboto64.makeLabel("Droid Towers");
    container.add(label).align(Align.CENTER);
    container.row();

    Label versionLabel = FontManager.Default.makeLabel(String.format("%s (%s)", HappyDroidConsts.VERSION, HappyDroidConsts.GIT_SHA.substring(0, 8)));
    versionLabel.setColor(Color.DARK_GRAY);
    container.add(versionLabel).right().padTop(scale(-8));
    container.row().padTop(30);

    Tween.to(label, WidgetAccessor.COLOR, 500)
            .target(1, 1, 1, 0.65f)
            .repeatYoyo(Tween.INFINITY, 250)
            .start(TweenSystem.getTweenManager());

    if (TowerConsts.ENABLE_HAPPYDROIDS_CONNECT && TowerGameService.instance().haveNetworkConnection() && !TowerGameService.instance().hasAuthenticated()) {
      TextButton connectFacebookButton = FontManager.RobotoBold18.makeTextButton("login to happydroids.com", getGuiSkin());
      connectFacebookButton.setClickListener(new LaunchWindowClickListener(ConnectToHappyDroidsWindow.class));
      container.add(connectFacebookButton).fill().maxWidth(BUTTON_WIDTH);
      container.row().padTop(BUTTON_SPACING);
    }

    TextButton newGameButton = FontManager.RobotoBold18.makeTextButton("new game", getGuiSkin());
    container.add(newGameButton).fill().maxWidth(BUTTON_WIDTH);
    container.row().padTop(BUTTON_SPACING);

    TextButton loadGameButton = FontManager.RobotoBold18.makeTextButton("load game", getGuiSkin());
    container.add(loadGameButton).fill().maxWidth(BUTTON_WIDTH);
    container.row().padTop(BUTTON_SPACING);

//    TextButton optionsButton = FontManager.RobotoBold18.makeTextButton("options", getGuiSkin());
//    container.add(optionsButton).fill().maxWidth(BUTTON_WIDTH);
//    container.row().padTop(BUTTON_SPACING);

    TextButton exitGameButton = FontManager.RobotoBold18.makeTextButton("exit game", getGuiSkin());
    container.add(exitGameButton).fill().maxWidth(BUTTON_WIDTH);
    container.row();


    TextureAtlas atlas = TowerAssetManager.textureAtlas("hud/menus.txt");
    Image libGdxLogo = new Image(atlas.findRegion("powered-by-libgdx"));
    libGdxLogo.setAlign(Align.CENTER);
    libGdxLogo.x = 5;
    libGdxLogo.y = 5;
    libGdxLogo.scaleX = libGdxLogo.scaleY = 0f;
    libGdxLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.getPlatformBrowserUtil().launchWebBrowser("http://libgdx.badlogicgames.com");
      }
    });
    addActor(libGdxLogo);


    libGdxLogo.action(ScaleTo.$(1f, 1f, 0.55f)
                              .setInterpolator(OvershootInterpolator.$(1.75f)));

    Image happyDroidsLogo = new Image(atlas.findRegion("happy-droids-logo"));
    happyDroidsLogo.setAlign(Align.CENTER);
    happyDroidsLogo.x = getStage().width() - happyDroidsLogo.width - 5;
    happyDroidsLogo.y = 5;
    happyDroidsLogo.scaleX = happyDroidsLogo.scaleY = 0f;
    happyDroidsLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.getPlatformBrowserUtil().launchWebBrowser("http://www.happydroids.com");
      }
    });
    addActor(happyDroidsLogo);


    happyDroidsLogo.action(ScaleTo.$(1f, 1f, 0.55f)
                                   .setInterpolator(OvershootInterpolator.$(1.75f)));

    container.pack();
    addActor(container);
    center(container);

    container.color.a = 0f;

    container.action(FadeIn.$(0.85f));
    container.action(Sequence.$(
                                       MoveBy.$(0, -container.height, 0f),
                                       MoveBy.$(0, container.height, 0.75f)
                                               .setInterpolator(OvershootInterpolator.$(3f)
                                               )));

    cloudLayer = new SplashCloudLayer();

    newGameButton.setClickListener(new LaunchWindowClickListener(NewGameWindow.class));
    loadGameButton.setClickListener(new LaunchWindowClickListener(LoadGameWindow.class));

    exitGameButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Gdx.app.exit();
      }
    });


    DebugUtils.createNonSavableGame();
//    DebugUtils.loadFirstGameFound();
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    TowerAssetManager.assetManager().update();

    cloudLayer.update(deltaTime);
    cloudLayer.render(getSpriteBatch(), getCamera());
  }

  @Override
  public void dispose() {
  }

  private class LaunchWindowClickListener implements ClickListener {
    private final Class<? extends TowerWindow> windowClass;

    public LaunchWindowClickListener(Class<? extends TowerWindow> windowClass) {
      this.windowClass = windowClass;
    }

    public void click(Actor actor, float x, float y) {
      try {
        Constructor<? extends TowerWindow> constructor = windowClass.getConstructor(Stage.class, Skin.class);
        TowerWindow window = constructor.newInstance(getStage(), getGuiSkin());
        window.show();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
