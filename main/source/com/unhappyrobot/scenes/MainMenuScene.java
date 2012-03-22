package com.unhappyrobot.scenes;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.gui.LabelStyles;
import com.unhappyrobot.gui.TowerWindow;
import com.unhappyrobot.gui.WidgetAccessor;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.utils.Platform;

public class MainMenuScene extends Scene {
  private SplashCloudLayer cloudLayer;

  @Override
  public void create() {
    addModalBackground();

    Table container = new Table(getGuiSkin());
    container.defaults().center().left();

    Label label = LabelStyles.BankGothic64.makeLabel("Droid Towers");
    container.add(label).align(Align.CENTER);
    container.row();

    Label loadingMessage = LabelStyles.Default.makeLabel("Reticulating Splines...");
    container.add(loadingMessage).align(Align.RIGHT);
    container.row().padTop(50);

    TextButton newGameButton = new TextButton("new game", getGuiSkin());
    container.add(newGameButton).fill().maxWidth(150);
    container.row().padTop(15);

    TextButton loadGameButton = new TextButton("load game", getGuiSkin());
    container.add(loadGameButton).fill().maxWidth(150);
    container.row().padTop(60);

    TextButton optionsButton = new TextButton("options", getGuiSkin());
    container.add(optionsButton).fill().maxWidth(150);
    container.row();


    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("hud/menus.txt"));
    Image happyDroidsLogo = new Image(atlas.findRegion("happy-droids-logo"));
    happyDroidsLogo.setAlign(Align.CENTER);
    happyDroidsLogo.x = getStage().width() - happyDroidsLogo.width - 5;
    happyDroidsLogo.y = 5;
    happyDroidsLogo.scaleX = happyDroidsLogo.scaleY = 0f;
    happyDroidsLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Platform.launchWebBrowser("http://www.happydroids.com");
      }
    });
    addActor(happyDroidsLogo);

    Tween.to(happyDroidsLogo, WidgetAccessor.SCALE, 1000)
            .delay(250)
            .ease(Bounce.OUT)
            .target(1f, 1f)
            .start(TweenSystem.getTweenManager());

    container.pack();
    addActor(container);
    center(container);

    container.color.a = 0f;

    Tween.to(container, WidgetAccessor.OPACITY, 550)
            .delay(150)
            .ease(Quad.INOUT)
            .target(1f)
            .start(TweenSystem.getTweenManager());

    cloudLayer = new SplashCloudLayer();


    newGameButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
//        TowerGame.changeScene(TowerScene.class);
        TowerWindow window = new TowerWindow("Start a new Tower", getStage(), getGuiSkin());
        window.add(LabelStyles.Default.makeLabel("Please provide a name for your Tower:"));
        window.row();

        TextField nameField = new TextField("", "test", getGuiSkin());
        window.add(nameField);
        window.debug();
        window.show().centerOnStage();
      }
    });
  }

  private void center(Actor actor) {
    centerHorizontally(actor);
    centerVertically(actor);
  }

  private void centerVertically(Actor actor) {
    actor.y = (getStage().height() - actor.height) / 2;
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
  }
}
