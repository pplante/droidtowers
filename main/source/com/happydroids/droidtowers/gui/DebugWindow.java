/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.ScreenShot;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformPurchaseManger;

import static com.badlogic.gdx.Application.ApplicationType.Android;
import static com.happydroids.droidtowers.platform.Display.scale;

public class DebugWindow extends ScrollableTowerWindow {
  public DebugWindow(Stage stage) {
    super("Debug", stage);

    //noinspection PointlessBooleanExpression
    if (!TowerConsts.DEBUG) {
      throw new RuntimeException("ZOMG WUT?");
    }

    defaults().pad(scale(10)).left();

    if (SceneManager.getActiveScene() instanceof TowerScene) {
      row();
      add(makeResetAchievementsButton());
      add(makeCompleteAllAchievementsButton());

      row();
      add(makeGiveMoneyButton());
    }

    row();
    add(makeInvalidateTexturesButton());
    add(makeRestartActiveSceneButton());

    row();
    add(makeDisconnectHappyDroidsButton());
    add(makeGenerateNewDeviceIdButton());

    row();
    add(makeTakeScreenshotButton());
    add(makeToggleDebugInfoButton());

    row();
    add(makeAndroidTestPurchaseButton());
    add(makeTogglePurchaseUnlimitedButton());

    shoveContentUp();
  }

  private Actor makeTogglePurchaseUnlimitedButton() {
    final PlatformPurchaseManger purchaseManger = Platform.getPurchaseManager();

    final TextButton button = FontManager.Roboto24.makeTextButton(purchaseManger.hasPurchasedUnlimitedVersion() ? "Refund" : "Purchase" + " Unlimited Version");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        if (purchaseManger.hasPurchasedUnlimitedVersion()) {
          purchaseManger.revokeItem("droidtowers.version.unlimited");
        } else {
          purchaseManger.purchaseItem("droidtowers.version.unlimited");
        }

        dismiss();
      }
    });
    return button;
  }

  private Actor makeAndroidTestPurchaseButton() {
    Table table = new Table();
    table.defaults().space(scale(16));
    final SelectBox selectBox = new SelectBox(TowerAssetManager.getCustomSkin());
    table.add(selectBox);

    if (Gdx.app.getType().equals(Android)) {
      selectBox.setItems(new String[]{"android.test.purchased", "android.test.canceled", "android.test.refunded", "android.test.item_unavailable"});
    } else {
      selectBox.setItems(new String[]{"desktop.test.purchased", "desktop.test.canceled", "desktop.test.refunded", "desktop.test.item_unavailable"});
    }

    TextButton button = FontManager.Roboto24.makeTextButton("Fake Purchase");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Platform.getPurchaseManager().requestPurchase(selectBox.getSelection());
      }
    });

    table.add(button);

    return table;
  }

  private TextButton makeToggleDebugInfoButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Toggle Debug Info");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        HappyDroidConsts.DISPLAY_DEBUG_INFO = !HappyDroidConsts.DISPLAY_DEBUG_INFO;
      }
    });
    return button;
  }

  private Actor makeTakeScreenshotButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Take Screenshot");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        dismiss();
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            ScreenShot.capture();
          }
        });
      }
    });
    return button;
  }

  private Actor makeGenerateNewDeviceIdButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Generate new Device ID");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Preferences preferences = Gdx.app.getPreferences("CONNECT");
        preferences.clear();
        preferences.flush();
      }
    });
    return button;
  }

  private Actor makeDisconnectHappyDroidsButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Disconnect from happydroids");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        TowerGameService.instance().setSessionToken(null);
      }
    });
    return button;
  }

  private TextButton makeRestartActiveSceneButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Restart Active Scene");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        SceneManager.restartActiveScene();
      }
    });
    return button;
  }

  private TextButton makeInvalidateTexturesButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Invalidate Textures");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        TowerAssetManager.assetManager().invalidateAllTextures();
      }
    });
    return button;
  }

  private Actor makeCompleteAllAchievementsButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Complete all achievements");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        AchievementEngine.instance().completeAll();
        TutorialEngine.instance().completeAll();
      }
    });
    return button;
  }

  private TextButton makeGiveMoneyButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Give $100K");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Player.instance().addCurrency(100000);
      }
    });
    return button;
  }

  private TextButton makeResetAchievementsButton() {
    TextButton resetAchievements = FontManager.Roboto24.makeTextButton("Reset all achievements");
    resetAchievements.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        AchievementEngine.instance().resetState();
        TutorialEngine.instance().resetState();
      }
    });
    return resetAchievements;
  }
}
