/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;

public class ViewNeighborHUD extends Table {
  public ViewNeighborHUD(final GameState playerGameState) {
    super();

    defaults().top().left();

    TextButton backButton = FontManager.Roboto18.makeTextButton("< back to my tower");
    backButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        SceneManager.popScene();
      }
    });

    TextButton addNeighborsButton = FontManager.Roboto18.makeTextButton("add neighbors");
    addNeighborsButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new FriendsListWindow(getStage(), playerGameState).show();
      }
    });

    row().top().left().fillX();
    add(backButton);
    add().expandX();
    add(addNeighborsButton).right();
  }

  @Override
  public float getPrefWidth() {
    return Display.getWidth();
  }

  public void showToast(String toastText) {
    Toast toast = new Toast();
    toast.setMessage(toastText);
    getStage().addActor(toast);
    toast.show();
  }
}
