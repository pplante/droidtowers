/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.gui.controls.AnimatedHappyDroid;

public class PurchaseAppreciationDialog extends Dialog {
  public PurchaseAppreciationDialog() {
    super();
    setTitle("You are the best!");

    Table content = new Table();
    content.defaults().left();
    content.row().fill();
    AnimatedHappyDroid happyDroidImage = new AnimatedHappyDroid();
    happyDroidImage.setScale((getStage().getHeight() * 0.33f) / happyDroidImage.getHeight());
    happyDroidImage.setPosition(getStage().getWidth() / 2, 0);

    content.add(FontManager.RobotoBold18.makeLabel("Thank you for purchasing Droid Towers: Unlimited!\n\nAs promised, all limitations have been lifted!\n\nHappy Building,\nHappy Droids Team")).expand();
    content.add(happyDroidImage).width(252).height(228);

    setView(content);
  }
}
