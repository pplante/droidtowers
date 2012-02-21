package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.controllers.AvatarLayer;

public class Janitor extends Avatar {
  public Janitor(AvatarLayer avatarLayer) {
    super(avatarLayer);

    setColor(Color.WHITE);
  }

  @Override
  protected TextureAtlas getTextureAtlas() {
    return new TextureAtlas(Gdx.files.internal("characters/janitor.txt"));
  }
}
