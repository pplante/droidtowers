/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerGame;

public class ViewNeighborHUD extends Table {
  public ViewNeighborHUD() {
    super();

    defaults().top().left().expand();

    TextButton backButton = FontManager.Roboto18.makeTextButton("< back");
    backButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        TowerGame.popScene();
      }
    });

    add(backButton);
  }

  public void showToast(String toastText) {
    Toast toast = new Toast();
    toast.setMessage(toastText);
    getStage().addActor(toast);
    toast.show();
  }
}
