package com.unhappyrobot.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.gui.WidgetAccessor;

public class TweenSystem {
  static TweenManager tweenManager;

  static {
    Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
    Tween.registerAccessor(Actor.class, new WidgetAccessor());
  }

  public static TweenManager getTweenManager() {
    if (tweenManager == null) {
      tweenManager = new TweenManager();
    }

    return tweenManager;
  }

  public static void setTweenManager(TweenManager tweenManager) {
    TweenSystem.tweenManager = tweenManager;
  }
}
