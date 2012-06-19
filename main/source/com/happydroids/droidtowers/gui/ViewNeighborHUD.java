/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.scenes.components.SceneManager;

public class ViewNeighborHUD extends Table {
  public ViewNeighborHUD(final CloudGameSave playerGameSave) {
    super();

    defaults().top().left();

    TextButton backButton = FontManager.Roboto18.makeTextButton("< back to my tower");
    backButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        SceneManager.popScene();
      }
    });

    TextButton addNeighborsButton = FontManager.Roboto18.makeTextButton("add neighbors");
    addNeighborsButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new FriendsListWindow(getStage(), playerGameSave).show();
      }
    });

    row().top().left().fillX();
    add(backButton);
    add().expandX();
    add(addNeighborsButton).right();
  }

  @Override
  public float getPrefWidth() {
    return Gdx.graphics.getWidth();
  }

  public void showToast(String toastText) {
    Toast toast = new Toast();
    toast.setMessage(toastText);
    getStage().addActor(toast);
    toast.show();
  }
}
