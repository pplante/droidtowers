/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.*;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.HappyDroidConnect;

import static com.badlogic.gdx.Application.ApplicationType.Applet;
import static com.happydroids.droidtowers.gui.FontManager.Default;

public class MainMenuButtonPanel extends Table {
  private static final String TAG = MainMenuButtonPanel.class.getSimpleName();
  public static final int BUTTON_WIDTH = Display.devicePixel(280);
  public static final int BUTTON_SPACING = Display.devicePixel(12);
  private NinePatch dropShadowPatch;

  public MainMenuButtonPanel() {
    super();

    dropShadowPatch = TowerAssetManager.ninePatch("swatches/drop-shadow.png", Color.WHITE, 22, 22, 22, 22);
    setBackground(TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.DARK_GRAY));

    pad(BUTTON_SPACING);

    if (!Gdx.app.getType().equals(Applet)) {
      padBottom(0);
    }

    defaults().space(BUTTON_SPACING);

    row();
    TextButton loadGameButton = Default.makeTextButton("load tower");
    add(loadGameButton).fill().width(BUTTON_WIDTH);

    row();
    TextButton newGameButton = Default.makeTextButton("new tower");
    add(newGameButton).fill().width(BUTTON_WIDTH);

    row();
    Table optionsAndCreditsRow = new Table();
    optionsAndCreditsRow.row().fill().space(BUTTON_SPACING);
    add(optionsAndCreditsRow).width(BUTTON_WIDTH);

    TextButton optionsButton = Default.makeTextButton("options");
    optionsAndCreditsRow.add(optionsButton).expandX();

    optionsButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new OptionsDialog(getStage()).show();
      }
    });

    TextButton aboutButton = Default.makeTextButton("credits");
    optionsAndCreditsRow.add(aboutButton).expandX();

    //noinspection PointlessBooleanExpression
    if (TowerConsts.ENABLE_HAPPYDROIDS_CONNECT && !Gdx.app.getType().equals(Applet)) {
      row();
      final TextButton connectToHappyDroids = Default.makeTextButton("login to happydroids.com");
      connectToHappyDroids.setVisible(false);
      add(connectToHappyDroids).fill().width(BUTTON_WIDTH);
      row().padTop(BUTTON_SPACING);

      TowerGameService.instance().afterDeviceIdentification(new Runnable() {
        public void run() {
          Gdx.app.debug(TAG, "After auth, close/show connect button.");
          if (!TowerGameService.instance().isAuthenticated()) {
            connectToHappyDroids.setVisible(true);
            connectToHappyDroids.addListener(new VibrateClickListener() {
              @Override
              public void onClick(InputEvent event, float x, float y) {
                SceneManager.pushScene(HappyDroidConnect.class);
              }
            });
            connectToHappyDroids.addAction(Actions.fadeIn(0.25f));
          }
        }
      });
    }

    if (!Gdx.app.getType().equals(Applet)) {
      row();
      TextButton exitGameButton = Default.makeTextButton("exit");
      add(exitGameButton).fill().width(BUTTON_WIDTH);

      exitGameButton.addListener(new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          Gdx.app.exit();
        }
      });
    }

    newGameButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new NewTowerDialog(getStage()).show();
      }
    });
    loadGameButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new LoadTowerWindow(getStage()).show();
      }
    });

    aboutButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new AboutWindow(getStage()).show();
      }
    });
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    if (this.dropShadowPatch != null) {
      batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
      this.dropShadowPatch.draw(batch, getX() - dropShadowPatch.getLeftWidth(),
                                       getY() - dropShadowPatch.getTopHeight(),
                                       getWidth() + dropShadowPatch.getRightWidth() + dropShadowPatch.getLeftWidth(),
                                       getHeight() + dropShadowPatch.getBottomHeight() + dropShadowPatch.getTopHeight());
    }

    super.drawBackground(batch, parentAlpha);
  }
}
