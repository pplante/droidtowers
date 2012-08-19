/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;

class GridObjectDesignerItemTouchListener extends InputListener {
  private final DesignerInputAdapter inputProcessor;
  private final TextureAtlas.AtlasRegion region;

  public GridObjectDesignerItemTouchListener(DesignerInputAdapter inputProcessor, TextureAtlas.AtlasRegion region) {
    this.inputProcessor = inputProcessor;
    this.region = region;
  }

  @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
    event.cancel();

    Image selectedItem = new Image(region);
    selectedItem.setScaling(Scaling.none);
    float width = selectedItem.getWidth();
    float height = selectedItem.getHeight();
    if (width < 32) {
      selectedItem.setWidth(32);
    }
    if (height < 32) {
      selectedItem.setHeight(32);
    }
    selectedItem.setOrigin(-width / 2, -height / 2);
    selectedItem.addAction(Actions.sequence(Actions.scaleTo(1.25f, 1.25f, 0.15f),
                                                   Actions.scaleTo(1f, 1f, 0.15f)));
    selectedItem.setPosition(event.getStageX() - x, event.getStageY() - y);

    event.getStage().addActor(selectedItem);

    inputProcessor.setSelectedItem(selectedItem);
    inputProcessor.setTouchOffset(new Vector2(x, y));
    inputProcessor.setOriginalPosition(new Vector2(event.getStageX(), event.getStageY()));

    return true;
  }
}
