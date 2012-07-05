/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.badlogic.gdx.utils.Scaling.none;

public class PurchaseAppreciationDialog extends Dialog {
  public PurchaseAppreciationDialog() {
    super();
    setTitle("You are the best!");

    Table content = new Table();
    content.defaults().left();
    content.row().fill();
    AnimatedImage droidAnim = new AnimatedImage(TowerAssetManager.textureAtlas("happy-droid.txt").findRegions("happy-droid"), 0.05f, true);
    droidAnim.delayAfterPlayback(2f);
    droidAnim.setAlign(Align.RIGHT);
    droidAnim.setScaling(none);
    droidAnim.layout();
    content.add(FontManager.RobotoBold18.makeLabel("Thank you for purchasing Droid Towers: Unlimited!\n\nAs promised, all limitations have been lifted!\n\nHappy Building,\nHappy Droids Team")).expand();
    content.add(droidAnim).width(252).height(228);

    setView(content);
  }
}
