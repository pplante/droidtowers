/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.badlogic.gdx.Application.ApplicationType.Applet;

public class QuitGameInputAdapter extends InputAdapter {

  private boolean mainMenuIsActive;
  private Dialog quitGameDialog;

  @Override
  public boolean keyDown(int keycode) {
    if (keycode != Input.Keys.ESCAPE && keycode != Input.Keys.BACK || !TowerAssetManager.preloadFinished()) {
      return false;
    }

    mainMenuIsActive = SceneManager.activeScene() instanceof MainMenuScene;

    if (Gdx.app.getType().equals(Applet) && mainMenuIsActive) {
      return false;
    }

    quitGameDialog = new Dialog(DroidTowersGame.getRootUiStage());
    quitGameDialog
            .setTitle("Awe, don't leave me.")
            .setMessage("Are you sure you want to exit " + (mainMenuIsActive ? "the game?" : "to the Main Menu?"))
            .addButton("No way!", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
              }
            })
            .addButton("Yes", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
                if (mainMenuIsActive) {
                  Gdx.app.exit();
                } else {
                  SceneManager.changeScene(MainMenuScene.class);
                }
              }
            })
            .show();

    return true;
  }
}
