/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import java.util.List;

import static com.happydroids.droidtowers.platform.Display.scale;

public class Dialog extends Table {
  private String title;
  private String message;
  private List<TextButton> buttons;
  private Runnable dismissCallback;
  private InputCallback dismissInputCallback;
  private Actor view;
  private boolean hideButtons;
  private Texture modalNoiseTexture;
  private Group youCantTouchThis;
  private ButtonBar buttonBar;

  public Dialog() {
    this(DroidTowersGame.getRootUiStage());
  }

  public Dialog(Stage stage) {
    super();
    this.stage = stage;
    touchable = true;
    hideButtons = false;

    buttonBar = new ButtonBar();

    modalNoiseTexture = TowerAssetManager.texture("swatches/modal-noise.png");
    modalNoiseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    setBackground(TowerAssetManager.ninePatch("hud/dialog-bg.png", Color.WHITE, 1, 1, 1, 1));

    youCantTouchThis = new TouchSwallower();
    youCantTouchThis.width = getStage().width();
    youCantTouchThis.height = getStage().height();

    dismissInputCallback = new InputCallback() {
      @Override
      public boolean run(float timeDelta) {
        dismiss();
        return true;
      }
    };
  }

  public Dialog setTitle(String title) {
    this.title = title;

    return this;
  }

  public Dialog setMessage(String message) {
    this.message = message;

    return this;
  }

  public Dialog show() {
    clearActions();
    clear();

    stage.addActor(youCantTouchThis);
    stage.addActor(this);

    defaults().top().left();

    color.a = 0f;
    action(FadeIn.$(0.25f));

    if (title != null) {
      add(FontManager.Default.makeLabel(title, Colors.ICS_BLUE)).pad(scale(6));
      row().fillX();
      add(new HorizontalRule()).expandX();
    }

    int padSide = scale(32);
    int padTop = scale(20);
    if (view != null) {
      row();
      add(view).pad(padTop, padSide, padTop, padSide).center();
    }

    if (message != null) {
      row().pad(padTop, padSide, padTop, padSide);
      add(FontManager.Roboto18.makeLabel(message, Color.WHITE));
    }

    if (!hideButtons) {
      if (buttonBar.getButtonCount() == 0) {
        addButton("Dismiss", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            dialog.dismiss();
          }
        });
      }

      row().fillX();
      add(buttonBar).expandX();
    }

    pack();

    x = getStage().centerX() - width / 2;
    y = getStage().centerY() - height / 2;

    if (!hideButtons) {
      InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, dismissInputCallback);
    }

    return this;
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    super.touchDown(x, y, pointer);

    return true;
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    batch.setColor(1, 1, 1, 0.45f * color.a);
    batch.draw(modalNoiseTexture, 0, 0, getStage().width(), getStage().height(), 0, 0, getStage().width() / modalNoiseTexture.getWidth(), getStage().height() / modalNoiseTexture.getHeight());

    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    batch.setColor(Color.WHITE);
    super.drawBackground(batch, parentAlpha);
  }

  public Dialog addButton(String buttonText, final OnClickCallback clickCallback) {
    return addButton(buttonText, new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        clickCallback.onClick(Dialog.this);
      }
    });
  }

  public Dialog addButton(String buttonText, VibrateClickListener clickListener) {
    buttonBar.addButton(buttonText, clickListener);

    return this;
  }

  public void dismiss() {
    InputSystem.instance().unbind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, dismissInputCallback);


    action(FadeOut.$(0.125f).setCompletionListener(new OnActionCompleted() {
      @Override
      public void completed(Action action) {
        markToRemove(true);
      }
    }));

    youCantTouchThis.markToRemove(true);

    if (dismissCallback != null) {
      dismissCallback.run();
    }
  }

  public Dialog setDismissCallback(Runnable dismissCallback) {
    this.dismissCallback = dismissCallback;

    return this;
  }

  public void setView(Actor view) {
    this.view = view;
  }

  public Dialog hideButtons(boolean cancelable) {
    this.hideButtons = cancelable;

    return this;
  }
}
