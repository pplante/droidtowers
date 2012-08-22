/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.Screenshot;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformPurchaseManger;
import com.happydroids.platform.purchase.DroidTowerVersions;

public class DebugWindow extends ScrollableTowerWindow {
  public DebugWindow(Stage stage) {
    super("Debug", stage);

    //noinspection PointlessBooleanExpression
    if (!TowerConsts.DEBUG) {
      throw new RuntimeException("ZOMG WUT?");
    }

    defaults().pad(Display.devicePixel(10)).left().expandX().fillX();

    if (SceneManager.activeScene() instanceof TowerScene) {
      row();
      add(makeResetAchievementsButton());
      row();
      add(makeCompleteAllAchievementsButton());

      row();
      add(makeGiveMoneyButton());

      row();
      add(makeTakeAllMoneyButton());
    }

    row();
    add(makeInvalidateTexturesButton());
    row();
    add(makeRestartActiveSceneButton());

    row();
    add(makeDisconnectHappyDroidsButton());
    row();
    add(makeGenerateNewDeviceIdButton());

    row();
    add(makeTakeScreenshotButton());
    row();
    add(makeToggleDebugInfoButton());

    row();
    add(makeTogglePurchaseUnlimitedButton());

    shoveContentUp();
  }

  private Actor makeTakeAllMoneyButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Take ALL Money");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        Player.instance().subtractCurrency(Player.instance().getCoins());
      }
    });
    return button;
  }

  private Actor makeTogglePurchaseUnlimitedButton() {
    final PlatformPurchaseManger purchaseManger = Platform.getPurchaseManager();

    final TextButton button = FontManager.Roboto24
                                      .makeTextButton(purchaseManger.hasPurchasedUnlimitedVersion() ? "Refund" : "Purchase" + " Unlimited Version");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        if (purchaseManger.hasPurchasedUnlimitedVersion()) {
          purchaseManger.revokeItem(purchaseManger.getSkuForVersion(DroidTowerVersions.UNLIMITED_299));
        } else {
          purchaseManger.purchaseItem(purchaseManger.getSkuForVersion(DroidTowerVersions.UNLIMITED_299), "DEBUG WINDOW LOL");
        }

        dismiss();
      }
    });
    return button;
  }

  private TextButton makeToggleDebugInfoButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Toggle Debug Info");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        HappyDroidConsts.DISPLAY_DEBUG_INFO = !HappyDroidConsts.DISPLAY_DEBUG_INFO;
      }
    });
    return button;
  }

  private Actor makeTakeScreenshotButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Take Screenshot");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            Screenshot.capture();
          }
        });
      }
    });
    return button;
  }

  private Actor makeGenerateNewDeviceIdButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Generate new Device ID");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        TowerGameService.instance().setDeviceId(null);
      }
    });
    return button;
  }

  private Actor makeDisconnectHappyDroidsButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Disconnect from happydroids");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        TowerGameService.instance().setSessionToken(null);
      }
    });
    return button;
  }

  private TextButton makeRestartActiveSceneButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Restart Active Scene");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        SceneManager.restartActiveScene();
      }
    });
    return button;
  }

  private TextButton makeInvalidateTexturesButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Invalidate Textures");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        TowerAssetManager.assetManager().invalidateAllTextures();
      }
    });
    return button;
  }

  private Actor makeCompleteAllAchievementsButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Complete all achievements");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        AchievementEngine.instance().completeAll();
        TutorialEngine.instance().completeAll();
      }
    });
    return button;
  }

  private TextButton makeGiveMoneyButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Give $100K");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        Player.instance().addCurrency(100000);
      }
    });
    return button;
  }

  private TextButton makeResetAchievementsButton() {
    TextButton resetAchievements = FontManager.Roboto24.makeTextButton("Reset all achievements");
    resetAchievements.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        AchievementEngine.instance().resetState();
        TutorialEngine.instance().resetState();
      }
    });
    return resetAchievements;
  }
}
