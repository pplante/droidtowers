/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.List;

public class PickerTool extends ToolBase {
  public PickerTool(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
    super(camera, gameLayers, gameGrid);
  }

  @Override
  public boolean longPress(float x, float y) {
    Vector2 worldPoint = cameraPickRayToWorldPoint(x, y);

    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer.isTouchEnabled() && gameLayer.longPress(worldPoint)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean pan(float x, float y, float deltaX, float deltaY) {
    Vector2 worldPoint = cameraPickRayToWorldPoint(x, y);
    Vector2 deltaPoint = cameraPickRayToWorldPoint(x + -deltaX, y + deltaY);

    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer.isTouchEnabled() && gameLayer.pan(worldPoint, deltaPoint)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean tap(float x, float y, int count, int button) {
    Vector2 worldPoint = cameraPickRayToWorldPoint(x, y);

    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer.isTouchEnabled() && gameLayer.tap(worldPoint, count, button)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean touchDown(float x, float y, int pointer, int button) {
    Vector2 worldPoint = cameraPickRayToWorldPoint(x, y);

    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer.isTouchEnabled() && gameLayer.touchDown(worldPoint, pointer)) {
        return true;
      }
    }

    return false;
  }

  private Vector2 cameraPickRayToWorldPoint(float x, float y) {
    Ray pickRay = camera.getPickRay(x, y);
    Vector3 pickRayEndPoint = pickRay.getEndPoint(1);
    return new Vector2(pickRayEndPoint.x, pickRayEndPoint.y);
  }
}
