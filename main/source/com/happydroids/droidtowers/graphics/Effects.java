/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Effects {
  public final NinePatch dropShadowPatch;

  public Effects() {
    dropShadowPatch = new NinePatch(new Texture("swatches/drop-shadow.png"), 22, 22, 22, 22);
  }

  public void drawDropShadow(SpriteBatch batch, float parentAlpha, Actor actor) {
    if (this.dropShadowPatch != null) {
      batch.setColor(1, 1, 1, 1f * parentAlpha);
      this.dropShadowPatch.draw(batch,
                                       (int) (actor.x - dropShadowPatch.getLeftWidth()),
                                       ((int) (actor.y - dropShadowPatch.getTopHeight())),
                                       ((int) (actor.width + dropShadowPatch.getRightWidth() + dropShadowPatch.getLeftWidth())),
                                       ((int) (actor.height - 2 + dropShadowPatch.getBottomHeight() + dropShadowPatch.getTopHeight())));
    }
  }

  public NinePatch getDropShadowPatch() {
    return dropShadowPatch;
  }
}
