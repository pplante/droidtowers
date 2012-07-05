/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.entities.CommercialSpace;

import static com.happydroids.droidtowers.platform.Display.scale;

public class CommercialSpacePopOver extends GridObjectPopOver<CommercialSpace> {
  private final StarRatingBar crimeBar;
  private final StarRatingBar employmentBar;


  public CommercialSpacePopOver(final CommercialSpace commercialSpace) {
    super(commercialSpace);

    crimeBar = makeStarRatingBar("Crime");
    employmentBar = makeStarRatingBar("Employment");

    TransparentTextButton manageButton = FontManager.Default.makeTransparentButton("Manage", Color.CLEAR, Colors.ICS_BLUE);
    manageButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ManageCommercialSpaceDialog(commercialSpace).show();
      }
    });
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

    crimeBar.setValue(gridObject.getCrimeLevel() * 5f);
    employmentBar.setValue(gridObject.getEmploymentLevel() * 5f);
  }
}
