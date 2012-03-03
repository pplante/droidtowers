package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Scaling;

public class MainMenu extends Window {
  public MainMenu() {
    super(HeadsUpDisplay.instance().getGuiSkin());
    visible = false;
    setModal(true);
    setMovable(false);

    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("hud/menus.txt"));
    Image image = new Image(atlas.findRegion("droid-towers-logo"), Scaling.fit);

    defaults().pad(4);
    row();
    add(image).center().colspan(2);

    row().pad(10);

    TextButton playGameButton = new TextButton("Play Game", HeadsUpDisplay.instance().getGuiSkin());
    playGameButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        resumeGame();
      }
    });
    add(playGameButton).center().colspan(2);
    row();

    TextButton exitButton = new TextButton("Exit Game", HeadsUpDisplay.instance().getGuiSkin());
    exitButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Gdx.app.exit();
      }
    });
    add(exitButton).bottom().left();

    add(new Image(atlas.findRegion("happy-droids-logo"), Scaling.none)).bottom().right();

    pack();
  }

  private void resumeGame() {
    ModalOverlay.instance().hide();
    markToRemove(true);
  }


  public MainMenu centerOnStage() {
    x = (stage.width() - width) / 2;
    y = (stage.height() - height) / 2;

    return this;
  }

  public MainMenu show() {
    visible = true;
    ModalOverlay.instance().show();

    HeadsUpDisplay.instance().addActor(this);

    return this;
  }
}
