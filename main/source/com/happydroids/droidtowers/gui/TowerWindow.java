/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.utils.StringUtils;

import static com.happydroids.droidtowers.ColorUtil.rgba;
import static com.happydroids.droidtowers.gui.FontManager.Roboto18;
import static com.happydroids.droidtowers.gui.FontManager.Roboto32;

public class TowerWindow {
  private static final int[] DIALOG_CLOSE_KEYCODES = new int[]{InputSystem.Keys.ESCAPE, InputSystem.Keys.BACK};

  private InputCallback closeDialogCallback;
  private Runnable dismissCallback;
  protected final Stage stage;
  protected Table content;
  protected Table wrapper;
  private final Label titleLabel;
  protected final TransparentTextButton closeButton;
  private Actor staticHeaderContent;
  private Actor staticFooterContent;
  private final Cell actionBarCell;
  private final Cell footerBarCell;
  private final Cell contentRow;
  protected final VerticalRule closeButtonLine;

  public TowerWindow(String title, Stage stage) {
    this.stage = stage;

    wrapper = new Table() {
      //      TODO: GROT, I have no clue why this texture is blending with the stuff behind it..
      @Override
      protected void drawBackground(SpriteBatch batch, float parentAlpha) {
        batch.disableBlending();
        super.drawBackground(batch, 1f);
        batch.enableBlending();
      }
    };
    wrapper.setFillParent(true);
    wrapper.defaults().top().left();
    wrapper.setTouchable(Touchable.enabled);

    Texture texture = TowerAssetManager.texture("hud/window-bg.png");
    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    wrapper.setBackground(new NinePatchDrawable(new NinePatch(texture)));
    wrapper.size((int) stage.getWidth(), (int) stage.getHeight());

    titleLabel = Roboto32.makeLabel(StringUtils.truncate(title, 40));
    closeButton = Roboto18.makeTransparentButton("< back", rgba("#007399"), Colors.DARK_GRAY);
    closeButtonLine = new VerticalRule(Display.devicePixel(2));

    Table topBar = new Table();
    topBar.row().fill();
    topBar.add(closeButton).fill();
    topBar.add(closeButtonLine).fillY();
    topBar.add(titleLabel).center().left().expand().pad(Display.devicePixel(4)).padLeft(Display.devicePixel(12));

    wrapper.add(topBar).fill();

    wrapper.row().fillX();
    wrapper.add(new HorizontalRule(Display.devicePixel(2))).expandX();

    wrapper.row().fillX();
    actionBarCell = wrapper.add();

    contentRow = wrapper.row();
    padding(24);
    wrapper.add(makeContentContainer()).expand();

    wrapper.row().fillX();
    footerBarCell = wrapper.add();

    closeButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
      }
    });

    wrapper.addListener(new InputEventBlackHole());
  }

  protected void padding(final int pixels) {
    contentRow.fill().padLeft(Display.devicePixel(pixels)).padRight(Display.devicePixel(pixels));
  }

  protected Actor makeContentContainer() {
    content = new Table();
    content.defaults().top().left();
    content.row().fill();

    return content;
  }

  public Cell add(Actor actor) {
    return content.add(actor);
  }

  public Cell row() {
    return content.row();
  }

  public TowerWindow show() {
    bindKeys();

    wrapper.invalidate();
    wrapper.pack();
    wrapper.setColor(Color.WHITE);
    stage.addActor(wrapper);

    return this;
  }

  public void dismiss() {
    wrapper.setVisible(false);
    unbindKeys();

    if (dismissCallback != null) {
      dismissCallback.run();
    }

    stage.setScrollFocus(null);
    stage.setKeyboardFocus(null);

    wrapper.remove();
  }

  public TowerWindow setDismissCallback(Runnable dismissCallback) {
    this.dismissCallback = dismissCallback;

    return this;
  }

  protected void debug() {
    content.debug();
  }

  protected void clear() {
    content.clear();
  }

  protected void bindKeys() {
    closeDialogCallback = new InputCallback() {
      public boolean run(float timeDelta) {
        TowerWindow.this.dismiss();
        return true;
      }
    };

    InputSystem.instance().bind(DIALOG_CLOSE_KEYCODES, closeDialogCallback);
  }

  protected void unbindKeys() {
    if (closeDialogCallback != null) {
      InputSystem.instance().unbind(DIALOG_CLOSE_KEYCODES, closeDialogCallback);
      closeDialogCallback = null;
    }
  }

  public TowerWindow setTitle(String title) {
    titleLabel.setText(title);

    return this;
  }

  protected Cell defaults() {
    return content.defaults();
  }

  protected Cell add() {
    return content.add();
  }

  public void setStaticHeader(Actor staticContent) {
    this.staticHeaderContent = staticContent;
    actionBarCell.setWidget(staticContent);
    actionBarCell.expandX();
    wrapper.pack();
  }

  public void setStaticFooter(Actor staticContent) {
    this.staticFooterContent = staticContent;
    footerBarCell.setWidget(staticContent);
    footerBarCell.expandX();
    wrapper.pack();
  }

  protected Cell addHorizontalRule(Color darkGray, int desiredHeight, int colspan) {
    row().fillX();
    return add(new HorizontalRule(darkGray, desiredHeight)).expandX().colspan(colspan);
  }

  protected Cell addLabel(String labelText, FontHelper labelFont, Color fontColor) {
    row();
    return add(labelFont.makeLabel(labelText, fontColor));
  }

  public Cell addLabel(String text, FontHelper labelFont) {
    return addLabel(text, labelFont, Color.WHITE);
  }

  public Table getContent() {
    return content;
  }
}
