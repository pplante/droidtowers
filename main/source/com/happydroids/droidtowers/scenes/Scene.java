/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.events.GameSpeedChangeEvent;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.graphics.Effects;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.platform.Display;

public abstract class Scene {
  private Object[] startArgs;
  private static SpriteBatch spriteBatch;
  private final Stage stage;
  protected final OrthographicCamera camera;
  private float timeMultiplier;
  private EventBus eventBus;
  private Vector3 previousCameraPosition;
  private float previousCameraZoom;
  protected CameraController cameraController;
  private final Effects effects;

  public Scene() {
    camera = new OrthographicCamera(Display.getWidth(), Display.getHeight());
    stage = new Stage(Display.getWidth(), Display.getHeight(), false, getSpriteBatch());
//    stage = new Stage(800, 480, true, getSpriteBatch());
    cameraController = new CameraController(camera, new Vector2(Display.getWidth(), Display.getHeight()));
    eventBus = new SafeEventBus(getClass().getSimpleName());
    timeMultiplier = 1f;

    effects = new Effects();
  }

  public abstract void create(Object... args);

  public abstract void pause();

  public abstract void resume();

  public abstract void render(float deltaTime);

  public abstract void dispose();

  public float getTimeMultiplier() {
    return timeMultiplier;
  }

  public void setTimeMultiplier(float value) {
    timeMultiplier = MathUtils.clamp(value, TowerConsts.GAME_SPEED_MIN, TowerConsts.GAME_SPEED_MAX);
    events().post(new GameSpeedChangeEvent(this));
  }

  public SpriteBatch getSpriteBatch() {
    return spriteBatch;
  }

  public static void setSpriteBatch(SpriteBatch spriteBatch) {
    Scene.spriteBatch = spriteBatch;
  }

  public Stage getStage() {
    return stage;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }

  protected void addActor(Actor actor) {
    getStage().addActor(actor);
  }

  protected void center(Actor actor) {
    centerHorizontally(actor);
    centerVertically(actor);
  }

  protected void centerHorizontally(Actor actor) {
    actor.setX((int) (getStage().getWidth() - actor.getWidth()) / 2);
  }


  protected void centerVertically(Actor actor) {
    actor.setY((int) (getStage().getHeight() - actor.getHeight()) / 2);
  }

  public EventBus events() {
    return eventBus;
  }

  public CameraController getCameraController() {
    return cameraController;
  }

  public void setStartArgs(Object[] startArgs) {
    this.startArgs = startArgs;
  }

  public Object[] getStartArgs() {
    return startArgs;
  }

  public Effects effects() {
    return effects;
  }
}
