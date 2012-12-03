/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.controls.AnimatedHappyDroid;

public class PurchaseAppreciationDialog extends Dialog {
  public PurchaseAppreciationDialog() {
    super();
    setTitle("You are the best!");

    Table content = new Table();
    content.defaults().left();
    content.row().fill();
    AnimatedHappyDroid happyDroidImage = new AnimatedHappyDroid();
    Image santaDroid = TowerAssetManager.image("droid-santa-hat.png");
//    santaDroid.setScale((getStage().getHeight() * 0.33f) / santaDroid.getHeight());
//    santaDroid.setPosition(getStage().getWidth() / 2, 0);

    content.add(FontManager.RobotoBold18
        .makeLabel("Thank you for purchasing Droid Towers: Unlimited!\n\nAs promised, all limitations have been lifted!\n\nHappy Building,\nHappy Droids Team"))
        .expand();
    content.add(santaDroid).width(252).height(226);

    setView(content);
  }
}
