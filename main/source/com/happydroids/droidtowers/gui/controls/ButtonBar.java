/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.HorizontalRule;
import com.happydroids.droidtowers.gui.VerticalRule;
import com.happydroids.droidtowers.gui.VibrateClickListener;

import java.util.List;

public class ButtonBar extends Table {
  private List<TextButton> buttons;

  public ButtonBar() {
    super();

    buttons = Lists.newArrayList();
  }

  public ButtonBar addButton(String labelText, VibrateClickListener clickListener) {
    TextButton button = FontManager.Roboto18.makeTransparentButton(labelText, Color.CLEAR, Colors.ICS_BLUE);
    button.setClickListener(clickListener);

    buttons.add(button);

    updateLayout();

    return this;
  }

  private void updateLayout() {
    clear();
    row().fillX();
    add(new HorizontalRule(Color.GRAY, 1)).height(1).fillX().colspan(buttons.size() * 2 - 1);
    row().fillX();

    int numButtons = buttons.size();
    for (int i = 0; i < numButtons; i++) {
      add(buttons.get(i)).expandX().uniformX();

      if (i < numButtons - 1) {
        add(new VerticalRule(Color.GRAY, 1)).width(1).fillY();
      }
    }
  }

  public int getButtonCount() {
    return buttons.size();
  }
}
