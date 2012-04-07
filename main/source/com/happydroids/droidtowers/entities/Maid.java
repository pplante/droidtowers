/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

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
