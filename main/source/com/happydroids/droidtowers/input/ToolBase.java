/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.List;

public class ToolBase implements GestureDetector.GestureListener {
    protected final OrthographicCamera camera;
    protected final List<GameLayer> gameLayers;
    protected final GameGrid gameGrid;

    public ToolBase(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
        this.camera = camera;
        this.gameLayers = gameLayers;
        this.gameGrid = gameGrid;
    }

    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    public boolean longPress(float x, float y) {
        return false;
    }

    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
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
