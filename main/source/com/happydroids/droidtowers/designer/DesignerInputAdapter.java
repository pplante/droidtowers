/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

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
      float xPos;
      float yPos;
      xPos = 16 * MathUtils.floor((screenCoords.x - touchOffset.x) / 16);
      yPos = 16 * MathUtils.floor((screenCoords.y - touchOffset.y) / 16);

      Rectangle itemRect = new Rectangle(xPos, yPos, selectedItem.getWidth(), selectedItem.getHeight());
      Rectangle canvasRect = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
      if (canvasRect.overlaps(itemRect)) {
        selectedItem.setScale(1f);
        selectedItem.setPosition(xPos, yPos);
        xPos = MathUtils.clamp(xPos, 0f, canvas.getWidth() - selectedItem.getWidth());
        yPos = MathUtils.clamp(yPos, 0f, canvas.getHeight() - selectedItem.getHeight());
        selectedItem.addAction(Actions.moveTo(xPos, yPos, 0.075f));
        canvas.add(selectedItem);
      } else {
        final Actor actor = selectedItem;
        selectedItem.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(0.15f),
                                                                        Actions.moveBy(selectedItem.getWidth(),
                                                                                              selectedItem.getHeight(), 0.15f),
                                                                        Actions.scaleTo(0, 0, 0.15f)),
                                                       Actions.run(new Runnable() {
                                                         @Override public void run() {
                                                           actor.remove();
                                                         }
                                                       })));
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
//    canvas.setPosition((-canvas.getWidth() / 2) * canvas.getScaleX(), (-canvas.getHeight() / 2) * canvas.getScaleX());

    return true;
  }
}
