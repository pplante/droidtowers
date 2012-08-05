/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.actions.GameGridClickListener;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.scenes.ViewNeighborScene;

public class NeighborMenuBuilder implements GameGridClickListener {
  private ViewNeighborScene viewNeighborScene;

  public NeighborMenuBuilder(ViewNeighborScene viewNeighborScene) {
    this.viewNeighborScene = viewNeighborScene;
  }

  @Override
  public void click(GameGrid gameGrid, GridObject gridObject, float x, float y) {
    RadialMenu menu = new RadialMenu();
    menu.arc = 45;
    menu.arcStart = 35;
    menu.radius = 100;

    Vector3 pos = new Vector3(x, y, 0f);

    viewNeighborScene.getCamera().project(pos);
    menu.setX(pos.x);
    menu.setY(pos.y);

    System.out.println("pos = " + pos);

    menu.addActor(new Image(TowerAssetManager.textureFromAtlas("tool-housing", "hud/buttons.txt")));
    menu.addActor(new Image(TowerAssetManager.textureFromAtlas("tool-transit", "hud/buttons.txt")));
    menu.addActor(new Image(TowerAssetManager.textureFromAtlas("tool-commerce", "hud/buttons.txt")));

    viewNeighborScene.getStage().addActor(menu);
    menu.show();
  }
}
