/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes.components;

import static com.happydroids.droidtowers.TowerAssetManager.assetManager;

public class AssetLoadProgressPanel extends ProgressPanel {
  @Override
  public void act(float delta) {
    super.act(delta);

    setProgress((int) (assetManager().getProgress() * 100f));
  }
}
