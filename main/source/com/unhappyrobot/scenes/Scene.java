package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class Scene {
  private final SpriteBatch spriteBatch;
  private final Stage stage;
  private static Skin skin;

  public Scene(SpriteBatch spriteBatch_) {
    if (skin == null) {
      skin = new Skin(Gdx.files.internal("default-skin.ui"), Gdx.files.internal("default-skin.png"));
    }

    spriteBatch = spriteBatch_;
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, spriteBatch);
  }

  public abstract void create();

  public abstract void pause();

  public abstract void resume();

  public abstract void render(float deltaTime);

  public SpriteBatch getSpriteBatch() {
    return spriteBatch;
  }

  public Stage getStage() {
    return stage;
  }

  public Skin getGuiSkin() {
    return skin;
  }
}
