package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.MoveBy;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.interpolators.OvershootInterpolator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.gui.LabelStyles;
import com.unhappyrobot.gui.NewGameWindow;
import com.unhappyrobot.utils.Platform;

public class MainMenuScene extends Scene {
  private SplashCloudLayer cloudLayer;

  @Override
  public void create() {
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

    happyDroidsLogo.action(ScaleTo.$(1f, 1f, 0.55f)
                                   .setInterpolator(OvershootInterpolator.$(1.75f)));

    container.pack();
    addActor(container);
    center(container);

    container.color.a = 0f;

    container.action(FadeIn.$(0.85f));
    container.action(Sequence.$(
                                       MoveBy.$(0, -container.height, 0f),
                                       MoveBy.$(0, container.height, 0.75f)
                                               .setInterpolator(OvershootInterpolator.$(3f)
                                               )));

    cloudLayer = new SplashCloudLayer();


    newGameButton.setClickListener(new ClickListener() {
      public void click(final Actor actor, float x, float y) {
        NewGameWindow window = new NewGameWindow("Start a new Tower", getStage(), getGuiSkin());
        window.modal(true).show().centerOnStage();
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

  @Override
  public void dispose() {
  }
}
