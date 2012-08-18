/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

class DesignerInputAdapter extends InputAdapter {
  private final Canvas canvas;
  private Actor selectedItem;
  private Vector2 touchOffset;
  private Vector2 originalPosition;

  public DesignerInputAdapter(Canvas canvas) {
    this.canvas = canvas;
  }

  public Actor getSelectedItem() {
    return selectedItem;
  }

  public void setSelectedItem(Actor selectedItem) {
    this.selectedItem = selectedItem;
  }

  public void setTouchOffset(Vector2 touchOffset) {
    this.touchOffset = touchOffset;
  }

  public void setOriginalPosition(Vector2 originalPosition) {
    this.originalPosition = originalPosition;
  }

  @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (selectedItem != null) {
      Vector2 screenCoords = new Vector2(screenX, screenY);
      canvas.getStage().screenToStageCoordinates(screenCoords);
      float xPos = screenCoords.x - touchOffset.x;
      float yPos = screenCoords.y - touchOffset.y;
      xPos = 16 * MathUtils.floor(xPos / 16);
      yPos = 16 * MathUtils.floor(yPos / 16);
      selectedItem.setPosition(xPos, yPos);
    }

    return selectedItem != null;
  }

  @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    boolean hadItem = selectedItem != null;

    if (hadItem) {
      Vector2 screenCoords = new Vector2(screenX, screenY);
      canvas.getStage().screenToStageCoordinates(screenCoords);
      canvas.stageToLocalCoordinates(screenCoords);
      float xPos = screenCoords.x - touchOffset.x;
      float yPos = screenCoords.y - touchOffset.y;
      xPos = 16 * MathUtils.floor(xPos / 16);
      yPos = 16 * MathUtils.floor(yPos / 16);
      if (xPos > 0 && yPos > 0) {
        selectedItem.setPosition(xPos, yPos);
        canvas.add(selectedItem);
      } else {
        selectedItem.remove();
      }

      selectedItem = null;
    }

    return hadItem;
  }
}
