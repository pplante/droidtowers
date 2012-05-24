/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

import java.util.Set;

import static com.happydroids.droidtowers.SplashSceneStates.FULL_LOAD;
import static com.happydroids.droidtowers.SplashSceneStates.PRELOAD_ONLY;
import static com.happydroids.droidtowers.TowerAssetManager.assetManager;
import static com.happydroids.droidtowers.platform.Display.scale;

public class SplashScene extends Scene {
  private static final String[] STRINGS = new String[]{
                                                              "reticulating splines...",
                                                              "manufacturing robots",
                                                              "tickling random number generator",
                                                              "wasting your time",
                                                              "infinite recursion",
                                                              "are we there yet?",
                                                              "solving world hunger",
                                                              "booting skynet...SUCCESS!",
                                                              "GLaDOS loves you.",
                                                              "priming buttons for clicking",
                                                              "calculating shipping and handling",
                                                              "contacting the authorities",
                                                              "I'm still alive...",
                                                              "downloading pictures of cats",
                                                              "spinning up ftl drives",
                                                              "so, uhh...how are you?",
                                                              "its going to be\na beautiful day!",
                                                              "de-fuzzing logic pathways",
                                                              "cleaning the tubes",
  };
  private Label progressLabel;
  private Label loadingMessage;
  private boolean selectedNewMessage;
  private Sprite happyDroid;
  private SplashSceneStates splashState = PRELOAD_ONLY;
  private GameSave gameSave;
  private int progressLastChanged;
  private Set<String> messagesUsed;
  private Runnable postLoadRunnable;

  @Override
  public void create(Object... args) {
    if (args != null) {
      splashState = ((SplashSceneStates) args[0]);

      if (args.length > 1) {
        Object firstArg = args[1];
        if (firstArg instanceof GameSave) {
          gameSave = ((GameSave) firstArg);
          postLoadRunnable = new Runnable() {
            public void run() {
              TowerGame.changeScene(TowerScene.class, gameSave);
            }
          };
        } else if (firstArg instanceof Runnable) {
          postLoadRunnable = (Runnable) firstArg;
        }
      }
    }

    messagesUsed = Sets.newHashSet();

    Label titleLabel = FontManager.Roboto64.makeLabel("Droid Towers", Color.WHITE, Align.CENTER);
    titleLabel.y = getStage().centerY() * 1.66f;
    centerHorizontally(titleLabel);
    addActor(titleLabel);

    loadingMessage = FontManager.Roboto32.makeLabel(selectRandomMessage(), Color.WHITE, Align.CENTER);
    loadingMessage.setAlignment(Align.CENTER);
    center(loadingMessage);
    addActor(loadingMessage);

    progressLabel = FontManager.Roboto64.makeLabel(null, Color.WHITE, Align.CENTER);
    progressLabel.setAlignment(Align.CENTER);
    centerHorizontally(progressLabel);
    progressLabel.y = scale(100);
    addActor(progressLabel);

    happyDroid = new GameObject(new Texture("happy-droid.png"));
    happyDroid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
    Tween.to(happyDroid, GameObjectAccessor.OPACITY, 1000)
            .target(0f)
            .setCallback(new TweenCallback() {
              @Override
              public void onEvent(int type, BaseTween source) {
                happyDroid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .repeat(Tween.INFINITY, 100)
            .start(TweenSystem.getTweenManager());
  }

  private String selectRandomMessage() {
    String msg;
    do {
      msg = STRINGS[Random.randomInt(STRINGS.length - 1)];
    } while (messagesUsed.contains(msg));

    messagesUsed.add(msg);

    return msg;
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {

  }

  @Override
  public void render(float deltaTime) {
    boolean assetManagerFinished = assetManager().update();
    Thread.yield();

    if (!assetManagerFinished) {
      int progress = (int) (assetManager().getProgress() * 100f);
      progressLabel.setText(progress + "%");

      if (progress - progressLastChanged >= 33) {
        progressLastChanged = progress;
        loadingMessage.setText(selectRandomMessage());
      }

      try {
        getSpriteBatch().begin();
        happyDroid.draw(getSpriteBatch());
      } catch (Throwable ignored) {
      } finally {
        getSpriteBatch().end();
      }
    } else {
      if (splashState == PRELOAD_ONLY) {
        if (!TowerAssetManager.hasFilesToPreload()) {
          if (postLoadRunnable != null) {
            postLoadRunnable.run();
          }
        }
      } else if (splashState == FULL_LOAD) {
        if (assetManagerFinished) {
          if (postLoadRunnable != null) {
            postLoadRunnable.run();
          } else {
            TowerGame.popScene();
          }
        }
      }
    }
  }

  @Override
  public void dispose() {
  }
}
