/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerAssetManagerFilesList;
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

  @Override
  public void create(Object... args) {
    if (args != null) {
      splashState = ((SplashSceneStates) args[0]);

      if (args.length > 1 && args[1] instanceof GameSave) {
        gameSave = ((GameSave) args[1]);
      }
    }

    messagesUsed = Sets.newHashSet();

    Label titleLabel = FontManager.Roboto64.makeLabel("Droid Towers");
    titleLabel.setAlignment(Align.CENTER);
    titleLabel.y = getStage().centerY() * 1.66f;
    centerHorizontally(titleLabel);
    addActor(titleLabel);

    loadingMessage = FontManager.Roboto32.makeLabel(selectRandomMessage());
    loadingMessage.setAlignment(Align.CENTER);
    center(loadingMessage);
    addActor(loadingMessage);

    progressLabel = FontManager.Roboto64.makeLabel(null);
    progressLabel.setAlignment(Align.CENTER);
    centerHorizontally(progressLabel);
    progressLabel.y = scale(100);
    addActor(progressLabel);

    happyDroid = new GameObject(new Texture("happy-droid.png"));
    happyDroid.setPosition(-happyDroid.getWidth() / 2, -happyDroid.getHeight() / 2);

    Tween.to(happyDroid, GameObjectAccessor.OPACITY, 500).target(0f).repeatYoyo(-1, 0).start(TweenSystem.getTweenManager());
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
    verifyFilesPreloaded();

    boolean assetManagerFinished = assetManager().update();

    if (splashState == FULL_LOAD) {
      if (assetManagerFinished) {
        if (gameSave != null) {
          TowerGame.changeScene(TowerScene.class, gameSave);
        } else {
          TowerGame.popScene();
        }
      }
    }

    if (!assetManagerFinished) {
      int progress = (int) (assetManager().getProgress() * 100f);
      progressLabel.setText(progress + "%");

      if (progress - progressLastChanged >= 33) {
        progressLastChanged = progress;
        loadingMessage.setText(selectRandomMessage());
      }

      getSpriteBatch().begin();
      happyDroid.draw(getSpriteBatch());
      getSpriteBatch().end();
    }

    Thread.yield();
  }

  private void verifyFilesPreloaded() {
    Set<String> preloadFiles = TowerAssetManagerFilesList.preloadFiles.keySet();

    boolean hasFilesToPreload = false;
    for (String preloadFile : preloadFiles) {
      if (!assetManager().isLoaded(preloadFile)) {
        hasFilesToPreload = true;
      }
    }

    if (splashState == PRELOAD_ONLY) {
      if (!hasFilesToPreload) {
        TowerGame.changeScene(MainMenuScene.class);
      }
    }
  }

  @Override
  public void dispose() {
  }
}
