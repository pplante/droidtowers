/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;

import java.util.List;

import static com.happydroids.droidtowers.ColorUtil.rgba;
import static com.happydroids.droidtowers.platform.Display.scale;

public class Dialog extends Table {
  private String title;
  private String message;
  private final NinePatch dropShadowPatch;
  private final TiledImage modalNoise;
  private List<TextButton> buttons;
  private Runnable dismissCallback;
  private InputCallback dismissInputCallback;

  public Dialog(Stage stage) {
    super();
    this.stage = stage;
    touchable = true;

    buttons = Lists.newArrayList();

    dropShadowPatch = TowerAssetManager.ninePatch("swatches/drop-shadow.png", Color.WHITE, 22, 22, 22, 22);
    modalNoise = new TiledImage(TowerAssetManager.texture("swatches/modal-noise.png"));
    modalNoise.touchable = true;
    modalNoise.color.a = 0.5f;
    modalNoise.x = 0;
    modalNoise.y = 0;
    modalNoise.width = getStage().width();
    modalNoise.height = getStage().height();
    modalNoise.layout();
    modalNoise.setClickListener(new ClickListener() {
      @Override
      public void click(Actor actor, float x, float y) {

      }
    });

    dismissInputCallback = new InputCallback() {
      @Override
      public boolean run(float timeDelta) {
        dismiss();
        return true;
      }
    };
  }

  public Dialog() {
    this(TowerGame.getRootUiStage());
  }

  public Dialog setTitle(String title) {
    this.title = title;

    return this;
  }

  public Dialog setMessage(String message) {
    this.message = message;

    return this;
  }

  public void show() {
    clearActions();
    clear();
    stage.addActor(modalNoise);
    stage.addActor(this);

    defaults().top().left();

    setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.DARK_GRAY));

    add(FontManager.Default.makeLabel(title, Colors.ICS_BLUE)).pad(scale(6));

    row().fillX();
    add(new HorizontalRule()).expandX();

    row();
    int padSide = scale(32);
    int padTop = scale(20);
    add(FontManager.Roboto18.makeLabel(message, Color.WHITE)).pad(padTop, padSide, padTop, padSide);

    row().fillX();
    add(new HorizontalRule(Color.GRAY, 1));

    if (buttons.isEmpty()) {
      addButton("Dismiss", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
        }
      });
    }

    Table buttonBar = new Table();
    buttonBar.row().fillX();
    for (int i = 0; i < buttons.size(); i++) {
      buttonBar.add(buttons.get(i)).expandX().uniformX();

      if (i < buttons.size() - 1) {
        buttonBar.add(new VerticalRule(Color.GRAY, 1)).width(1);
      }
    }

    row().fillX();
    add(buttonBar).expandX();
    pack();

    x = getStage().centerX() - width / 2;
    y = getStage().centerY() - height / 2;

    InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, dismissInputCallback);
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    super.touchDown(x, y, pointer);

    return true;
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    if (this.dropShadowPatch != null) {
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      this.dropShadowPatch.draw(batch, x - dropShadowPatch.getLeftWidth(), y - dropShadowPatch.getTopHeight(), width + dropShadowPatch.getRightWidth() + dropShadowPatch.getLeftWidth(), height + dropShadowPatch.getBottomHeight() + dropShadowPatch.getTopHeight());
    }

    super.drawBackground(batch, parentAlpha);
  }

  public Dialog addButton(String buttonText, final OnClickCallback clickCallback) {
    TransparentTextButton button = FontManager.Roboto18.makeTransparentButton(buttonText, Colors.DARK_GRAY, rgba("#007399"));
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        clickCallback.onClick(Dialog.this);
      }
    });

    buttons.add(button);

    return this;
  }

  public void dismiss() {
    InputSystem.instance().unbind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, dismissInputCallback);

    markToRemove(true);
    modalNoise.action(FadeOut.$(0.125f).setCompletionListener(new OnActionCompleted() {
      @Override
      public void completed(Action action) {
        modalNoise.markToRemove(true);
      }
    }));

    if (dismissCallback != null) {
      dismissCallback.run();
    }
  }

  public Dialog setDismissCallback(Runnable dismissCallback) {
    this.dismissCallback = dismissCallback;

    return this;
  }
}
