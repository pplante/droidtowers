/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.server.CloudGameSaveCollection;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.gui.WidgetAccessor;
import com.happydroids.droidtowers.gui.dialogs.ReviewDroidTowersPrompt;
import com.happydroids.droidtowers.scenes.components.MainMenuButtonPanel;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.platform.Platform;
import com.happydroids.security.SecurePreferences;
import com.happydroids.server.HappyDroidService;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;
import static com.happydroids.droidtowers.TowerAssetManager.preloadFinished;
import static com.happydroids.droidtowers.TowerAssetManager.textureAtlas;
import static com.happydroids.droidtowers.gui.dialogs.ReviewDroidTowersPrompt.RATING_TIMES_SINCE_PROMPTED;
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

    Label versionLabel = FontManager.Default.makeLabel(String.format("v%s (%s, %s)", HappyDroidConsts.VERSION, HappyDroidConsts.GIT_SHA.substring(0, 8), HappyDroidService.getDeviceOSMarketName()));
    versionLabel.setColor(Color.LIGHT_GRAY);
    versionLabel.x = getStage().width() - versionLabel.width - 5;
    versionLabel.y = getStage().height() - versionLabel.height - 5;
    addActor(versionLabel);
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

      buildMenuComponents(textureAtlas("hud/menus.txt"));

      SecurePreferences preferences = TowerGameService.instance().getPreferences();

      if (!preferences.getBoolean(ReviewDroidTowersPrompt.RATING_ADDED, false) && !preferences.getBoolean(ReviewDroidTowersPrompt.RATING_NEVER_ASK_AGAIN, false)) {
        int timesSincePrompt = preferences.incrementInt(RATING_TIMES_SINCE_PROMPTED);
        preferences.flush();

        if (timesSincePrompt >= 3 && !Gdx.app.getType().equals(Desktop)) {
          new ReviewDroidTowersPrompt(getStage()).show();
        }
      }
//      DebugUtils.loadFirstGameFound();
//      DebugUtils.createNonSavableGame(true);
    }
  }

  private void buildMenuComponents(final TextureAtlas menuButtonAtlas) {
    progressPanel.markToRemove(true);

    addActor(makeLibGDXLogo(menuButtonAtlas));
    addActor(makeHappyDroidsLogo(menuButtonAtlas));

    if (!Platform.getPurchaseManager().hasPurchasedUnlimitedVersion()) {
      makeUpgradeToUnlimitedButton(menuButtonAtlas);
    }

    MainMenuButtonPanel menuButtonPanel = new MainMenuButtonPanel();
    menuButtonPanel.pack();
    menuButtonPanel.y = droidTowersLogo.y - menuButtonPanel.height;
    menuButtonPanel.x = -droidTowersLogo.getImageWidth();
    addActor(menuButtonPanel);

    Tween.to(menuButtonPanel, WidgetAccessor.POSITION, CAMERA_PAN_DOWN_DURATION)
            .target(50 + (45 * (droidTowersLogo.getImageWidth() / droidTowersLogo.getRegion().getRegionWidth())), menuButtonPanel.y)
            .ease(TweenEquations.easeInOutExpo)
            .start(TweenSystem.manager());
  }

  private void makeUpgradeToUnlimitedButton(TextureAtlas buttonAtlas) {
    Image upgradeToUnlimited = new Image(buttonAtlas.findRegion("upgrade-to-unlimited"));
    upgradeToUnlimited.pack();
    upgradeToUnlimited.y = getStage().centerY() - (upgradeToUnlimited.getImageHeight() / 2);
    upgradeToUnlimited.x = getStage().right() - (upgradeToUnlimited.width + scale(50));
    upgradeToUnlimited.originX = upgradeToUnlimited.width / 2;
    upgradeToUnlimited.originY = upgradeToUnlimited.height / 2;
    addActor(upgradeToUnlimited);

    Tween.to(upgradeToUnlimited, WidgetAccessor.SCALE, 2000)
            .target(0.75f, 0.75f)
            .delay(1.5f)
            .repeatYoyo(Tween.INFINITY, 50)
            .ease(TweenEquations.easeInElastic)
            .start(TweenSystem.manager());

    upgradeToUnlimited.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Platform.getPurchaseManager().requestPurchaseForUnlimitedVersion();
      }
    });
  }

  @Override
  public void dispose() {
  }

  private Image makeHappyDroidsLogo(TextureAtlas atlas) {
    Image happyDroidsLogo = new Image(atlas.findRegion("happy-droids-logo"));
    happyDroidsLogo.color.a = 0f;
    happyDroidsLogo.action(FadeIn.$(0.125f));
    happyDroidsLogo.x = getStage().width() - happyDroidsLogo.width - scale(5);
    happyDroidsLogo.y = scale(5);
    happyDroidsLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser(TowerConsts.HAPPYDROIDS_URI);
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
        Platform.getBrowserUtil().launchWebBrowser("http://libgdx.badlogicgames.com");
      }
    });
    return libGdxLogo;
  }
}
