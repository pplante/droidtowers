package com.unhappyrobot.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.gui.WidgetAccessor;
import com.unhappyrobot.input.CameraController;
import com.unhappyrobot.input.CameraControllerAccessor;

public class TweenSystem {
  private TweenSystem() {

  }

  static TweenManager tweenManager;

  static {
    Tween.registerAccessor(CameraController.class, new CameraControllerAccessor());
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
