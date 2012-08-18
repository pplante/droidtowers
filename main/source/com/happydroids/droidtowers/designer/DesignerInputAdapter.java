/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

class DesignerInputAdapter extends InputAdapter {
  private final Canvas canvas;
  private final Stage stage;
  private final OrthographicCamera camera;
  private Actor selectedItem;
  private final Vector2 touchOffset;
  private final Vector2 originalPosition;

  public DesignerInputAdapter(Canvas canvas, Stage stage, OrthographicCamera camera) {
    this.canvas = canvas;
    this.stage = stage;
    this.camera = camera;
    touchOffset = new Vector2();
    originalPosition = new Vector2();
  }

  public Actor getSelectedItem() {
    return selectedItem;
  }

  public void setSelectedItem(Actor selectedItem) {
    this.selectedItem = selectedItem;
  }

  public void setTouchOffset(Vector2 touchOffset) {
    this.touchOffset.set(touchOffset);
  }

  public void setOriginalPosition(Vector2 originalPosition) {
    this.originalPosition.set(originalPosition);
  }

  @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (selectedItem != null) {
      Vector2 screenCoords = new Vector2(screenX, screenY);
      stage.screenToStageCoordinates(screenCoords);
      float xPos = screenCoords.x - touchOffset.x;
      float yPos = screenCoords.y - touchOffset.y;
      xPos = 16 * MathUtils.floor(xPos / 16);
      yPos = 16 * MathUtils.floor(yPos / 16);
      selectedItem.setPosition(xPos, yPos);

      return true;
    }

    return false;
  }

  @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (selectedItem != null) {
      Vector2 screenCoords = new Vector2(screenX, screenY);
      stage.screenToStageCoordinates(screenCoords);
      canvas.stageToLocalCoordinates(screenCoords);
      Vector3 vec = new Vector3(screenX, screenY, 1f);
      camera.unproject(vec);
      float xPos = vec.x - touchOffset.x;
      float yPos = vec.y - touchOffset.y;
      xPos = 16 * MathUtils.floor(xPos / 16);
      yPos = 16 * MathUtils.floor(yPos / 16);
      if (xPos > 0 && yPos > 0) {
        selectedItem.setPosition(xPos, yPos);
        canvas.add(selectedItem);
      } else {
        selectedItem.remove();
      }

      selectedItem = null;

      return true;
    }

    return false;
  }

  @Override public boolean scrolled(int amount) {
    float scale = canvas.getScaleX();
    scale = MathUtils.clamp(scale + (float) amount / 10, 1f, 2f);
    canvas.setScale(scale);
    canvas.setPosition((-canvas.getWidth() / 2) * canvas.getScaleX(), (-canvas.getHeight() / 2) * canvas.getScaleX());

    return true;
  }
}
