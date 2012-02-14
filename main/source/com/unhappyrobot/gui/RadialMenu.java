package com.unhappyrobot.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.unhappyrobot.TowerGame;

import static com.unhappyrobot.gui.WidgetAccessor.*;

public class RadialMenu extends WidgetGroup {
  public RadialMenu() {
    visible = false;
  }

  public float getPrefWidth() {
    return children.size();
  }

  public float getPrefHeight() {
    return children.size();
  }

  public void show() {
    visible = true;

    float radius = 12f * children.size();
    float angle = 15f;
    float step = 180f / children.size();

    color.a = 1f;

    Timeline timeline = Timeline.createParallel();

    int index = 0;
    for (Actor child : children) {
      child.color.a = 0;
      child.x = child.y = 0;
      child.scaleX = child.scaleY = 0;

      float newX = (float) (-radius * Math.cos(Math.toRadians(angle)));
      float newY = (float) (radius * Math.sin(Math.toRadians(angle)));
      timeline.push(Tween.to(child, POSITION, 200).delay(10 * index++).ease(Cubic.INOUT).target(newX, newY));
      timeline.push(Tween.to(child, SCALE, 200).delay(10 * index++).ease(Quad.INOUT).target(1f, 1f));
      timeline.push(Tween.to(child, OPACITY, 200).delay(10 * index++).target(1f));

      angle += step;
    }

    timeline.start(TowerGame.getTweenManager());
  }

  public void hide() {
    Tween.to(this, OPACITY, 150)
            .target(0.0f)
            .addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                visible = false;
              }
            })
            .start(TowerGame.getTweenManager());
  }
}
