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
    visible = false;
    arc = 60f;
  }

  public float getPrefWidth() {
    return children.size();
  }

  public float getPrefHeight() {
    return children.size();
  }

  public void show() {
    InputSystem.instance().bind(InputSystem.Keys.ESCAPE, closeMenuInputCallback);
    InputSystem.instance().addInputProcessor(closeMenuInputAdapter, 0);

    visible = true;

    float angle = arc / children.size();

    color.a = 1f;

    Timeline timeline = Timeline.createParallel();
    int index = 0;
    for (Actor child : children) {
      child.color.a = 0;
      child.x = child.y = 0;

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
                visible = false;
              }
            })
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(TweenSystem.manager());
  }

  private InputAdapter closeMenuInputAdapter = new InputAdapter() {
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
      Vector2 touchDown = new Vector2();
      getStage().toStageCoordinates(x, y, touchDown);
      toLocalCoordinates(touchDown);

      if (hit(touchDown.x, touchDown.y) == null) {
        close();
        return true;
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
