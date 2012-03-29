package com.unhappyrobot.scenes;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.unhappyrobot.TowerAssetManager;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.gui.LabelStyle;

public class SplashScene extends Scene {
  private Label progressBar;

  @Override
  public void create(Object... args) {
    addModalBackground();

    Label titleLabel = LabelStyle.BankGothic64.makeLabel("Droid Towers");
    titleLabel.setAlignment(Align.CENTER);
    titleLabel.y = getStage().centerY() * 1.66f;
    centerHorizontally(titleLabel);
    addActor(titleLabel);

    Label loadingMessage = LabelStyle.Default.makeLabel("reticulating splines...");
    loadingMessage.setAlignment(Align.CENTER);
    center(loadingMessage);
    addActor(loadingMessage);

    progressBar = LabelStyle.BankGothic64.makeLabel(null);
    progressBar.setAlignment(Align.CENTER);
    centerHorizontally(progressBar);
    progressBar.y = loadingMessage.y - 20;
    addActor(progressBar);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    boolean assetManagerFinished = TowerAssetManager.assetManager().update();

    String progressText = String.format("%.1f%%", (TowerAssetManager.assetManager().getProgress() * 100f));
    progressBar.setText(progressText);

    if (assetManagerFinished) {
      TowerGame.changeScene(MainMenuScene.class);
    }
  }

  @Override
  public void dispose() {
  }
}
