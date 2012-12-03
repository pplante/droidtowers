/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.happydroids.droidtowers.gui.WidgetAccessor.*;

public class RadialMenu extends WidgetGroup {
  public float radius;
  public float arc;
  public float arcStart;


  public RadialMenu() {
    setVisible(false);
    arc = 60f;
  }

  public float getPrefWidth() {
    return getChildren().size;
  }

  public float getPrefHeight() {
    return getChildren().size;
  }

  public void show() {
    InputSystem.instance().bind(InputSystem.Keys.ESCAPE, closeMenuInputCallback);
    InputSystem.instance().addInputProcessor(closeMenuInputAdapter, 0);

    setVisible(true);

    float angle = arc / getChildren().size;

    getColor().a = 1f;

    Timeline timeline = Timeline.createParallel();
    int index = 0;
    for (Actor child : getChildren()) {
      child.getColor().a = 0;
      child.setX(0);
      child.setY(0);

      float radian = (float) ((arcStart + (angle * index + angle / 2)) * (Math.PI / 180f));

      float newX = (float) (-radius * Math.cos(radian));
      float newY = (float) (radius * Math.sin(radian));

      timeline.push(Tween.to(child, POSITION, 200).delay(10 * index++).target(newX, newY));
      timeline.push(Tween.to(child, SCALE, 200).delay(10 * index++).target(1f, 1f));
      timeline.push(Tween.to(child, OPACITY, 200).delay(10 * index++).target(1f));
    }

    timeline.start(TweenSystem.manager());
  }

  public void close() {
    InputSystem.instance().removeInputProcessor(closeMenuInputAdapter);

    Tween.to(this, OPACITY, 150)
            .target(0.0f)
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                setVisible(false);
              }
            })
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(TweenSystem.manager());
  }

  private InputAdapter closeMenuInputAdapter = new InputAdapter() {
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
      Vector2 touchDown = new Vector2(x, y);
      getStage().screenToStageCoordinates(touchDown);
      stageToLocalCoordinates(touchDown);

      if (hit(touchDown.x, touchDown.y, true) == null) {
        close();
      }

      return false;
    }
  };
  private InputCallback closeMenuInputCallback = new InputCallback() {
    public boolean run(float timeDelta) {
      InputSystem.instance().switchTool(GestureTool.PICKER, null);
      close();
      unbind();
      return true;
    }
  };
}
