/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerAssetManagerFilesList;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

import java.util.Set;

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
                                                              "downloading pictures of cats",
                                                              "so, uhh...how are you?",
                                                              "its going to be\na beautiful day!",
                                                              "de-fuzzing logic pathways",
                                                              "cleaning the tubes",
  };
  private Label progressBar;
  private Label loadingMessage;
  private boolean selectedNewMessage;
  private Sprite happyDroid;

  @Override
  public void create(Object... args) {
//    addModalBackground();

    Label titleLabel = FontManager.BankGothic64.makeLabel("Droid Towers");
    titleLabel.setAlignment(Align.CENTER);
    titleLabel.y = getStage().centerY() * 1.66f;
    centerHorizontally(titleLabel);
    addActor(titleLabel);

    loadingMessage = FontManager.BankGothic32.makeLabel(selectRandomMessage());
    loadingMessage.setAlignment(Align.CENTER);
    center(loadingMessage);
    addActor(loadingMessage);

    progressBar = FontManager.BankGothic64.makeLabel(null);
    progressBar.setAlignment(Align.CENTER);
    centerHorizontally(progressBar);
    progressBar.y = 100;
    addActor(progressBar);

    happyDroid = new GameObject(new Texture("happy-droid.png"));
    happyDroid.setPosition(-happyDroid.getWidth() / 2, -happyDroid.getHeight() / 2);

    Tween.to(happyDroid, GameObjectAccessor.OPACITY, 500).target(0f).repeatYoyo(-1, 0).start(TweenSystem.getTweenManager());
  }

  private String selectRandomMessage() {
    return STRINGS[Random.randomInt(STRINGS.length - 1)];
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    Set<String> preloadFiles = TowerAssetManagerFilesList.preloadFiles.keySet();

    boolean hasFilesToPreload = false;
    for (String preloadFile : preloadFiles) {
      if (!TowerAssetManager.assetManager().isLoaded(preloadFile)) {
        hasFilesToPreload = true;
      }
    }


    boolean assetManagerFinished = TowerAssetManager.assetManager().update();

    int progress = (int) (TowerAssetManager.assetManager().getProgress() * 100f);
    String progressText = String.format("%d%%", progress);
    progressBar.setText(progressText);

    if (progress >= 50 && !selectedNewMessage) {
      loadingMessage.setText(selectRandomMessage());
      selectedNewMessage = true;
    }

    if (!hasFilesToPreload) {
      TowerGame.changeScene(MainMenuScene.class);
    }

    getSpriteBatch().begin();
    happyDroid.draw(getSpriteBatch());
    getSpriteBatch().end();
  }

  @Override
  public void dispose() {
  }
}
