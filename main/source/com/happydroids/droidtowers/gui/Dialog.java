/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import java.util.List;

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
  private boolean viewPadding;

  public Dialog() {
    this(DroidTowersGame.getRootUiStage());
  }

  public Dialog(Stage stage) {
    super();
    this.setStage(stage);
    setTouchable(Touchable.enabled);
    hideButtons = false;
    viewPadding = true;

    buttonBar = new ButtonBar();

    modalNoiseTexture = TowerAssetManager.texture("swatches/modal-noise.png");
    modalNoiseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    setBackground(TowerAssetManager.ninePatchDrawable("hud/dialog-bg.png", Color.WHITE, 1, 1, 1, 1));

    youCantTouchThis = new TouchSwallower();
    youCantTouchThis.setWidth(getStage().getWidth());
    youCantTouchThis.setHeight(getStage().getHeight());

    dismissInputCallback = new InputCallback() {
      @Override
      public boolean run(float timeDelta) {
        dismiss();
        return true;
      }
    };

    addListener(new InputEventBlackHole());
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

    getStage().addActor(youCantTouchThis);
    getStage().addActor(this);

    defaults().top().left();

    getColor().a = 0f;
    addAction(Actions.fadeIn(0.25f));

    if (title != null) {
      add(FontManager.Default.makeLabel(title, Colors.ICS_BLUE)).pad(Display.devicePixel(6));
      row().fillX();
      add(new HorizontalRule()).expandX();
    }

    int padSide = Display.devicePixel(32);
    int padTop = Display.devicePixel(20);
    if (view != null) {
      row().fill();
      Cell viewCell = add(view).center().expand();

      if (viewPadding) {
        viewCell.pad(padTop, padSide, padTop, padSide);
      }
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

    setX((getStage().getWidth() / 2) - (getWidth() / 2));
    setY((getStage().getHeight() / 2) - (getHeight() / 2));

    if (!hideButtons) {
      InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, dismissInputCallback);
    }

    return this;
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    drawModalNoise(batch);

    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    batch.setColor(Color.WHITE);
    super.drawBackground(batch, parentAlpha);
  }

  protected void drawModalNoise(SpriteBatch batch) {
    batch.setColor(1, 1, 1, 0.45f * getColor().a);
    batch.draw(modalNoiseTexture, 0, 0, getStage().getWidth(), getStage().getHeight(), 0, 0, getStage().getWidth() / modalNoiseTexture
                                                                                                                             .getWidth(), getStage()
                                                                                                                                                  .getHeight() / modalNoiseTexture
                                                                                                                                                                         .getHeight());
  }

  public Dialog addButton(String buttonText, final OnClickCallback clickCallback) {
    return addButton(buttonText, new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
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

    addAction(Actions.sequence(Actions.fadeOut(0.125f), Actions.run(new Runnable() {
      @Override
      public void run() {
        remove();
      }
    })));

    youCantTouchThis.remove();

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

  protected void clearButtons() {
    buttons.clear();
  }

  protected void useViewPadding(boolean b) {
    viewPadding = b;
  }

  protected void addButton(Button button) {
    buttonBar.addButton(button);
  }
}
