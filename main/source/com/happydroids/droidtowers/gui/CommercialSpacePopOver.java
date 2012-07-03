/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.entities.CommercialSpace;

import static com.happydroids.droidtowers.platform.Display.scale;

public class CommercialSpacePopOver extends GridObjectPopOver {
  private final StarRatingBar crimeBar;
  private final StarRatingBar employmentBar;


  public CommercialSpacePopOver(CommercialSpace commercialSpace) {
    super(commercialSpace);

    crimeBar = makeStarRatingBar("Crime");
    employmentBar = makeStarRatingBar("Employment");

    TransparentTextButton manageButton = FontManager.Default.makeTransparentButton("Manage", Color.CLEAR, Colors.ICS_BLUE);

    Table buttonBar = new Table();
    buttonBar.defaults();

    buttonBar.row().fillX();
    buttonBar.add(new HorizontalRule(Color.GRAY, 1)).colspan(3).height(1);

    buttonBar.row().fillX();
    buttonBar.add(manageButton).expandX().uniformX();
    buttonBar.pack();

    row().fillX().pad(scale(-8)).padTop(scale(16));
    add(buttonBar).expandX().minWidth(200);
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    crimeBar.setValue(room.getCrimeLevel() * 5f);
    employmentBar.setValue(((CommercialSpace) room).getEmploymentLevel() * 5f);
  }
}
