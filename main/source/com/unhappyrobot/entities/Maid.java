package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.types.ProviderType;

public class Maid extends Janitor {
  public Maid(AvatarLayer avatarLayer) {
    super(avatarLayer);
  }

  @Override
  protected TextureAtlas getTextureAtlas() {
    return new TextureAtlas(Gdx.files.internal("characters/maid.txt"));
  }

  @Override
  protected boolean checkProviderType(ProviderType providerType) {
    return providerType.equals(ProviderType.HOTEL_ROOMS);
  }
}
