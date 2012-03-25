package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.unhappyrobot.gui.TiledImage;

public abstract class Scene {
  private static SpriteBatch spriteBatch;
  private final Stage stage;
  private static Skin skin;
  protected static OrthographicCamera camera;

  public Scene() {
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, getSpriteBatch());
  }

  public static void setGuiSkin(Skin guiSkin) {
    Scene.skin = guiSkin;
  }

  public abstract void create(Object... args);

  public abstract void pause();

  public abstract void resume();

  public abstract void render(float deltaTime);

  public abstract void dispose();

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

  protected void addModalBackground() {
    final Texture texture = new Texture(Gdx.files.internal("hud/modal-noise.png"));
    texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    TiledImage background = new TiledImage(texture);
    background.color.a = 0.75f;
    background.width = Gdx.graphics.getWidth();
    background.height = Gdx.graphics.getHeight();
    getStage().addActor(background);
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
}
