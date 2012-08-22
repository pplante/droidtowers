/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;

import static com.badlogic.gdx.math.MathUtils.floor;

class GridObjectDesignerItemTouchListener extends InputListener {
  private final Canvas canvas;
  private final TextureAtlas.AtlasRegion region;
  private Image selectedItem;
  public static final int GRID_SNAP = 2;
  private Vector2 touchOffset;

  public GridObjectDesignerItemTouchListener(Canvas canvas, TextureAtlas.AtlasRegion region) {
    this.canvas = canvas;
    this.region = region;
    touchOffset = new Vector2();
  }

  @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
    event.stop();
    selectedItem = new Image(region);
    canvas.getStage().addActor(selectedItem);
    selectedItem.setScale(2);
    selectedItem.setScaling(Scaling.none);
    selectedItem.addAction(Actions.scaleTo(canvas.getScaleX(), canvas.getScaleY(), 0.35f));

    touchOffset.set(x / 2, y / 2);

    syncItemPosition(event);

    return true;
  }

  @Override public void touchDragged(InputEvent event, float x, float y, int pointer) {
    if (selectedItem != null) {
      event.stop();

      syncItemPosition(event);
    }
  }

  @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
    if (selectedItem != null) {
      event.stop();

      syncItemPosition(event);

      Vector2 localCoords = new Vector2(selectedItem.getX(), selectedItem.getY());

      Rectangle canvasRect = new Rectangle(canvas.getX(), canvas.getY(),
                                                  canvas.getWidth(),
                                                  canvas.getHeight());
      Rectangle itemRect = new Rectangle(localCoords.x,
                                                localCoords.y,
                                                selectedItem.getWidth(),
                                                selectedItem.getHeight());

      System.out.println("canvasRect = " + canvasRect);
      System.out.println("itemRect = " + itemRect);

      if (itemRect.overlaps(canvasRect)) {
        float xPos = (float) (GRID_SNAP * floor((localCoords.x - touchOffset.x) / GRID_SNAP));
        float yPos = (float) (GRID_SNAP * floor((localCoords.y - touchOffset.y) / GRID_SNAP));
        xPos = MathUtils.clamp(xPos, 0, canvasRect.getWidth() - selectedItem.getWidth());
        yPos = MathUtils.clamp(yPos, 0, canvasRect.getHeight() - selectedItem.getHeight());
        selectedItem.clearActions();
        selectedItem.setPosition(xPos, yPos);
        selectedItem.setScale(1f);
        canvas.add(selectedItem);
      } else {
        selectedItem.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0.15f),
                                                                        Actions.moveBy(selectedItem.getWidth(),
                                                                                              selectedItem.getHeight(), 0.15f),
                                                                        Actions.scaleTo(0, 0, 0.15f)),
                                                       Actions.removeActor()));
      }

      selectedItem = null;
    }
  }

  private void syncItemPosition(InputEvent event) {
    Vector2 stageCoords = new Vector2(event.getStageX(), event.getStageY());
    selectedItem.setPosition((float) (GRID_SNAP * floor((stageCoords.x - touchOffset.x * canvas.getScaleX()) / GRID_SNAP)),
                                    (float) (GRID_SNAP * floor((stageCoords.y - touchOffset.y * canvas.getScaleY()) / GRID_SNAP)));
  }
}
