/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.TowerAssetManager;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.types.ProviderType;

public class Maid extends Janitor {
  public Maid(AvatarLayer avatarLayer) {
    super(avatarLayer);
  }

  @Override
  protected TextureAtlas getTextureAtlas() {
    return TowerAssetManager.textureAtlas("characters/maid.txt");
  }

  public static boolean checkProviderType(ProviderType providerType) {
    return providerType.equals(ProviderType.HOTEL_ROOMS);
  }
}
