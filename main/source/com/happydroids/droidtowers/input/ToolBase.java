/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.NeighborGameGrid;

import java.util.List;

public class ToolBase implements GestureDetector.GestureListener {
  protected final OrthographicCamera camera;
  protected final List<GameLayer> gameLayers;

  public ToolBase(OrthographicCamera camera, List<GameLayer> gameLayers) {
    this.camera = camera;
    this.gameLayers = gameLayers;
  }

  public GameGrid getGameGrid() {
    for (GameLayer gameLayer : gameLayers) {
      if ((gameLayer instanceof GameGrid) && !(gameLayer instanceof NeighborGameGrid)) {
        return (GameGrid) gameLayer;
      }
    }

    return null;
  }

  public boolean touchDown(int x, int y, int pointer) {
    return false;
  }

  public boolean tap(int x, int y, int count) {
    return false;
  }

  public boolean longPress(int x, int y) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY) {
    return false;
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    return false;
  }

  public boolean scrolled(int amount) {
    return false;
  }

  public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23) {
    return false;
  }

  public void cleanup() {

  }

  public void update(float deltaTime) {

  }
}
