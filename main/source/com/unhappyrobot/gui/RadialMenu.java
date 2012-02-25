package com.unhappyrobot.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.tween.TweenSystem;

import static com.unhappyrobot.gui.WidgetAccessor.*;

public class RadialMenu extends WidgetGroup {
  public float radius;
  public float arc;

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
    InputSystem.instance().bind(InputSystem.Keys.ESCAPE, new InputCallback() {
      public boolean run(float timeDelta) {
        InputSystem.instance().switchTool(GestureTool.PICKER, null);
        hide();
        unbind();
        return true;
      }
    });
    InputSystem.instance().addInputProcessor(new InputAdapter() {
      @Override
      public boolean touchDown(int x, int y, int pointer, int button) {
        hide();
        InputSystem.instance().removeInputProcessor(this);
        return true;
      }
    }, 100000);


    visible = true;

    float angle = arc / children.size();

    color.a = 1f;

    Timeline timeline = Timeline.createParallel();
    int index = 0;
    for (Actor child : children) {
      child.color.a = 0;
      child.x = child.y = 0;
      child.scaleX = child.scaleY = 0;

      float radian = (float) ((angle * index + angle / 2) * (Math.PI / 180f));

      float newX = (float) (-radius * Math.cos(radian));
      float newY = (float) (radius * Math.sin(radian));

      timeline.push(Tween.to(child, POSITION, 200).delay(10 * index++).target(newX, newY));
      timeline.push(Tween.to(child, SCALE, 200).delay(10 * index++).target(1f, 1f));
      timeline.push(Tween.to(child, OPACITY, 200).delay(10 * index++).target(1f));
    }

    timeline.start(TweenSystem.getTweenManager());
  }

  public void hide() {
    Tween.to(this, OPACITY, 150)
            .target(0.0f)
            .addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                visible = false;
              }
            })
            .start(TweenSystem.getTweenManager());
  }
}
