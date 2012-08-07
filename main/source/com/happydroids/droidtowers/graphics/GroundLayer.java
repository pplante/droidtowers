/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.platform.Display;

public class GroundLayer extends GameLayer implements RespondsToWorldSizeChange {
  public GroundLayer() {
    super();
  }

  @Override
  public void updateWorldSize(Vector2 worldSize) {
    removeAllChildren();

    Texture topTexture = TowerAssetManager.texture("backgrounds/ground-top.png");

    float tiledHeight = TowerConsts.GROUND_HEIGHT - topTexture.getHeight();

    GameObject top = new GameObject(topTexture);
    int biggestScreenDimension = Display.getBiggestScreenDimension();
    top.setPosition(-biggestScreenDimension, tiledHeight);
    top.setSize(worldSize.x + (biggestScreenDimension * 2), topTexture.getHeight());
    top.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    addChild(top);

    Texture tileTexture = TowerAssetManager.texture("backgrounds/ground-tile.png");

    GameObject tile = new GameObject(tileTexture);
    tile.setPosition(-biggestScreenDimension, -biggestScreenDimension);
    tile.setSize(worldSize.x + (biggestScreenDimension * 4), tiledHeight + biggestScreenDimension);
    tile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    addChild(tile);
  }
}
