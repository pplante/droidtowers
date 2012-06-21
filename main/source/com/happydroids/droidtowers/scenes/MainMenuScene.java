/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.CloudGameSaveCollection;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.WidgetAccessor;
import com.happydroids.droidtowers.scenes.components.MainMenuButtonPanel;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.happydroids.droidtowers.TowerAssetManager.preloadFinished;
import static com.happydroids.droidtowers.TowerAssetManager.textureAtlas;
import static com.happydroids.droidtowers.platform.Display.scale;

public class MainMenuScene extends SplashScene {
  private static final String TAG = MainMenuScene.class.getSimpleName();
  public static final int BUTTON_WIDTH = scale(280);
  public static final int BUTTON_SPACING = scale(16);

  private CloudGameSaveCollection cloudGameSaves;
  private boolean builtOutMenu;

  @Override
  public void create(Object... args) {
    super.create(args);

    cloudGameSaves = new CloudGameSaveCollection();

    Label versionLabel = FontManager.Default.makeLabel(String.format("%s (%s)", HappyDroidConsts.VERSION, HappyDroidConsts.GIT_SHA.substring(0, 8)));
    versionLabel.setColor(Color.DARK_GRAY);
    versionLabel.x = getStage().width() - versionLabel.width - 5;
    versionLabel.y = getStage().height() - versionLabel.height - 5;
    addActor(versionLabel);

//    DebugUtils.createNonSavableGame(true);
//    DebugUtils.loadFirstGameFound();
//    DebugUtils.loadGameFromCloud(19);
//    new FriendsListWindow(getStage()).show();
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    super.render(deltaTime);

    if (!builtOutMenu && preloadFinished()) {
      builtOutMenu = true;

      buildMenuComponents();
    }
  }

  @Override
  public void dispose() {
  }

  private void buildMenuComponents() {
    progressPanel.markToRemove(true);

    TextureAtlas menuButtonAtlas = textureAtlas("hud/menus.txt");
    addActor(makeLibGDXLogo(menuButtonAtlas));
    addActor(makeHappyDroidsLogo(menuButtonAtlas));

    MainMenuButtonPanel menuButtonPanel = new MainMenuButtonPanel();
    menuButtonPanel.pack();
    menuButtonPanel.y = droidTowersLogo.y - menuButtonPanel.height;
    menuButtonPanel.x = -droidTowersLogo.getImageWidth();
    addActor(menuButtonPanel);

    Tween.to(menuButtonPanel, WidgetAccessor.POSITION, 500)
            .target(50 + (45 * (droidTowersLogo.getImageWidth() / droidTowersLogo.getRegion().getRegionWidth())), menuButtonPanel.y)
            .ease(TweenEquations.easeInOutExpo)
            .start(TweenSystem.manager());
  }

  private Image makeHappyDroidsLogo(TextureAtlas atlas) {
    Image happyDroidsLogo = new Image(atlas.findRegion("happy-droids-logo"));
    happyDroidsLogo.color.a = 0f;
    happyDroidsLogo.action(FadeIn.$(0.125f));
    happyDroidsLogo.x = getStage().width() - happyDroidsLogo.width - scale(5);
    happyDroidsLogo.y = scale(5);
    happyDroidsLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.getPlatformBrowserUtil().launchWebBrowser("http://www.happydroids.com");
      }
    });
    return happyDroidsLogo;
  }

  private Image makeLibGDXLogo(TextureAtlas atlas) {
    Image libGdxLogo = new Image(atlas.findRegion("powered-by-libgdx"));
    libGdxLogo.color.a = 0f;
    libGdxLogo.action(FadeIn.$(0.125f));
    libGdxLogo.x = libGdxLogo.y = scale(5);
    libGdxLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.getPlatformBrowserUtil().launchWebBrowser("http://libgdx.badlogicgames.com");
      }
    });
    return libGdxLogo;
  }
}
