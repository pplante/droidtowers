package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.unhappyrobot.gui.TiledImage;

public class SplashScreen extends Scene {

  public SplashScreen(SpriteBatch spriteBatch_) {
    super(spriteBatch_);
  }

  @Override
  public void create() {
    final Texture texture = new Texture(Gdx.files.internal("hud/modal-noise.png"));
    texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    TiledImage background = new TiledImage(texture);
    background.color.a = 0.85f;
    background.width = Gdx.graphics.getWidth();
    background.height = Gdx.graphics.getHeight();
    getStage().addActor(background);

    Label label = new Label("Droid Towers", getGuiSkin());
    label.setStyle(new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/bank_gothic_64.fnt"), false), Color.WHITE));
    label.setAlignment(Align.CENTER);
    label.width = getStage().width();
    label.y = getStage().centerY() * 1.66f;
    getStage().addActor(label);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    getStage().act(deltaTime);
    getStage().draw();
  }
}
