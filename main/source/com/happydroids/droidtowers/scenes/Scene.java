/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.events.GameSpeedChangeEvent;

public abstract class Scene {
  private static SpriteBatch spriteBatch;
  private final Stage stage;
  protected static OrthographicCamera camera;
  private float timeMultiplier;
  private EventBus eventBus;

  public Scene() {
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, getSpriteBatch());
    eventBus = new EventBus(getClass().getSimpleName());
    timeMultiplier = 1f;
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
    timeMultiplier = MathUtils.clamp(value, 0.5f, 4f);
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

  public static void setCamera(OrthographicCamera camera) {
    Scene.camera = camera;
  }

  protected void addActor(Actor actor) {
    getStage().addActor(actor);
  }

  protected void center(Actor actor) {
    centerHorizontally(actor);
    centerVertically(actor);
  }

  protected void centerHorizontally(Actor actor) {
    actor.x = (int) (getStage().width() - actor.width) / 2;
  }


  private void centerVertically(Actor actor) {
    actor.y = (int) (getStage().height() - actor.height) / 2;
  }

  public EventBus events() {
    return eventBus;
  }
}
