package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class Scene {
  private static SpriteBatch spriteBatch;
  private final Stage stage;
  private static Skin skin;
  protected static OrthographicCamera camera;

  public Scene() {
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, getSpriteBatch());
  }

  public static void setGuiSkin(Skin guiSkin) {
    Scene.skin = guiSkin;
  }

  public abstract void create();

  public abstract void pause();

  public abstract void resume();

  public abstract void render(float deltaTime);

  public float getTimeMultiplier() {
    return 1f;
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

  public static Skin getGuiSkin() {
    return skin;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }

  public static void setCamera(OrthographicCamera camera) {
    Scene.camera = camera;
  }
}
