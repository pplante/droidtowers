package com.unhappyrobot.scenes;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.gui.LabelStyles;

public class SplashScene extends Scene {
  private static final int SPLASH_DURATION = 50;

  private long launchTime;
  private CloudLayer cloudLayer;

  @Override
  public void create() {
    addModalBackground();

    Label label = LabelStyles.BankGothic64.makeLabel("Droid Towers");
    label.setAlignment(Align.CENTER);
    label.width = getStage().width();
    label.y = getStage().centerY() * 1.66f;
    addActor(label);

    Label loadingMessage = LabelStyles.Default.makeLabel("Reticulating Splines...");
    loadingMessage.setAlignment(Align.CENTER);
    loadingMessage.width = getStage().width();
    loadingMessage.y = getStage().centerY();
    addActor(loadingMessage);

    launchTime = System.currentTimeMillis() + SPLASH_DURATION;
    cloudLayer = new SplashCloudLayer();
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    cloudLayer.update(deltaTime);
    cloudLayer.render(getSpriteBatch(), getCamera());

    if (launchTime <= System.currentTimeMillis()) {
      TowerGame.changeScene(MainMenuScene.class);
//      TowerGame.changeScene(TowerScene.class);
    }
  }

  @Override
  public void dispose() {
  }
}
