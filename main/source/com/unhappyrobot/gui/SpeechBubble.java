package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;

public class SpeechBubble extends Toast {
  private static TextureAtlas textureAtlas;
  private static BitmapFont labelFont;

  private final NinePatch patch;
  private final Label label;
  private final Image tip;

  public SpeechBubble() {
    if (textureAtlas == null) {
      textureAtlas = new TextureAtlas(Gdx.files.internal("hud/misc.txt"));
      labelFont = new BitmapFont(Gdx.files.internal("fonts/helvetica_neue_14_black.fnt"), false);
    }

    patch = new NinePatch(textureAtlas.findRegion("speech-bubble-box"), 4, 4, 4, 4);
    tip = new Image(textureAtlas.findRegion("speech-bubble-tip"), Scaling.none);
    label = new Label("hello! i am an avatar!\nthis is another line!\n\nand another way down here!!", new Label.LabelStyle(labelFont, Color.WHITE));

    defaults();
    setBackground(patch);
    pad(4);
    add(label);
    pack();

    alpha = 1f;
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    this.x -= tip.width - 2;
    this.y += tip.height - 2;
    super.draw(batch, parentAlpha);
    tip.x = this.x + tip.width;
    tip.y = this.y - tip.height + 2;
    tip.draw(batch, parentAlpha);
  }
}
