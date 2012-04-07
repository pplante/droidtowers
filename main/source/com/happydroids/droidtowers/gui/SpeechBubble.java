/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.input.CameraController;

public class SpeechBubble extends Toast {
  private static TextureAtlas textureAtlas;
  private static BitmapFont labelFont;

  private final Label label;
  private GameObject gameObjectToFollow;
  private GridObject gridObjectToFollow;

  public SpeechBubble() {
    super();
    if (textureAtlas == null) {
      textureAtlas = TowerAssetManager.textureAtlas("hud/misc.txt");
      labelFont = TowerAssetManager.bitmapFont("fonts/helvetica_neue_14_black.fnt");
    }

    NinePatch patch = new NinePatch(textureAtlas.findRegion("speech-bubble-box"), 4, 4, 4, 4);
    Image tip = new Image(textureAtlas.findRegion("speech-bubble-tip"), Scaling.none);
    label = new Label("", new Label.LabelStyle(labelFont, Color.WHITE));

    defaults();
    setBackground(patch);
    pad(4);
    add(label);
    row().align(Align.LEFT).padBottom(-12);
    add(tip);
    pack();
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    Vector3 worldPoint = null;
    if (gameObjectToFollow != null) {
      worldPoint = new Vector3(gameObjectToFollow.getX(), gameObjectToFollow.getY() + gameObjectToFollow.getHeight(), 1f);
    } else if (gridObjectToFollow != null) {
      Vector2 worldTop = gridObjectToFollow.getWorldTop();
      worldPoint = new Vector3(worldTop.x, worldTop.y, 1f);
    }

    if (worldPoint != null) {
      CameraController.instance().getCamera().project(worldPoint);
      x = (int) worldPoint.x - 4;
      y = (int) worldPoint.y + 4;
    }
  }

  public void followObject(GridObject gridObject) {
    gridObjectToFollow = gridObject;
  }

  public void followObject(GameObject gameObject) {
    gameObjectToFollow = gameObject;
  }

  public void setText(String newText) {
    label.setText(newText);
    pack();
  }
}
